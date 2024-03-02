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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MyGDXGame;
import serializableObjects.PlayerData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class B2WorldCreator {
    public static Set<Robot> robotsToDestroy = new HashSet<>();
    public static Set<OtherPlayer> playersToDestroy = new HashSet<OtherPlayer>();
    private World world;


    public B2WorldCreator(World world, TiledMap map, PlayScreen playScreen) {
        this.world = world;

        // Get the coordinates of the start position point
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle(); // This is actually a point but LibGDX treats points as rectangles with height and width as 0
            playScreen.startPosX = rectangle.getX();
            playScreen.startPosY = rectangle.getY();
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
            fdef.filter.categoryBits = MyGDXGame.WORLD_CATEGORY;
            fdef.filter.maskBits = MyGDXGame.BULLET_CATEGORY | MyGDXGame.PLAYER_CATEGORY | MyGDXGame.OTHER_PLAYER_CATEGORY;

            body.createFixture(fdef);
        }

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
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

                        if (robot.getHealth() <= 0) {
                            robotsToDestroy.add(robot);
                        }
                    }

                    // Check if the other fixture is a robot and apply damage
                    Fixture otherPlayerFixture = (fixtureA.getBody().getUserData() instanceof OtherPlayer) ? fixtureA : (fixtureB.getBody().getUserData() instanceof OtherPlayer) ? fixtureB : null;

                    if (otherPlayerFixture != null) {
                        OtherPlayer player = (OtherPlayer) otherPlayerFixture.getBody().getUserData();
                        System.out.println("Player took damage from bullet " + bullet);
                        player.takeDamage(Bullet.DAMAGE);
                        System.out.println("other player took damage");
                        if (player.getHealth() <= 0) {
                            playersToDestroy.add(player);
                        }
                    }

                    // Check if the other fixture is a robot and apply damage
                    Fixture playerFixture = (fixtureA.getBody().getUserData() instanceof Player) ? fixtureA : (fixtureB.getBody().getUserData() instanceof Player) ? fixtureB : null;

                    if (playerFixture != null) {
                        Player player = (Player) playerFixture.getBody().getUserData();
                        System.out.println("cur health is " + player.getHealth());
                        player.takeDamage(Bullet.DAMAGE);
                        System.out.println("you took damage, health is now " + player.getHealth());
                        if (player.getHealth() <= 0) {
                            playScreen.playerDead = true;
                        }
                    }
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

    public void destroyDeadRobots() {
        // Destroy robots marked for destruction
        for (Robot robot : robotsToDestroy) {
            world.destroyBody(robot.b2body);

            String uniqueId = robot.getUuid();
            System.out.println("robot " + uniqueId + " is marked for destruction");

            PlayScreen.destroyedRobots.add(uniqueId);
            PlayScreen.allDestroyedRobots.add(uniqueId);
        }

        robotsToDestroy.clear();
    }

    public void destroyDeadPlayers() {
        // Destroy robots marked for destruction
        for (OtherPlayer player : playersToDestroy) {
            world.destroyBody(player.b2body);

            System.out.println("removing player " + player.getUuid());
            MyGDXGame.playerDict.remove(player.getId());
            PlayScreen.allDestroyedPlayers.add(player.getUuid());

            if (MyGDXGame.lastReceivedData instanceof HashMap) {
                ((HashMap<Integer, PlayerData>) MyGDXGame.lastReceivedData).remove(player.getId());
            }
        }

        playersToDestroy.clear();
    }
}
