package Screens;

import Bullets.Bullet;
import Bullets.BulletManager;
import InputHandlers.PlayScreenInputHandler;
import Scenes.DeathScene;
import Scenes.ExitToMainMenu;
import crystals.Crystal;
import Opponents.Robot;
import Scenes.Debug;
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
import serializableObjects.PlayerData;
import serializableObjects.RobotData;
import serializableObjects.RobotDataMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PlayScreen implements Screen {
    private MyGDXGame game;
    public static OrthographicCamera gameCam = new OrthographicCamera();
    private Viewport gamePort;
    private TiledMap map;
    private TmxMapLoader mapLoader = new TmxMapLoader();
    private OrthogonalTiledMapRenderer renderer;
    public World world;
    private Box2DDebugRenderer b2dr;
    public Player player;
    private final TextureAtlas playerAtlas = new TextureAtlas("player/player_spritesheet.atlas");
    private final TextureAtlas robotAtlas = new TextureAtlas("Opponents/Robot.atlas");
    private float prevPosX = 0;
    private float prevPosY = 0;
    private int prevFrameIndex = 0;

    public float startPosX;
    public float startPosY;
    private Debug debug;
    public HUD hud;
    public BulletManager bulletManager;
    public static List<String> robotIds = new ArrayList<>();
    public static HashMap<String, Robot> robots = new HashMap<>();
    public static RobotDataMap robotDataMap;
    private PlayScreenInputHandler handler;
    private B2WorldCreator b2WorldCreator;
    public static Set<String> destroyedRobots = new HashSet<>();
    public static Set<String> allDestroyedRobots = new HashSet<>();
    public static Set<String> allDestroyedPlayers = new HashSet<>();
    private Music music;
    public String worldUUID;
    public static List<Crystal> crystals = new ArrayList<>();
    public final ExitToMainMenu pauseDialog;
    private final MenuScreen menuScreen;
    public final DeathScene deathScene;
    private int deathSceneCounter = 0;

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
        robotDataMap = new RobotDataMap(worldUUID);
        gamePort = new FitViewport(MyGDXGame.V_WIDTH / MyGDXGame.PPM, MyGDXGame.V_HEIGHT / MyGDXGame.PPM, gameCam);

        map = mapLoader.load("level/test_map.tmx");
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
        music.setLooping(true);
        music.setVolume(SettingsScreen.musicValue);
        music.play();
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


    /**
     * Updates the game logic.
     *
     * @param dt The time elapsed since the last frame.
     */
    public void update(float dt) throws IOException {
        world.step(1 / 60f, 6, 2);

        player.update(dt);

        // updating robots and adding info to the robotDataMap, which is sent to the server
        for (Map.Entry<String, Robot> entry : robots.entrySet()) {
            Robot robot = entry.getValue();
            robotDataMap.put(entry.getKey(),
                    new RobotData(robot.getX(), robot.getY(), robot.getHealth(), robot.getUuid()));
            robot.update(dt);
        }

        bulletManager.update(dt);
        b2WorldCreator.destroyDeadRobots();
        b2WorldCreator.destroyDeadPlayers();

        MyGDXGame.client.sendTCP(robotDataMap);

        for (String id : destroyedRobots) {
            if (robotDataMap.getMap().containsKey(id)) {

                float x = robotDataMap.getMap().get(id).getX();
                float y = robotDataMap.getMap().get(id).getY();

                if (Math.abs(player.getX() - x) < 1f &&
                        Math.abs(player.getY() - y) < 1f) {
                    player.takeDamage(Robot.EXPLOSION_DAMAGE);
                }

                robots.remove(id);
                robotIds.remove(id);
                robotDataMap.remove(id);
            }
        }

        if (player.shouldBeDestroyed) {
            if (deathSceneCounter == 0) {
                deathScene.showStage();
                deathSceneCounter++;
            }
        }

        // robotDataMap is constantly being updated by all client instances
        // this block of code makes new instances of the robot class if it is a robot with a new ID
        HashMap<String, RobotData> map = robotDataMap.getMap();
        for (Map.Entry<String, RobotData> entry : map.entrySet()) {
            String key = entry.getKey();
            if (!robotIds.contains(key) && !destroyedRobots.contains(key) && entry.getValue().getHealth() != 0) {
                Robot robot = new Robot(world,
                        this,
                        entry.getValue().getX(),
                        entry.getValue().getY(),
                        entry.getValue().getHealth(),
                        entry.getValue().getUuid());

                robots.put(key, robot);
                robotIds.add(key);

            }
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
                || player.getIsMining()) {

            MyGDXGame.client.sendTCP(new PlayerData(
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
        }

        destroyedRobots.clear();
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

        player.draw(game.batch); // Draw the player after rendering the physics world

        // draws all robots
        for (Map.Entry<String, Robot> entry : robots.entrySet()) {
            Robot robot = entry.getValue();
            robot.draw(game.batch);
        }

        // Draw the other players within the game
        for (Map.Entry<Integer, Set<OtherPlayer>> entry : MyGDXGame.playerDict.entrySet()) {
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
        world.destroyBody(player.b2body);
        MyGDXGame.playerDict.clear();
        game.dispose();
        music.dispose();

        // currently we have not yet decided what to do when the player dies
        // bcs the dead players go back to the main menu, it is logical to
        // remove their connection with the server
        MyGDXGame.client.close();
        MyGDXGame.client.dispose();

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
        deathScene.dispose();
        pauseDialog.dispose();
    }
}
