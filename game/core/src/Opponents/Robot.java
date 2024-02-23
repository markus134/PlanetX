package Opponents;

import ObjectsToSend.RobotData;
import Screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGDXGame;

import java.util.ArrayList;
import java.util.HashMap;

public class Robot extends Sprite {
    private static final int FRAME_WIDTH = 48;
    private static final int FRAME_HEIGHT = 48;
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float robot_RADIUS = 8 / MyGDXGame.PPM;
    private static final float LINEAR_DAMPING = 4f;
    private static final float robot_HEIGHT = 64 / MyGDXGame.PPM;
    private static final float robot_WIDTH = 64 / MyGDXGame.PPM;
    private static final float VELOCITY_THRESHOLD = 0.8f;

    // Enums for robot state and direction
    public enum State {
        RUNNING,
        STANDING
    }

    public enum runDirection {
        UPPER,
        RIGHT,
        LOWER,
        UP,
        DOWN
    }

    public State currentState;
    public State prevState;
    public runDirection currentDirection;
    public runDirection prevRunDirection;
    public World world;
    public Body b2body;
    private TextureRegion robotStand;
    private Animation<TextureRegion> robotRunUpper;
    private Animation<TextureRegion> robotRun;
    private Animation<TextureRegion> robotRunLower;
    private Animation<TextureRegion> robotRunUp;
    private Animation<TextureRegion> robotRunDown;
    public ArrayList<TextureRegion> robotAllFrames = new ArrayList<>();
    private HashMap<TextureRegion, Integer> frameIndexMap = new HashMap<>();
    public boolean runningRight;
    private float stateTimer;
    public TextureRegion region;


    public Robot(World world, PlayScreen screen) {
        super(screen.getAtlas2().findRegion("Robot"));
        this.world = world;
        currentState = State.STANDING;
        currentDirection = runDirection.RIGHT;
        stateTimer = 0;
        runningRight = true;

        initializeAnimations();
        definerobot(screen.startPosX, screen.startPosY);

        robotStand = new TextureRegion(getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        robotAllFrames.add(robotStand);

        // Put all frames into a hashmap, so we wouldn't have to search the whole list everytime we want to get the current frame's index
        for (int i = 0; i < robotAllFrames.size(); i++) {
            frameIndexMap.put(robotAllFrames.get(i), i);
        }

        setBounds(0, 0, robot_WIDTH, robot_HEIGHT);
        setRegion(robotStand);
    }

    /**
     * Initializes robot animations using sprite sheet regions.
     */
    private void initializeAnimations() {
        robotRunUpper = createAnimation(0, 3, 1);
        robotRunLower = createAnimation(0, 3, 2);
        robotRun = createAnimation(0, 3, 0);
        robotRunDown = createAnimation(0, 3, 4);
        robotRunUp = createAnimation(0, 3, 5);
    }

    public int getCurrentFrameIndex() {
        return frameIndexMap.getOrDefault(region, -1);
    }

    /**
     * Creates an animation from specified sprite sheet region parameters.
     *
     * @param startFrame The starting frame index in the sprite sheet.
     * @param endFrame   The ending frame index in the sprite sheet.
     * @param row        The row index in the sprite sheet.
     * @return The created animation.
     */
    private Animation<TextureRegion> createAnimation(int startFrame, int endFrame, int row) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = startFrame; i <= endFrame; i++) {
            TextureRegion textureRegion = new TextureRegion(getTexture(), i * FRAME_WIDTH, row * FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);
            frames.add(textureRegion);
            robotAllFrames.add(textureRegion);
        }
        return new Animation<>(ANIMATION_SPEED, frames);
    }

    /**
     * Updates the robot's position and sets the appropriate animation frame.
     *
     * @param delta The time elapsed since the last frame.
     */
    public void update(float delta) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        region = getFrame(delta);
        setRegion(region);
    }

    public void updatePosition(RobotData data) {
        if (data.getFrame() != -1) {
            b2body.setTransform(data.getX(), data.getY(), 0); // Set the box2d body at the right place
            setPosition(data.getX() - getWidth() / 2, data.getY() - getHeight() / 2); // Set the texture pos at the right place
            TextureRegion frame = robotAllFrames.get(data.getFrame()); // The connection sends the index of correct frame
            if (!data.isRunningRight() && !frame.isFlipX()) {
                frame.flip(true, false);
            } else if (data.isRunningRight() && frame.isFlipX()) {
                frame.flip(true, false);
            }
            setRegion(frame);
            b2body.setAwake(true); // By default, it's not awake
        }
    }

    /**
     * Retrieves the current animation frame based on the robot's state and direction.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame.
     */
    public TextureRegion getFrame(float dt) {
        currentState = getState();

        // If we are standing, then there is no point in continuing and we can return robotStand
        if (currentState == State.STANDING) {
            return robotStand;
        }

        currentDirection = getRunDirection();

        TextureRegion region;

        // We will only check the right side directions. If it's left, then we can flip the region
        // The UPPER and LOWER mean upper right and lower right respectively (maybe change the names)
        switch (currentDirection) {
            case UPPER:
                region = robotRunUpper.getKeyFrame(stateTimer, true);
                break;
            case RIGHT:
                region = robotRun.getKeyFrame(stateTimer, true);
                break;
            case LOWER:
                region = robotRunLower.getKeyFrame(stateTimer, true);
                break;
            case UP:
                region = robotRunUp.getKeyFrame(stateTimer, true);
                break;
            case DOWN:
                region = robotRunDown.getKeyFrame(stateTimer, true);
                break;
            default:
                region = robotStand;
                break;
        }
        // Start the animation from the start if currentState or currentDirection have changed
        stateTimer = (currentState == prevState && currentDirection == prevRunDirection) ? stateTimer + dt : 0;

        // We will check if linear velocity indicates right or left movement and also check whether the region is already flipped
        if (b2body.getLinearVelocity().x < 0 && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if (b2body.getLinearVelocity().x > 0 && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        prevState = currentState;
        prevRunDirection = currentDirection;

        return region;
    }

    /**
     * Retrieves the current state of the robot (standing or running) based on linear velocity.
     *
     * @return The current state of the robot.
     */
    private State getState() {
        float velocityX = b2body.getLinearVelocity().x;
        float velocityY = b2body.getLinearVelocity().y;
        float velocityThreshold = VELOCITY_THRESHOLD; // Added a velocity threshold as we want the standing texture to be rendered right away not when velocity reaches zero

        if (Math.abs(velocityX) < velocityThreshold && Math.abs(velocityY) < velocityThreshold) {
            return State.STANDING;
        }

        return State.RUNNING;
    }

    /**
     * Retrieves the current running direction of the robot based on linear velocity.
     *
     * @return The current running direction of the robot (right side).
     */
    private runDirection getRunDirection() {
        float velocityX = Math.abs(b2body.getLinearVelocity().x); // We only care for positive x here so for example if running direction is upper left, then we want to return upper right
        float velocityY = b2body.getLinearVelocity().y;

        if (velocityX > 0) {
            if (velocityY > 0) return runDirection.UPPER;
            else if (velocityY < 0) return runDirection.LOWER;
            else return runDirection.RIGHT;
        } else {
            if (velocityY > 0) return runDirection.UP;
            else if (velocityY < 0) return runDirection.DOWN;
            else return null; // We should never reach this point hopefully as it means that the robot isn't running
        }
    }

    /**
     * Defines the robot's Box2D body and fixture.
     */
    public void definerobot(float startX, float startY) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startX / MyGDXGame.PPM, startY / MyGDXGame.PPM);
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
