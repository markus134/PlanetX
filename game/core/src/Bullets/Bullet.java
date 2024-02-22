package Bullets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MyGDXGame;

public class Bullet extends Sprite {
    private World world;
    public Body body;
    private boolean isDestroyed;
    private Texture texture;
    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 32;
    private boolean shouldDestroy = false;

    public Bullet(World world, float x, float y) {
        // Assuming you have a bullet texture, replace "bulletTexturePath" with the actual path
        texture = new Texture("Bullets/01.png");

        this.world = world;
        this.isDestroyed = false;

        // Create the Box2D body for the bullet
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(4f / MyGDXGame.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0.5f;

        fixtureDef.filter.categoryBits = MyGDXGame.BULLET_CATEGORY;
        fixtureDef.filter.maskBits = MyGDXGame.WORLD_CATEGORY;

        body.createFixture(fixtureDef);

        setBounds(body.getPosition().x, body.getPosition().y, FRAME_WIDTH / MyGDXGame.PPM, FRAME_HEIGHT / MyGDXGame.PPM);

        setRegion(texture);
    }

    public void update(float deltaTime) {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        if (shouldDestroy) {
            BulletManager.freeBullet(this);
        }
    }

    public void destroy() {
        // Destroy the Box2D body and mark the bullet as destroyed
        world.destroyBody(body);
        isDestroyed = true;
    }

    public void setShouldDestroy() {
        shouldDestroy = true;
    }

    public void setId(int id) {
        body.setUserData(id);
    }
}
