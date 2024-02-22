package Screens;

<<<<<<< Updated upstream
=======
import ObjectsToSend.BulletData;
import Scenes.Debug;
>>>>>>> Stashed changes
import Sprites.OtherPlayer;
import Sprites.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
<<<<<<< Updated upstream
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
=======
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
>>>>>>> Stashed changes
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;
import Bullets.BulletManager;
import Bullets.Bullet;
import ObjectsToSend.PlayerData;

import java.util.*;

public class PlayScreen implements Screen, InputProcessor {
    private MyGDXGame game;
    public OrthographicCamera gameCam;
    private Viewport gamePort;
    private TiledMap map;
    private TmxMapLoader mapLoader;
    private OrthogonalTiledMapRenderer renderer;
    public World world;
    private Box2DDebugRenderer b2dr;
    public Player player;
    private TextureAtlas atlas;
    private int keyPresses = 0;
    private HashSet<Integer> keysPressed;
    private float prevPosX = 0;
    private float prevPosY = 0;
    public float startPosX;
    public float startPosY;
<<<<<<< Updated upstream
    private Stage stage; //it is used for debug table
    private Table debugTable;
    private Label[] labelArray = new Label[4]; //the number corresponds to the amount of displayed variables
    private float mapCenterX = 7.52667f;
    private float mapCenterY = 8.15333f;
=======
    private Debug debug;
    private Vector3 touchPoint; // Added to store the touch point in world coordinates
    public BulletManager bulletManager;
    private float bulletSpeed = 5.0f; // Adjust the bullet speed as needed
>>>>>>> Stashed changes

    /**
     * Constructor for the PlayScreen.
     *
     * @param game The Game instance representing the main game.
     */
    public PlayScreen(MyGDXGame game) {
        atlas = new TextureAtlas("player_spritesheet.atlas");
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new StretchViewport(MyGDXGame.V_WIDTH / MyGDXGame.PPM, MyGDXGame.V_HEIGHT / MyGDXGame.PPM, gameCam);
        keysPressed = new HashSet<>();

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("test_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MyGDXGame.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true); // Set gravity as null as we have a top down game
        b2dr = new Box2DDebugRenderer();

        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle(); // This is actually a point but LibGDX treats points as rectangles with height and width as 0
            startPosX = rectangle.getX();
            startPosY = rectangle.getY();
        }

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Create the box2d walls
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MyGDXGame.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MyGDXGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rectangle.getWidth() / 2 / MyGDXGame.PPM, rectangle.getHeight() / 2 / MyGDXGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
        player = new Player(world, this);

<<<<<<< Updated upstream
        //debug table generation
        stage = new Stage(new ScreenViewport());
        debugTable = new Table();
        debugTable.top().left();
        debugTable.setFillParent(true);

        Label debugLabel = new Label("Debug Info", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        debugLabel.setAlignment(Align.left);
        debugTable.add(debugLabel);

        // you should write the variables that you want to display here
        // and update the updateLabelValues() method
        String[][] arraysOfVariablesWithValues = {
                {"X", Float.toString(player.b2body.getPosition().x)},
                {"Y", Float.toString(player.b2body.getPosition().y)},
                {"xFromCenter", "0"},
                {"yFromCenter", "0"}
        };
        addDebugLabels(arraysOfVariablesWithValues);

        stage.addActor(debugTable);
=======
        debug = new Debug(game.batch, player);

        touchPoint = new Vector3();

        // Initialize BulletManager
        bulletManager = new BulletManager(world);

        // Inside the constructor
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
>>>>>>> Stashed changes
    }

    /**
     * Handles the logic for button clicks.
     */
    private void buttonClick() {
        System.out.println("Button Clicked!");
        // Add your button click logic here
        // This will later be used to regenerate the map without closing and reopening the program
    }

    /**
     * Adds debug labels to the debug table.
     *
     * @param listOfVariablesWithValues Array of variable names and their initial values.
     */
    private void addDebugLabels(String[][] listOfVariablesWithValues) {
        // debug table layout
        for (int i = 0; i < listOfVariablesWithValues.length; i++) {
            Label name = new Label(listOfVariablesWithValues[i][0], new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            Label value = new Label(listOfVariablesWithValues[i][1], new Label.LabelStyle(new BitmapFont(), Color.WHITE));

            debugTable.row();
            debugTable.add(name).padRight(10);
            debugTable.add(value).row();

            labelArray[i] = value;
        }
    }

    /**
     * Updates the values of the debug labels.
     */
    private void updateLabelValues() {
        labelArray[0].setText(Float.toString(player.b2body.getPosition().x));
        labelArray[1].setText(Float.toString(player.b2body.getPosition().y));
        labelArray[2].setText(Float.toString(player.b2body.getPosition().x - mapCenterX));
        labelArray[3].setText(Float.toString(player.b2body.getPosition().y - mapCenterY));
    }

    /**
     * Shows the PlayScreen and sets the input processor.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
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
     * Handles player input.
     */
    private void handleInput() {
        if (keyPresses > 0) {
            for (Integer keypress : keysPressed) {
                switch (keypress) {
                    case Input.Keys.W:
                        player.b2body.applyLinearImpulse(new Vector2(0, 0.1f), player.b2body.getWorldCenter(), true);
                        break;
                    case Input.Keys.S:
                        player.b2body.applyLinearImpulse(new Vector2(0, -0.1f), player.b2body.getWorldCenter(), true);
                        break;
                    case Input.Keys.D:
                        player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
                        break;
                    case Input.Keys.A:
                        player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
                        break;
                    case Input.Keys.B:
                        // this will later be used to regenerate the map
                        buttonClick();
                        break;
                }
            }
        }
    }

    /**
     * Updates the game logic.
     *
     * @param dt The time elapsed since the last frame.
     */
    public void update(float dt) {
        world.step(1 / 60f, 6, 2);

        player.update(dt);

        bulletManager.update(dt);

        gameCam.position.x = player.b2body.getPosition().x;
        gameCam.position.y = player.b2body.getPosition().y;

        gameCam.update();
        renderer.setView(gameCam);

        // It is used to send the position of the player to the server
        if (prevPosX != gameCam.position.x || prevPosY != gameCam.position.y || player.prevState != player.currentState) {
<<<<<<< Updated upstream
            MyGDXGame.client.sendTCP(gameCam.position.x + "," + gameCam.position.y + "," + player.playerAllFrames.indexOf(player.region) + "," + player.runningRight);
=======
            MyGDXGame.client.sendTCP(new PlayerData(
                    gameCam.position.x,
                    gameCam.position.y,
                    player.getCurrentFrameIndex(),
                    player.runningRight
            ));
>>>>>>> Stashed changes

            prevPosX = gameCam.position.x;
            prevPosY = gameCam.position.y;
        }
    }

    /**
     * Renders the game.
     *
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void render(float delta) {
        handleInput();
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        // Uncomment the following line if you want to see box2d lines
        // b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch); // Draw the player after rendering the physics world

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
        updateLabelValues();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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
        keyPresses = 0;
        keysPressed.clear();

        stage.getViewport().update(width, height, true);
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
     * Handles the key down event.
     *
     * @param keycode The keycode of the pressed key.
     * @return True to indicate that the input event was handled.
     */
    @Override
    public boolean keyDown(int keycode) {
        keysPressed.add(keycode);
        keyPresses++;

        return true; // Return true to indicate that the input event was handled
    }

    /**
     * Handles the key up event.
     *
     * @param keycode The keycode of the released key.
     * @return True to indicate that the input event was handled.
     */
    @Override
    public boolean keyUp(int keycode) {
        keysPressed.remove(keycode);

        // Reset horizontal velocity when D or A key is released
        if (keycode == Input.Keys.D || keycode == Input.Keys.A) {
            player.b2body.setLinearVelocity(0, player.b2body.getLinearVelocity().y);
            keyPresses--;
        }

        // Reset vertical velocity when W or S key is released
        if (keycode == Input.Keys.W || keycode == Input.Keys.S) {
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);
            keyPresses--;
        }

        // When resizing and clearing the keyPresses and keyPressed hashset, it for some reason sometimes bugs out and decrements from 0 to get -1
        // This is to make sure it doesn't get lower than 0
        if (keyPresses < 0) keyPresses = 0;

        return true;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    /**
     * Handles touch down event.
     *
     * @param screenX The x-coordinate of the touch position.
     * @param screenY The y-coordinate of the touch position.
     * @param pointer  The pointer for the event.
     * @param button   The button pressed during the event.
     * @return True if the input event was handled.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Convert screen coordinates to world coordinates
        touchPoint.set(screenX, screenY, 0);
        gameCam.unproject(touchPoint);

        // Shoot a bullet towards the touched position
        Bullet newBullet = bulletManager.obtainBullet(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
        Vector2 direction = new Vector2(touchPoint.x - (player.getX() + player.getWidth() / 2), touchPoint.y - (player.getY() + player.getHeight() / 2));
        direction.nor(); // Normalize the direction vector
        newBullet.body.setLinearVelocity(direction.x * bulletSpeed, direction.y * bulletSpeed);

        MyGDXGame.client.sendTCP(new BulletData(
                direction.x * bulletSpeed,
                direction.y * bulletSpeed,
                player.getX() + player.getWidth() / 2,
                player.getY() + player.getHeight() / 2
        ));

        return true;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
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
        stage.dispose();
    }
}
