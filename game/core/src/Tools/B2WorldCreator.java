package Tools;

import Bullets.Bullet;
import Bullets.BulletManager;
import Screens.PlayScreen;
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

public class B2WorldCreator {

    public B2WorldCreator(World world, TiledMap map, PlayScreen playScreen) {
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
}
