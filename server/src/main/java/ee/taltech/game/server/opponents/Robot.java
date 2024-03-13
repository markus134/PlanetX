package main.java.ee.taltech.game.server.opponents;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Robot {
    private final float PPM = 100;
    private final float robot_RADIUS = 16 / PPM;
    private final float LINEAR_DAMPING = 4f;
    private Body b2body;

    public Robot(World world, float startX, float startY) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startX / PPM, startY / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(robot_RADIUS);

        fdef.shape = shape;
        b2body.createFixture(fdef);

        // Set linear damping to simulate friction
        b2body.setLinearDamping(LINEAR_DAMPING);
    }
}
