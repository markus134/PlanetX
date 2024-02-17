package Sprites;

import Screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGDXGame;

import java.util.ArrayList;

public class OtherPlayer extends Sprite {
    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 32;
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float PLAYER_RADIUS = 8 / MyGDXGame.PPM;
    private static final float LINEAR_DAMPING = 4f;
    private static final float PLAYER_HEIGHT = 64 / MyGDXGame.PPM;
    private static final float PLAYER_WIDTH = 64 / MyGDXGame.PPM;
    public World world;
    public Body b2body;
    private TextureRegion playerStand;
    public ArrayList<TextureRegion> playerAllFrames = new ArrayList<>();


    public OtherPlayer(World world, PlayScreen screen, float posX, float posY) {
        super(screen.getAtlas().findRegion("player_spritesheet"));
        this.world = world;

        initializeAllFrames();
        definePlayer();

        playerStand = new TextureRegion(getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        playerAllFrames.add(playerStand);

        setBounds(0, 0, PLAYER_WIDTH, PLAYER_HEIGHT);
        setRegion(playerStand);
        setPosition(posX, posY);
    }

    /**
     * Initializes all frames.
     */
    private void initializeAllFrames() {
        addFrames(0, 3, 1);
        addFrames(0, 3, 2);
        addFrames(0, 5, 3);
        addFrames(0, 3, 4);
        addFrames(0, 3, 5);
    }

    /**
     * Adds frames to playerAllFrames from specified sprite sheet region parameters.
     *
     * @param startFrame The starting frame index in the sprite sheet.
     * @param endFrame   The ending frame index in the sprite sheet.
     * @param row        The row index in the sprite sheet.
     * @return The created animation.
     */
    private Animation<TextureRegion> addFrames(int startFrame, int endFrame, int row) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = startFrame; i <= endFrame; i++) {
            playerAllFrames.add(new TextureRegion(getTexture(), i * FRAME_WIDTH, row * FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT));
        }
        return new Animation<>(ANIMATION_SPEED, frames);
    }

    /**
     * Updates the player's position and sets the appropriate animation frame.
     */
    public void update(float posX, float posY, int frame_index, boolean runningRight) {
        b2body.setTransform(posX, posY, 0); // Set the box2d body at the right place
        setPosition(posX - getWidth() / 2, posY - getHeight() / 2); // Set the texture pos at the right place
        TextureRegion frame = playerAllFrames.get(frame_index); // The connection sends the index of correct frame
        if (!runningRight && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (runningRight && frame.isFlipX()) {
            frame.flip(true, false);
        }
        setRegion(frame);
        b2body.setAwake(true); // By default, it's not awake
    }

    /**
     * Defines the player's Box2D body and fixture.
     */
    public void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(PLAYER_WIDTH, PLAYER_HEIGHT);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(PLAYER_RADIUS);

        fdef.shape = shape;
        b2body.createFixture(fdef);

        // Set linear damping to simulate friction
        b2body.setLinearDamping(LINEAR_DAMPING);
    }
}
