package Screens;

import Bullets.Bullet;
import Bullets.BulletManager;
import InputHandlers.PlayScreenInputHandler;
import Opponents.Boss;
import Opponents.Monster;
import Opponents.Opponent;
import Opponents.Robot;
import Scenes.DeathScene;
import Scenes.Debug;
import Scenes.ExitToMainMenu;
import Scenes.HUD;
import Sprites.OtherPlayer;
import Sprites.Player;
import Tools.B2WorldCreator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;
import crystals.Crystal;
import serializableObjects.OpponentData;
import serializableObjects.OpponentDataMap;
import serializableObjects.PlayerData;
import serializableObjects.PlayerLeavesTheWorld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class PlayScreen implements Screen {
    public final MyGDXGame game;
    public static OrthographicCamera gameCam = new OrthographicCamera();
    private final Viewport gamePort;
    private final TiledMap map;
    private TmxMapLoader mapLoader = new TmxMapLoader();
    private final OrthogonalTiledMapRenderer renderer;
    public World world;
    private final Box2DDebugRenderer b2dr;
    public Player player;
    private final TextureAtlas playerAtlas = new TextureAtlas("player/player_spritesheet.atlas");
    private final TextureAtlas robotAtlas = new TextureAtlas("Opponents/Robot.atlas");
    private final TextureAtlas bossAtlas = new TextureAtlas("Opponents/boss.atlas");
    private final TextureAtlas monsterAtlas = new TextureAtlas("Opponents/monster.atlas");
    private float prevPosX = 0;
    private float prevPosY = 0;
    private int prevFrameIndex = 0;
    private Boolean prevRunningRight = null;

    public float startPosX;
    public float startPosY;
    private Debug debug;
    public HUD hud;
    public BulletManager bulletManager;
    public List<String> opponentIds = new ArrayList<>();
    public HashMap<String, Opponent> opponents = new HashMap<>();
    public OpponentDataMap opponentDataMap;
    private final PlayScreenInputHandler handler;
    private final B2WorldCreator b2WorldCreator;
    public Set<String> destroyedOpponents = new HashSet<>();
    public Set<String> allDestroyedOpponents = new HashSet<>();
    public Set<String> allDestroyedPlayers = new HashSet<>();
    private final Music music;
    public String worldUUID;
    public List<Crystal> crystals = new ArrayList<>();
    public final ExitToMainMenu pauseDialog;
    private final MenuScreen menuScreen;
    public final DeathScene deathScene;
    private int deathSceneCounter = 0;
    private static final int ROBOT_ID = 1;
    private static final int BOSS_ID = 2;
    private static final int MONSTER_ID = 3;
    private static final int MOB_SPAWN_INTERVAL = 5; // a mob is spawned each 5 seconds
    private int mobSpawnIntervalCounter = 0;
    private static final int MOB_PER_PLAYER = 10; // 10 mobs per player
    private static final int TIME_FOR_PLAYERS_TO_CHILL_AT_THE_BEGINNING = 30; // players have 30 seconds at
    // the beginning when no mobs are spawned
    private int chillTimeCounter = 0;

    /**
     * Constructor for the PlayScreen.
     *
     * @param game The Game instance representing the main game.
     * @param menu
     */
    public PlayScreen(MyGDXGame game, String worldUUID, MenuScreen menu) {
        this.menuScreen = menu;
        this.game = game;
        this.worldUUID = worldUUID;
        opponentDataMap = new OpponentDataMap(worldUUID);
        gamePort = new FitViewport(MyGDXGame.V_WIDTH / MyGDXGame.PPM, MyGDXGame.V_HEIGHT / MyGDXGame.PPM, gameCam);

        map = mapLoader.load("level/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MyGDXGame.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true); // Set gravity as null as we have a top-down game
        b2dr = new Box2DDebugRenderer();
        b2WorldCreator = new B2WorldCreator(world, map, this);

        player = new Player(world, this);

        debug = new Debug(game.batch, player);
        hud = new HUD(game.batch, player);
        pauseDialog = new ExitToMainMenu(game.batch, this);
        deathScene = new DeathScene(game.batch, this, this.player);

        // Initialize BulletManager
        bulletManager = new BulletManager(world);

        handler = new PlayScreenInputHandler(this);

        music = Gdx.audio.newMusic(Gdx.files.internal("Music/in-game.mp3"));

        MyGDXGame.worldUuidToScreen.put(worldUUID, this);
    }

    public void changeInputToHandler(){
        Gdx.input.setInputProcessor(handler);
    }

    /**
     * Shows the PlayScreen and sets the input processor.
     */
    @Override
    public void show() {
        changeInputToHandler();

        music.setLooping(true);
        music.setVolume(SettingsScreen.musicValue);
        music.play();
    }

    /**
     * Gets the player texture atlas used in the game.
     *
     * @return The texture atlas.
     */
    public TextureAtlas getPlayerAtlas() {
        return playerAtlas;
    }

    /**
     * Gets the robot texture atlas used in the game.
     *
     * @return The texture atlas.
     */
    public TextureAtlas getRobotAtlas() {
        return robotAtlas;
    }

    public TextureAtlas getBossAtlas() {
        return bossAtlas;
    }
    public TextureAtlas getMonsterAtlas() { return monsterAtlas; }


    /**
     * Updates the game logic.
     *
     * @param dt The time elapsed since the last frame.
     */
    public void update(float dt) throws IOException {
        world.step(1 / 60f, 6, 2);

        player.update(dt);

        // updating opponents and adding info to the opponentDataMap, which is sent to the server
        for (Map.Entry<String, Opponent> entry : opponents.entrySet()) {
            Opponent opponent = entry.getValue();


            opponentDataMap.put(entry.getKey(),
                    new OpponentData(opponent.getX(), opponent.getY(), opponent.getHealth(), opponent.getUuid(), opponent.getMobId(), opponent.getSpawnTime()));

            if (opponent instanceof Robot) {
                ((Robot) opponent).update(dt);
            } else if (opponent instanceof Boss) {
                ((Boss) opponent).update(dt);
            } else if (opponent instanceof  Monster) {
                ((Monster) opponent).update(dt);
            }

        }

        bulletManager.update(dt);
        b2WorldCreator.destroyDeadOpponents();
        b2WorldCreator.destroyDeadPlayers();

        game.client.sendTCP(opponentDataMap);

        for (String id : destroyedOpponents) {
            if (opponentDataMap.getMap().containsKey(id)) {
                if (opponentDataMap.getMap().get(id).getMob() == ROBOT_ID) {
                    float x = opponentDataMap.getMap().get(id).getX();
                    float y = opponentDataMap.getMap().get(id).getY();

                    if (Math.abs(player.getX() - x) < 1f &&
                            Math.abs(player.getY() - y) < 1f) {
                        player.takeDamage(Robot.EXPLOSION_DAMAGE);
                    }
                }

                opponents.remove(id);
                opponentIds.remove(id);
                opponentDataMap.remove(id);
            }
        }

        if (player.shouldBeDestroyed) {
            if (deathSceneCounter == 0) {
                deathScene.showStage();
                deathSceneCounter++;
            }
        }

        // opponentDataMap is constantly being updated by all client instances
        // this block of code makes new instances of the robot or boss if it is an opponent with a new ID
        HashMap<String, OpponentData> map = opponentDataMap.getMap();
        for (Map.Entry<String, OpponentData> entry : map.entrySet()) {
            String key = entry.getKey();
            OpponentData opponentData = entry.getValue();

            // Skip if opponent already exists or has been destroyed, or if health is 0
            if (opponentIds.contains(key) || destroyedOpponents.contains(key) || opponentData.getHealth() == 0) {
                continue;
            }

            // Instantiate the appropriate opponent type based on mob ID
            Opponent opponent;
            switch (opponentData.getMob()) {
                case ROBOT_ID:
                    opponent = new Robot(world, this, opponentData.getX(), opponentData.getY(),
                            opponentData.getHealth(), opponentData.getUuid());
                    break;
                case BOSS_ID:
                    opponent = new Boss(world, this, opponentData.getX(), opponentData.getY(),
                            opponentData.getHealth(), opponentData.getUuid(), opponentData.getMobSpawnTime());
                    break;
                case MONSTER_ID:
                    opponent = new Monster(world, this, opponentData.getX(), opponentData.getY(),
                            opponentData.getHealth(), opponentData.getUuid());
                    break;
                default:
                    // Handle unexpected mob ID
                    continue;
            }

            // Add the opponent to the opponents map and mark its ID as processed
            opponents.put(key, opponent);
            opponentIds.add(key);
        }


        gameCam.position.x = player.b2body.getPosition().x;
        gameCam.position.y = player.b2body.getPosition().y;

        gameCam.update();
        renderer.setView(gameCam);

        // It is used to send the position of the player to the server
        if (prevPosX != gameCam.position.x
                || prevPosY != gameCam.position.y
                || player.prevState != player.currentState
                || player.getCurrentFrameIndex() != prevFrameIndex
                || player.getIsMining()
                || prevRunningRight != player.runningRight) {

            game.client.sendTCP(new PlayerData(
                    gameCam.position.x,
                    gameCam.position.y,
                    player.getCurrentFrameIndex(),
                    player.runningRight,
                    player.getHealth(),
                    player.getUuid(),
                    worldUUID
            ));

            prevPosX = gameCam.position.x;
            prevPosY = gameCam.position.y;
            player.prevState = player.currentState;
            prevFrameIndex = player.getCurrentFrameIndex();
            prevRunningRight = player.runningRight;
        }

        System.out.println(chillTimeCounter);
        if (TIME_FOR_PLAYERS_TO_CHILL_AT_THE_BEGINNING * 60 > chillTimeCounter) {
            chillTimeCounter++;
        } else {
            if (mobSpawnIntervalCounter >= MOB_SPAWN_INTERVAL * 60
                    && opponentIds.size() <= MOB_PER_PLAYER * (game.playerDict.size() + 1)) {
                b2WorldCreator.spawnMob();
                mobSpawnIntervalCounter = 0;
            }
            mobSpawnIntervalCounter++;
        }

        destroyedOpponents.clear();
    }

    /**
     * Renders the game.
     *
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void render(float delta) {

        handler.handleInput();
        try {
            update(delta);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        // Uncomment the following line if you want to see box2d lines
        // b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        // Draw crystals
        for (Crystal crystal: crystals) {
            crystal.draw(game.batch);
        }


        // draws all opponents
        for (Map.Entry<String, Opponent> entry : opponents.entrySet()) {
            Opponent opponent = entry.getValue();
            opponent.draw(game.batch);
        }

        player.draw(game.batch); // Draw the player after rendering the physics world

        // Draw the other players within the game
        for (Map.Entry<Integer, Set<OtherPlayer>> entry : game.playerDict.entrySet()) {
            for (OtherPlayer otherPlayer : entry.getValue()) {
                otherPlayer.draw(game.batch);
            }
        }

        // Draw bullets
        for (Bullet bullet : bulletManager.getBullets()) {
            bullet.draw(game.batch);
        }

        game.batch.end();

        // rendering debug table (disabled for now)
        //debug.updateLabelValues();

        hud.updateLabelValues();
        if (pauseDialog.isToShow()) pauseDialog.renderStage();
        if (deathScene.isToShow()) {
            deathScene.renderStage();
            System.out.println("death scene has to be rendered");
        }
    }

    /**
     * Resizes the viewport.
     *
     * @param width  The new width.
     * @param height The new height.
     */
    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);

        // Clear the keys because for some reason the input processor bugs out when resizing the window
        handler.keyPresses = 0;
        handler.keysPressed.clear();

        //debug.stage.getViewport().update(width, height, true);
        hud.stage.getViewport().update(width, height, true);
        pauseDialog.stage.getViewport().update(width, height, true);
        deathScene.stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    /**
     * Changes player's screen to the main menu screen if the player dies
     */
    public void goToMenuWhenPlayerIsDead() throws IOException {
        //world.destroyBody(player.b2body);
        // game.playerDict.clear();
        // game.dispose();

        music.dispose();
        game.client.sendTCP(new PlayerLeavesTheWorld(worldUUID));

//        MyGDXGame.client.close();
//        MyGDXGame.client.dispose();

        // starts the music
        menuScreen.music.play();
        game.setScreen(menuScreen);
    }

    /**
     * Disposes of resources used by the PlayScreen.
     */
    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        playerAtlas.dispose();
        robotAtlas.dispose();
        bossAtlas.dispose();
        monsterAtlas.dispose();
        deathScene.dispose();
        pauseDialog.dispose();
    }
}
