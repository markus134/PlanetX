package Tools;

import Bullets.Bullet;
import Opponents.Boss;
import Opponents.Monster;
import Opponents.Opponent;
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
import crystals.Crystal;
import serializableObjects.OpponentData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class B2WorldCreator {
    private static final Set<Opponent> opponentsToDestroy = new HashSet<>();
    public static Set<OtherPlayer> playersToDestroy = new HashSet<>();
    private final World world;
    private static final int CRYSTALS_LAYER_INDEX = 4;
    private static final int START_POSITION_LAYER_INDEX = 5;
    private static final int WALLS_LAYER_INDEX = 6;
    private static final int OPPONENT_SPAWN_LAYER_INDEX = 7;
    private final List<Rectangle> opponentSpawnPoints = new ArrayList<>();
    private PlayScreen playScreen;

    /**
     * Constructor
     *
     * @param world
     * @param map
     * @param playScreen
     */
    public B2WorldCreator(World world, TiledMap map, PlayScreen playScreen) {
        this.world = world;
        this.playScreen = playScreen;

        setStartPosition(map, playScreen);
        createWalls(map);
        createCrystals(map);
        setContactListener();
        setOpponentSpawnPoints(map);
    }

    private void setOpponentSpawnPoints(TiledMap map) {
        for (MapObject object : map.getLayers().get(OPPONENT_SPAWN_LAYER_INDEX).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            opponentSpawnPoints.add(rectangle);
        }
    }

    public void spawnMob() {
        // select a possible spawnPoint
        int maxSpawnPointID = opponentSpawnPoints.size();
        int spawnPointID = new Random().nextInt(maxSpawnPointID);

        Rectangle spawn = opponentSpawnPoints.get(spawnPointID);
        float x = spawn.getX() / MyGDXGame.PPM;
        float y = spawn.getY() / MyGDXGame.PPM;

        // 0, 1, 2, 3 - robot, 4, 5, 6, 7 - monster, 8, 9 - boss
        int whatMobToSpawn = new Random().nextInt(10);
        String uniqueID = UUID.randomUUID().toString();
        Opponent opponent;

        List<Integer> arr1 = new ArrayList<>(List.of(0, 1, 2, 3));
        List<Integer> arr2 = new ArrayList<>(List.of(4, 5, 6, 7));

        if (arr1.contains(whatMobToSpawn)) {
            System.out.println("robot");
            opponent = new Robot(world, playScreen, x, y, Robot.MAX_HEALTH, uniqueID);
        } else if (arr2.contains(whatMobToSpawn)) {
            System.out.println("monster");
            opponent = new Monster(world, playScreen, x, y, Monster.MAX_HEALTH, uniqueID);
        } else {
            System.out.println("boss");
            opponent = new Boss(world, playScreen, x, y, Boss.MAX_HEALTH, uniqueID, System.currentTimeMillis());
        }

        playScreen.opponentIds.add(uniqueID);
        playScreen.opponents.put(uniqueID, opponent);
        playScreen.opponentDataMap.put(
                uniqueID,
                new OpponentData(
                        opponent.getX(),
                        opponent.getY(),
                        opponent.getHealth(),
                        opponent.getUuid(),
                        opponent.getMobId(),
                        opponent.getSpawnTime()));

        playScreen.game.client.sendTCP(playScreen.opponentDataMap);
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

    private void createCrystals(TiledMap map) {
        for (MapObject object : map.getLayers().get(CRYSTALS_LAYER_INDEX).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            float x = rectangle.getX();
            float y = rectangle.getY();

            playScreen.crystals.add(new Crystal(x, y));
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

            // Check if the other fixture is an opponent and apply damage
            Fixture opponentFixture = (fixtureA.getBody().getUserData() instanceof Opponent) ? fixtureA : (fixtureB.getBody().getUserData() instanceof Robot) ? fixtureB : null;

            if (opponentFixture != null) {
                Opponent opponent = (Opponent) opponentFixture.getBody().getUserData();
                opponent.takeDamage(Bullet.DAMAGE);
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
     * Removes the killed opponents
     */
    public void destroyDeadOpponents() {
        // Destroy opponents marked for destruction
        for (Opponent opponent : opponentsToDestroy) {
            world.destroyBody(opponent.getBody());

            String uniqueId = opponent.getUuid();

            playScreen.opponents.remove(uniqueId);
            playScreen.destroyedOpponents.add(uniqueId);
            playScreen.allDestroyedOpponents.add(uniqueId);

        }

        opponentsToDestroy.clear();
    }

    /**
     * Removes the killed players
     */
    public void destroyDeadPlayers() {
        // Destroy robots marked for destruction
        for (OtherPlayer player : playersToDestroy) {
            System.out.println("Destroying player");
            world.destroyBody(player.b2body);

            playScreen.game.playerDict.remove(player.getId());
            playScreen.game.playerDataMap.remove(player.getId());
            playScreen.allDestroyedPlayers.add(player.getUuid());
        }

        playersToDestroy.clear();
    }

    public static void markOpponentAsDestroyed(Opponent opponent) {
        opponentsToDestroy.add(opponent);
    }
}
