package Screens;

import Bullets.Bullet;
import Bullets.BulletManager;
import InputHandlers.PlayScreenInputHandler;
import Opponents.Boss;
import Opponents.Monster;
import Opponents.Opponent;
import Opponents.Robot;
import Scenes.Debug;
import Scenes.ExitToMainMenu;
import Scenes.GameOverScene;
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
import serializableObjects.*;

import java.io.IOException;
import java.util.*;



public class PlayScreen implements Screen {
    public final MyGDXGame game;
    public OrthographicCamera gameCam = new OrthographicCamera();
    private final Viewport gamePort;
    private final TiledMap map;
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
    public final B2WorldCreator b2WorldCreator;
    public Set<String> destroyedOpponents = new HashSet<>();
    public Set<String> allDestroyedOpponents = new HashSet<>();
    public Set<String> allDestroyedPlayers = new HashSet<>();
    private final Music music;
    public String worldUUID;
    public List<Crystal> crystals = new ArrayList<>();
    public final ExitToMainMenu pauseDialog;
    private final MenuScreen menuScreen;
    public final GameOverScene gameOverScene;
    private int deathSceneCounter = 0;
    private static final int ROBOT_ID = 1;
    private static final int BOSS_ID = 2;
    private static final int MONSTER_ID = 3;
    private static final int MOB_SPAWN_INTERVAL = 5; // a mob is spawned each 5 seconds
    private int mobSpawnIntervalCounter = 0;
    private static final int MOB_PER_PLAYER = 10; // 10 mobs per player
    private boolean singlePlayerWorld;

    /**
     * Constructor for the PlayScreen.
     *
     * @param game The Game instance representing the main game.
     * @param menu
     */
    public PlayScreen(MyGDXGame game, String worldUUID, MenuScreen menu, int currentRound, int currentTimeInWave, boolean singlePlayerWorld) {
        this.menuScreen = menu;
        this.game = game;
        this.worldUUID = worldUUID;
        this.singlePlayerWorld = singlePlayerWorld;

        opponentDataMap = new OpponentDataMap(worldUUID);
        gamePort = new FitViewport(MyGDXGame.V_WIDTH / MyGDXGame.PPM, MyGDXGame.V_HEIGHT / MyGDXGame.PPM, gameCam);

        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("level/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MyGDXGame.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true); // Set gravity as null as we have a top-down game
        b2dr = new Box2DDebugRenderer();
        b2WorldCreator = new B2WorldCreator(world, map, this);

        player = new Player(world, this);

        debug = new Debug(game.batch, player);
        hud = new HUD(game.batch, player, this, currentRound, currentTimeInWave);
        pauseDialog = new ExitToMainMenu(game.batch, this);
        gameOverScene = new GameOverScene(game.batch, this, this.player);

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

        updateOpponents(dt);

        bulletManager.update(dt);

        handleDestroyedOpponents();
        handlePlayerDeath();
        spawnNewOpponents();
        updateGameCamera();
        sendPlayerData();
        handleMobSpawning();

        destroyedOpponents.clear();
    }

    /**
     * Updates opponents' logic and sends their data to the server.
     */
    private void updateOpponents(float dt) {
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
    }

    /**
     * Handles destroyed opponents, removing them from various data structures.
     */
    private void handleDestroyedOpponents() {
        b2WorldCreator.destroyDeadOpponents();
        b2WorldCreator.destroyDeadPlayers();

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

        game.client.sendTCP(opponentDataMap);
    }

    /**
     * Handles the player's death and triggers the death scene if needed.
     */
    private void handlePlayerDeath() {
        if (player.shouldBeDestroyed || hud.allWavesFinished()) {
            if (deathSceneCounter == 0) {
                if (hud.allWavesFinished()) {
                    gameOverScene.addText("You have won.");
                }
                gameOverScene.showStage();
                deathSceneCounter++;

                game.playerDataMap.clear();
                game.playerDict.clear();

                if (SinglePlayerScreen.singlePlayerWorlds.containsValue(worldUUID)) {
                    game.client.sendTCP(new RemoveSinglePlayerWorld(game.playerUUID, worldUUID, SinglePlayerScreen.singlePlayerWorlds.get(worldUUID)));
                } else {
                    game.client.sendTCP(new RemoveMultiPlayerWorld(game.playerUUID, worldUUID, MultiPlayerScreen.multiPlayerWorlds.get(worldUUID)));
                }

                SinglePlayerScreen.singlePlayerWorlds.remove(getWorldNameByUUID(SinglePlayerScreen.singlePlayerWorlds, worldUUID));
                MultiPlayerScreen.multiPlayerWorlds.remove(getWorldNameByUUID(MultiPlayerScreen.multiPlayerWorlds, worldUUID));


                game.updateMenu();
            }
        }
    }

    private String getWorldNameByUUID(Map<String, String> worldMap, String worldUUID) {
        for (Map.Entry<String, String> entry : worldMap.entrySet()) {
            if (entry.getValue().equals(worldUUID)) {
                return entry.getKey();
            }
        }
        return null; // UUID not found in the given map
    }

    /**
     * Spawns new opponents based on opponentDataMap if not already spawned.
     */
    private void spawnNewOpponents() {
        HashMap<String, OpponentData> map = opponentDataMap.getMap();
        for (Map.Entry<String, OpponentData> entry : map.entrySet()) {
            String key = entry.getKey();
            OpponentData opponentData = entry.getValue();
            if (shouldSkipOpponent(opponentData, key)) continue;

            Opponent opponent = createOpponent(opponentData);
            opponents.put(key, opponent);
            opponentIds.add(key);
        }
    }

    /**
     * Determines whether an opponent should be skipped.
     */
    private boolean shouldSkipOpponent(OpponentData data, String key) {
        return opponentIds.contains(key) ||
                destroyedOpponents.contains(key) ||
                data.getHealth() == 0;
    }

    /**
     * Updates the game camera's position and view.
     */
    private void updateGameCamera() {
        gameCam.position.x = player.b2body.getPosition().x;
        gameCam.position.y = player.b2body.getPosition().y;
        gameCam.update();
        renderer.setView(gameCam);
    }

    /**
     * Creates an opponent instance based on the mob ID.
     */
    private Opponent createOpponent(OpponentData data) {
        switch (data.getMob()) {
            case ROBOT_ID:
                return new Robot(world, this, data.getX(), data.getY(), data.getHealth(), data.getUuid());
            case BOSS_ID:
                return new Boss(world, this, data.getX(), data.getY(), data.getHealth(), data.getUuid(), data.getMobSpawnTime());
            case MONSTER_ID:
                return new Monster(world, this, data.getX(), data.getY(), data.getHealth(), data.getUuid());
            default:
                throw new IllegalArgumentException("Unknown mob ID: " + data.getMob());
        }
    }

    /**
     * Sends player data to the server if position or state has changed.
     */
    private void sendPlayerData() {
        if (hasPlayerDataChanged()) {
            game.client.sendTCP(new PlayerData(
                    gameCam.position.x,
                    gameCam.position.y,
                    player.getCurrentFrameIndex(),
                    player.runningRight,
                    player.getHealth(),
                    player.getUuid(),
                    worldUUID
            ));

            updatePreviousPlayerData();
        }
    }

    /**
     * Checks if the player data has changed.
     */
    private boolean hasPlayerDataChanged() {
        return prevPosX != gameCam.position.x ||
                prevPosY != gameCam.position.y ||
                player.prevState != player.currentState ||
                player.getCurrentFrameIndex() != prevFrameIndex ||
                player.getIsMining() ||
                prevRunningRight != player.runningRight;
    }

    /**
     * Updates the previous player data for tracking changes.
     */
    private void updatePreviousPlayerData() {
        prevPosX = gameCam.position.x;
        prevPosY = gameCam.position.y;
        player.prevState = player.currentState;
        prevFrameIndex = player.getCurrentFrameIndex();
        prevRunningRight = player.runningRight;
    }

    /**
     * Handles the spawning of mobs if the time and conditions are met.
     */
    private void handleMobSpawning() {

        if (mobSpawnIntervalCounter >= MOB_SPAWN_INTERVAL * 60 &&
                opponentIds.size() <= MOB_PER_PLAYER * (game.playerDict.size() + 1)) {
            b2WorldCreator.spawnMob();
            mobSpawnIntervalCounter = 0;
        }
        mobSpawnIntervalCounter++;


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

        System.out.println(isSinglePlayerWorld());
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
        if (gameOverScene.isToShow()) {
            gameOverScene.renderStage();
        }
    }

    /**
     * Check if world is single player.
     * @return whether world is single player
     */
    public boolean isSinglePlayerWorld() {
        return singlePlayerWorld;
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
        gameOverScene.stage.getViewport().update(width, height, true);

        pauseDialog.centerDialog();
        gameOverScene.centerScene();
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
     * Changes player's screen to the main menu screen.
     */
    public void goToMenu() {
        music.dispose();
        game.client.sendTCP(new PlayerLeavesTheWorld(worldUUID, hud.getCurrentWave(), hud.getCurrentTime()));

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
        gameOverScene.dispose();
        pauseDialog.dispose();
    }
}
