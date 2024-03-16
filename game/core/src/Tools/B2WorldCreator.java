package Tools;

import Bullets.Bullet;
import Opponents.Robot;
import Screens.PlayScreen;
import Sprites.OtherPlayer;
import Sprites.Player;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MyGDXGame;

import java.util.HashSet;
import java.util.Set;

public class B2WorldCreator {
    public static Set<Robot> robotsToDestroy = new HashSet<>();
    public static Set<OtherPlayer> playersToDestroy = new HashSet<>();
    private World world;
    private static final int START_POSITION_LAYER_INDEX = 4;
    private static final int WALLS_LAYER_INDEX = 5;

    /**
     * Constructor
     *
     * @param world
     * @param map
     * @param playScreen
     */
    public B2WorldCreator(World world, TiledMap map, PlayScreen playScreen) {
        this.world = world;

        setStartPosition(map, playScreen);
        createWalls(map);
        setContactListener();
    }

    /**
     * Sets the starting position
     *
     * @param map
     * @param playScreen
     */
    private void setStartPosition(TiledMap map, PlayScreen playScreen) {
        for (MapObject object : map.getLayers().get(START_POSITION_LAYER_INDEX).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            playScreen.startPosX = rectangle.getX();
            playScreen.startPosY = rectangle.getY();
        }
    }

    /**
     * Creates the walls
     *
     * @param map
     */
    private void createWalls(TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        for (MapObject object : map.getLayers().get(WALLS_LAYER_INDEX).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MyGDXGame.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MyGDXGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rectangle.getWidth() / 2 / MyGDXGame.PPM, rectangle.getHeight() / 2 / MyGDXGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MyGDXGame.WORLD_CATEGORY;
            fdef.filter.maskBits = MyGDXGame.BULLET_CATEGORY | MyGDXGame.PLAYER_CATEGORY | MyGDXGame.OTHER_PLAYER_CATEGORY | MyGDXGame.OPPONENT_CATEGORY;

            body.createFixture(fdef);
        }
    }

    /**
     * Sets a contact listener
     */
    private void setContactListener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                handleBeginContact(contact);
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
     * Handles contact
     *
     * @param contact
     */
    private void handleBeginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();


        // Check if either fixture is the bullet
        if (fixtureA.getBody().getUserData() instanceof Bullet || fixtureB.getBody().getUserData() instanceof Bullet) {
            // Mark the bullet as destroyed
            Bullet bullet = (Bullet) (fixtureA.getBody().getUserData() instanceof Bullet ? fixtureA.getBody().getUserData() : fixtureB.getBody().getUserData());
            bullet.setShouldDestroy();

            // Check if the other fixture is a robot and apply damage
            Fixture robotFixture = (fixtureA.getBody().getUserData() instanceof Robot) ? fixtureA : (fixtureB.getBody().getUserData() instanceof Robot) ? fixtureB : null;

            if (robotFixture != null) {
                Robot robot = (Robot) robotFixture.getBody().getUserData();
                robot.takeDamage(Bullet.DAMAGE);
            }

            // Check if the other fixture is another player and apply damage
            Fixture otherPlayerFixture = (fixtureA.getBody().getUserData() instanceof OtherPlayer) ? fixtureA : (fixtureB.getBody().getUserData() instanceof OtherPlayer) ? fixtureB : null;

            if (otherPlayerFixture != null) {
                OtherPlayer player = (OtherPlayer) otherPlayerFixture.getBody().getUserData();
                player.takeDamage(Bullet.DAMAGE);

            }

            // Check if the other fixture is yourself and apply damage
            Fixture playerFixture = (fixtureA.getBody().getUserData() instanceof Player) ? fixtureA : (fixtureB.getBody().getUserData() instanceof Player) ? fixtureB : null;

            if (playerFixture != null) {
                Player player = (Player) playerFixture.getBody().getUserData();
                player.takeDamage(Bullet.DAMAGE);
            }
        }
    }

    /**
     * Removes the killed robots
     */
    public void destroyDeadRobots() {
        // Destroy robots marked for destruction
        for (Robot robot : robotsToDestroy) {
            world.destroyBody(robot.b2body);

            String uniqueId = robot.getUuid();

            PlayScreen.robots.remove(uniqueId);
            PlayScreen.destroyedRobots.add(uniqueId);
            PlayScreen.allDestroyedRobots.add(uniqueId);
        }

        robotsToDestroy.clear();
    }

    /**
     * Removes the killed players
     */
    public void destroyDeadPlayers() {
        // Destroy robots marked for destruction
        for (OtherPlayer player : playersToDestroy) {
            System.out.println("Destroying player");
            world.destroyBody(player.b2body);

            MyGDXGame.playerDict.remove(player.getId());
            MyGDXGame.playerDataMap.remove(player.getId());
            PlayScreen.allDestroyedPlayers.add(player.getUuid());
        }

        playersToDestroy.clear();
    }
}
