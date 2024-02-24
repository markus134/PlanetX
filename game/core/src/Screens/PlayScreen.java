package Screens;

import Bullets.Bullet;
import Bullets.BulletManager;
import ObjectsToSend.BulletData;
import ObjectsToSend.PlayerData;
import ObjectsToSend.RobotData;
import ObjectsToSend.RobotDataMap;
import InputHandlers.PlayScreenInputHandler;
import ObjectsToSend.PlayerData;
import ObjectsToSend.RobotData;
import Opponents.Robot;
import Scenes.Debug;
import Sprites.OtherPlayer;
import Sprites.Player;
import Tools.B2WorldCreator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

import java.util.*;
import java.util.Map;


public class PlayScreen implements Screen {
    private MyGDXGame game;
    public OrthographicCamera gameCam = new OrthographicCamera();
    private Viewport gamePort;
    private TiledMap map;
    private TmxMapLoader mapLoader = new TmxMapLoader();
    private OrthogonalTiledMapRenderer renderer;
    public World world;
    private Box2DDebugRenderer b2dr;
    public Player player;
    private TextureAtlas atlas = new TextureAtlas("player_spritesheet.atlas");
    private TextureAtlas atlas2 = new TextureAtlas("Opponents/Robot.atlas");
    private float prevPosX = 0;
    private float prevPosY = 0;
    public float startPosX;
    public float startPosY;
    private Debug debug;
    public BulletManager bulletManager;
    public static List<String> robotIds = new ArrayList<>();
    public static HashMap<String, Robot> robots = new HashMap<>();
    public static RobotDataMap robotDataMap = new RobotDataMap();
    private PlayScreenInputHandler handler;

    /**
     * Constructor for the PlayScreen.
     *
     * @param game The Game instance representing the main game.
     */
    public PlayScreen(MyGDXGame game) {
        this.game = game;
        gamePort = new StretchViewport(MyGDXGame.V_WIDTH / MyGDXGame.PPM, MyGDXGame.V_HEIGHT / MyGDXGame.PPM, gameCam);

        map = mapLoader.load("test_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MyGDXGame.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true); // Set gravity as null as we have a top-down game
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world, map, this);

        player = new Player(world, this);

        debug = new Debug(game.batch, player);

        // Initialize BulletManager
        bulletManager = new BulletManager(world);


        handler = new PlayScreenInputHandler(this);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                // Check if either fixture is the bullet
                if (fixtureA.getBody().getUserData() != null || fixtureB.getBody().getUserData() != null) {
                    // Mark the bullet as destroyed
                    int id = (int) (fixtureA.getBody().getUserData() != null ? fixtureA.getBody().getUserData() : fixtureB.getBody().getUserData());
                    Bullet bullet = BulletManager.getBulletById(id);
                    bullet.setShouldDestroy();
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {
            }
        });
    }


    /**
     * Shows the PlayScreen and sets the input processor.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(handler);
    }

    /**
     * Gets the texture atlas used in the game.
     *
     * @return The texture atlas.
     */
    public TextureAtlas getAtlas() {
        return atlas;
    }

    /**
     * Gets the texture atlas used in the game.
     *
     * @return The texture atlas.
     */
    public TextureAtlas getAtlas2() {
        return atlas2;
    }



    /**
     * Updates the game logic.
     *
     * @param dt The time elapsed since the last frame.
     */
    public void update(float dt) {
        world.step(1 / 60f, 6, 2);

        player.update(dt);

        // updating robots and adding info to the robotDataMap, which is sent to the server
        for (Map.Entry<String, Robot> entry : robots.entrySet()) {
            Robot robot = entry.getValue();
            robotDataMap.put(entry.getKey(),
                    new RobotData(robot.getX(), robot.getY()));
            robot.update(dt);
        }
        MyGDXGame.client.sendTCP(robotDataMap);

        bulletManager.update(dt);

        // robotDataMap is constantly being updated by all client instances
        // this block of code makes new instances of the robot class if it is a robot with a new ID
        HashMap<String, RobotData> map = robotDataMap.getMap();
        for (Map.Entry<String, RobotData> entry: map.entrySet()){
            String key = entry.getKey();
            if (!robotIds.contains(key)) {
                Robot robot = new Robot(world, this, entry.getValue().getX(), entry.getValue().getY());
                robots.put(key, robot);
                robotIds.add(key);
            }
        }

        gameCam.position.x = player.b2body.getPosition().x;
        gameCam.position.y = player.b2body.getPosition().y;

        gameCam.update();
        renderer.setView(gameCam);

        // It is used to send the position of the player to the server
        if (prevPosX != gameCam.position.x || prevPosY != gameCam.position.y || player.prevState != player.currentState)  {
            MyGDXGame.client.sendTCP(new PlayerData(
                    gameCam.position.x,
                    gameCam.position.y,
                    player.getCurrentFrameIndex(),
                    player.runningRight
            ));

            prevPosX = gameCam.position.x;
            prevPosY = gameCam.position.y;
            player.prevState = player.currentState;
        }
    }


    /**
     * Renders the game.
     *
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void render(float delta) {
        handler.handleInput();
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        // Uncomment the following line if you want to see box2d lines
        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch); // Draw the player after rendering the physics world

        // draws all robots
        for (Map.Entry<String, Robot> entry : robots.entrySet()) {
            Robot robot = entry.getValue();
            robot.draw(game.batch);
        }

        // Draw the other players within the game
        for (Map.Entry<Integer, OtherPlayer> entry : MyGDXGame.playerDict.entrySet()) {
            OtherPlayer otherPlayer = entry.getValue();
            otherPlayer.draw(game.batch);
        }

        // Draw bullets
        for (Bullet bullet : bulletManager.getBullets()) {
            bullet.draw(game.batch);
        }

        game.batch.end();

        // rendering debug table
        debug.updateLabelValues();
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

        debug.stage.getViewport().update(width, height, true);
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
     * Disposes of resources used by the PlayScreen.
     */
    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        atlas.dispose();
        atlas2.dispose();
    }
}
