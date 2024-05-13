package Bullets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MyGDXGame;

public class Bullet extends Sprite {
    private final World world;
    public Body body;
    private final Texture texture;
    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 32;
    public static final int DAMAGE = 20;
    private boolean shouldDestroy = false;
    private int id;

    /**
     * Constructor
     *
     * @param world
     * @param x
     * @param y
     * @param id
     */
    public Bullet(World world, float x, float y, int id) {
        texture = new Texture("Bullets/01.png");

        this.world = world;
        this.id = id;

        // Create the Box2D body for the bullet
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape shape = new CircleShape();
        shape.setRadius(4f / MyGDXGame.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0.5f;

        fixtureDef.filter.categoryBits = MyGDXGame.BULLET_CATEGORY;
        fixtureDef.filter.maskBits = MyGDXGame.WORLD_CATEGORY | MyGDXGame.OPPONENT_CATEGORY | MyGDXGame.OTHER_PLAYER_CATEGORY | MyGDXGame.PLAYER_CATEGORY;

        body.createFixture(fixtureDef);

        setBounds(body.getPosition().x, body.getPosition().y, FRAME_WIDTH / MyGDXGame.PPM, FRAME_HEIGHT / MyGDXGame.PPM);

        setRegion(texture);
    }

    /**
     * Updates the info about the bullet
     *
     * @param deltaTime
     */
    public void update(float deltaTime) {
        if (shouldDestroy) {
            BulletManager.freeBullet(this);
        } else {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        }
    }

    /**
     * Destroys the bullet
     */
    public void destroy() {
        world.destroyBody(body);
    }

    /**
     * Changes the shouldDestroy property to true
     */
    public void setShouldDestroy() {
        shouldDestroy = true;
    }

    /**
     * Dispose of all textures
     */
    public void dispose() {
        texture.dispose();
    }
}
