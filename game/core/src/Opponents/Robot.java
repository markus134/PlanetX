package Opponents;

import ObjectsToSend.PlayerData;
import ObjectsToSend.RobotData;
import Screens.PlayScreen;
import Sprites.OtherPlayer;
import Sprites.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGDXGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Robot extends Sprite {
    private static final int FRAME_WIDTH = 48;
    private static final int FRAME_HEIGHT = 48;
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float robot_RADIUS = 8 / MyGDXGame.PPM;
    private static final float LINEAR_DAMPING = 4f;
    private static final float robot_HEIGHT = 64 / MyGDXGame.PPM;
    private static final float robot_WIDTH = 64 / MyGDXGame.PPM;
    private static final float VELOCITY_THRESHOLD = 0.5f;
    public static RobotData data;

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
    public static HashMap playersInfo = new HashMap<>();


    public Robot(World world, PlayScreen screen) {
        super(screen.getAtlas2().findRegion("Robot"));
        this.world = world;
        currentState = State.STANDING;
        currentDirection = runDirection.RIGHT;
        stateTimer = 0;
        runningRight = true;

        initializeAnimations();
        defineRobot(screen.startPosX, screen.startPosY);

        // Put all frames into a hashmap, so we wouldn't have to search the whole list everytime we want to get the current frame's index
        for (int i = 0; i < robotAllFrames.size(); i++) {
            frameIndexMap.put(robotAllFrames.get(i), i);
        }

        setBounds(0, 0, robot_WIDTH, robot_HEIGHT);
    }

    /**
     * Initializes robot animations using sprite sheet regions.
     */
    private void initializeAnimations() {
        robotRunUpper = createAnimation(0, 3, 1);
        robotRunLower = createAnimation(0, 3, 2);
        robotRun = createAnimation(0, 3, 0);
        robotRunDown = createAnimation(0, 3, 3);
        robotRunUp = createAnimation(0, 3, 4);
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
        updatePosition();
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        region = getFrame(delta);
        setRegion(region);
        b2body.setAwake(true);
    }

    /**
     * Seeks for the closest enemy and moves the body of the robot in that direction.
     */
    private void updatePosition(){
        float distance = Float.MAX_VALUE;
        float closestX = 0;
        float closestY = 0;
        for (Object playerInfo : playersInfo.values()) {
            PlayerData info = (PlayerData) playerInfo;
            float playerX = info.getX();
            float playerY = info.getY();
            float actual_distance = (float) (Math.sqrt(Math.pow(playerX, 2) + Math.pow(playerY, 2)));
            if (actual_distance < distance) {
                distance = actual_distance;
                closestX = playerX;
                closestY = playerY;
            }
        }

        float robotX = this.b2body.getPosition().x;
        float robotY = this.b2body.getPosition().y;

        if (closestX> robotX){
            this.b2body.applyLinearImpulse(new Vector2(0.05f, 0), this.b2body.getWorldCenter(), true);
        } else{
            this.b2body.applyLinearImpulse(new Vector2(-0.05f, 0), this.b2body.getWorldCenter(), true);
        }
        if (closestY > robotY) {
            this.b2body.applyLinearImpulse(new Vector2(0, 0.05f), this.b2body.getWorldCenter(), true);
        } else {
            this.b2body.applyLinearImpulse(new Vector2(0, -0.05f), this.b2body.getWorldCenter(), true);
        }

        // more advanced but still not really a pathfinding algorithm

//        System.out.println(currentDirection);
//        if (Math.abs(closestX - robotX) > border){
//            if (closestX > robotX) {
//                if (Math.abs(closestY - robotY) < border) {
//                    this.b2body.applyLinearImpulse(new Vector2(0.05f, 0), this.b2body.getWorldCenter(), true);
//                    return;
//                } else {
//                    this.b2body.applyLinearImpulse(new Vector2(0.05f, 0), this.b2body.getWorldCenter(), true);
//                    if (closestY > robotY) {
//                        this.b2body.applyLinearImpulse(new Vector2(0, 0.05f), this.b2body.getWorldCenter(), true);
//                    } else {
//                        this.b2body.applyLinearImpulse(new Vector2(0, -0.05f), this.b2body.getWorldCenter(), true);
//                    }
//                    return;
//                }
//            } else {
//                if (Math.abs(closestY - robotY) < border) {
//                    this.b2body.applyLinearImpulse(new Vector2(-0.05f, 0), this.b2body.getWorldCenter(), true);
//                    return;
//                } else {
//                    this.b2body.applyLinearImpulse(new Vector2(-0.05f, 0), this.b2body.getWorldCenter(), true);
//                    if (closestY > robotY) {
//                        this.b2body.applyLinearImpulse(new Vector2(0, 0.05f), this.b2body.getWorldCenter(), true);
//                    } else {
//                        this.b2body.applyLinearImpulse(new Vector2(0, -0.05f), this.b2body.getWorldCenter(), true);
//                    }
//                    return;
//                }
//            }
//        } else {
//            if (closestY > robotY) {
//                this.b2body.applyLinearImpulse(new Vector2(0, 0.05f), this.b2body.getWorldCenter(), true);
//            } else {
//                this.b2body.applyLinearImpulse(new Vector2(0, -0.05f), this.b2body.getWorldCenter(), true);
//            }
//        }
    }
    /**
     * Retrieves the current animation frame based on the robot's state and direction.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame.
     */
    public TextureRegion getFrame(float dt) {
        currentDirection = getRunDirection();

        TextureRegion region = null;

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
     * Retrieves the current running direction of the robot based on linear velocity.
     *
     * @return The current running direction of the robot (right side).
     */
    private runDirection getRunDirection() {
        float velocityX = Math.abs(b2body.getLinearVelocity().x); // We only care for positive x here so for example if running direction is upper left, then we want to return upper right
        float velocityY = b2body.getLinearVelocity().y;

        if (velocityX > VELOCITY_THRESHOLD) {
            if (velocityY > VELOCITY_THRESHOLD) return runDirection.UPPER;
            else if (velocityY < -VELOCITY_THRESHOLD) return runDirection.LOWER;
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
    public void defineRobot(float startX, float startY) {
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
