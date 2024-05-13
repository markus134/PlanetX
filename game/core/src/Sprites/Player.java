package Sprites;

import Items.Items;
import Screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.MyGDXGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Player extends Sprite {
    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 32;
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float PLAYER_RADIUS = 16 / MyGDXGame.PPM;
    private static final float LINEAR_DAMPING = 4f;
    private static final float PLAYER_HEIGHT = 64 / MyGDXGame.PPM;
    private static final float PLAYER_WIDTH = 64 / MyGDXGame.PPM;
    private static final float VELOCITY_THRESHOLD = 0.8f;
    private static final int MAX_HEALTH = 100;



    // Enums for player state and direction
    public enum State {
        RUNNING,
        STANDING,
        REVIVING,
        MINING,
        SHELL,
        DEAD
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
    private final TextureRegion playerStand;
    private Animation<TextureRegion> playerRunUpper;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerRunLower;
    private Animation<TextureRegion> playerRunUp;
    private Animation<TextureRegion> playerRunDown;
    private Animation<TextureRegion> playerMine;
    private Animation<TextureRegion> playerDeath;
    private Animation<TextureRegion> playerRevive;
    private Animation<TextureRegion> playerShell;
    private Animation<TextureRegion> playerRunUpperGun;
    private Animation<TextureRegion> playerRunGun;
    private Animation<TextureRegion> playerRunLowerGun;
    private Animation<TextureRegion> playerRunUpGun;
    private Animation<TextureRegion> playerRunDownGun;
    public ArrayList<TextureRegion> playerAllFrames = new ArrayList<>();
    private final HashMap<TextureRegion, Integer> frameIndexMap = new HashMap<>();
    public boolean runningRight;
    private float stateTimer;
    public TextureRegion region;
    private int health;
    public boolean shouldBeDestroyed = false;
    private final String uuid;
    private boolean isMining = false;
    private boolean isReviving = false;
    private boolean isFirstDeath = true;
    private boolean isDead = false;
    private int counter = 0;
    private final int timeForDeathAnimation = 5;
    private final PlayScreen playScreen;
    private long lastShotTime = 0;
    private float lastShotAngle = 0;


    /**
     * Constructor
     *
     * @param world
     * @param screen
     */
    public Player(World world, PlayScreen screen) {
        super(screen.getPlayerAtlas().findRegion("player_spritesheet"));

        this.world = world;
        this.playScreen = screen;

        currentState = State.STANDING;
        currentDirection = runDirection.RIGHT;
        stateTimer = 0;
        runningRight = true;

        initializeAnimations();
        definePlayer(screen.startPosX, screen.startPosY);

        playerStand = new TextureRegion(getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        playerAllFrames.add(playerStand);

        // Put all frames into a hashmap, so we wouldn't have to search the whole list everytime we want to get the current frame's index
        for (int i = 0; i < playerAllFrames.size(); i++) {
            frameIndexMap.put(playerAllFrames.get(i), i);
        }

        setBounds(0, 0, PLAYER_WIDTH, PLAYER_HEIGHT);
        setRegion(playerStand);

        health = MAX_HEALTH;
        this.uuid = UUID.randomUUID().toString();
    }

    /**
     * Apply linear force to player.
     *
     * @param xForce
     * @param yForce
     */
    public void move(float xForce, float yForce) {
        // System.out.println("move " + xForce + " " + yForce);
        b2body.applyLinearImpulse(new Vector2(xForce, yForce), b2body.getWorldCenter(), true);
    }

    /**
     * Initializes player animations using sprite sheet regions.
     */
    private void initializeAnimations() {
        playerRunUpper = createAnimation(0, 3, 1);
        playerRunLower = createAnimation(0, 3, 2);
        playerRun = createAnimation(0, 5, 3);
        playerRunDown = createAnimation(0, 3, 4);
        playerRunUp = createAnimation(0, 3, 5);

        playerMine = createAnimation(0, 3, 6);
        playerDeath = createAnimation(0, 4, 7);
        playerRevive = createAnimation(0, 4, 8);
        playerShell = createAnimation(0, 4, 9);

        playerRunGun = createAnimation(0, 5, 10);
        playerRunUpperGun = createAnimation(0, 3, 11);
        playerRunLowerGun = createAnimation(0, 3, 12);
        playerRunUpGun = createAnimation(0, 3, 13);
        playerRunDownGun = createAnimation(0, 3, 14);
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
            playerAllFrames.add(textureRegion);
        }
        return new Animation<>(ANIMATION_SPEED, frames);
    }

    /**
     * Updates the player's position and sets the appropriate animation frame.
     *
     * @param delta The time elapsed since the last frame.
     */
    public void update(float delta) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        region = getFrame(delta);
        setRegion(region);
    }

    /**
     * Retrieves the current animation frame based on the player's state and direction.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame.
     */
    public TextureRegion getFrame(float dt) {
        currentState = getState();

        // Check if highlighted inventory item is a gun
        boolean hasGun = playScreen.hud.getHighlightedItem().equals(Items.BLASTER);

        // Handle special states
        switch (currentState) {
            case SHELL:
                return handleShellState(dt);
            case DEAD:
                return handleDeadState(dt);
            case REVIVING:
                return handleRevivingState(dt);
            case MINING:
                return handleMiningState(dt);
            case STANDING:
                return handleStandingState(dt);
            default:
                currentDirection = getRunDirection();
                return handleRunningState(dt, hasGun);
        }
    }

    /**
     * Handles the SHELL state animation.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame for the SHELL state.
     */
    private TextureRegion handleShellState(float dt) {
        flipRegionIfNeeded(dt, region);
        return playerShell.getKeyFrame(stateTimer, false);
    }

    /**
     * Handles the DEAD state animation.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame for the DEAD state.
     */
    private TextureRegion handleDeadState(float dt) {
        counter++;
        if (counter > timeForDeathAnimation) {
            shouldBeDestroyed = true;
        }
        TextureRegion textureRegion = playerDeath.getKeyFrame(stateTimer, false);
        flipRegionIfNeeded(dt, textureRegion);
        return textureRegion;
    }

    /**
     * Handles the REVIVING state animation.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame for the REVIVING state.
     */
    private TextureRegion handleRevivingState(float dt) {
        TextureRegion textureRegion = playerRevive.getKeyFrame(stateTimer, true);
        flipRegionIfNeeded(dt, textureRegion);
        return textureRegion;
    }

    /**
     * Handles the MINING state animation.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame for the MINING state.
     */
    private TextureRegion handleMiningState(float dt) {
        TextureRegion textureRegion = playerMine.getKeyFrame(stateTimer, true);
        flipRegionIfNeeded(dt, textureRegion);
        return textureRegion;
    }

    /**
     * Handles the STANDING state animation.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame for the STANDING state.
     */
    private TextureRegion handleStandingState(float dt) {
        TextureRegion textureRegion = playerStand;
        flipRegionIfNeeded(dt, textureRegion);
        return textureRegion;
    }

    /**
     * Handles the animation for running states (UPPER, RIGHT, LOWER, UP, DOWN).
     *
     * @param dt     The time elapsed since the last frame.
     * @param hasGun A boolean indicating whether the player has a gun.
     * @return The current animation frame for the running state.
     */
    private TextureRegion handleRunningState(float dt, boolean hasGun) {
        TextureRegion region;
        switch (currentDirection) {
            case UPPER:
                region = hasGun ? playerRunUpperGun.getKeyFrame(stateTimer, true) : playerRunUpper.getKeyFrame(stateTimer, true);
                break;
            case RIGHT:
                region = hasGun ? playerRunGun.getKeyFrame(stateTimer, true) : playerRun.getKeyFrame(stateTimer, true);
                break;
            case LOWER:
                region = hasGun ? playerRunLowerGun.getKeyFrame(stateTimer, true) : playerRunLower.getKeyFrame(stateTimer, true);
                break;
            case UP:
                region = hasGun ? playerRunUpGun.getKeyFrame(stateTimer, true) : playerRunUp.getKeyFrame(stateTimer, true);
                break;
            case DOWN:
                region = hasGun ? playerRunDownGun.getKeyFrame(stateTimer, true) : playerRunDown.getKeyFrame(stateTimer, true);
                break;
            default:
                region = playerStand;
                break;
        }
        flipRegionIfNeeded(dt, region);
        return region;
    }




    /**
     * Flips the given TextureRegion based on the player's state, direction, and last shot angle.
     *
     * @param dt     The time elapsed since the last frame.
     * @param region The TextureRegion to flip.
     */
    private void flipRegionIfNeeded(float dt, TextureRegion region) {
        // Start the animation from the start if currentState or currentDirection have changed
        stateTimer = (currentState == prevState && currentDirection == prevRunDirection) ? stateTimer + dt : 0;

        boolean isRecentShot = recentlyShot();

        if (isRecentShot) {
            runningRight = lastShotAngle <= 90 && lastShotAngle >= -90;
        }

        if ((!runningRight && !region.isFlipX()) || (runningRight && region.isFlipX())) {
            region.flip(true, false);
        }

        if (!isRecentShot) {
            // We will check if linear velocity indicates right or left movement and also check whether the region is already flipped
            if (b2body.getLinearVelocity().x < -0.1 && !region.isFlipX()) {
                region.flip(true, false);
                runningRight = false;

            } else if (b2body.getLinearVelocity().x > 0.1 && region.isFlipX()) {
                region.flip(true, false);
                runningRight = true;
            }
        }

        prevState = currentState;
        prevRunDirection = currentDirection;
    }



    /**
     * Retrieves the current state of the player (standing or running) based on linear velocity.
     *
     * @return The current state of the player.
     */
    private State getState() {
        float velocityX = b2body.getLinearVelocity().x;
        float velocityY = b2body.getLinearVelocity().y;
        float velocityThreshold = VELOCITY_THRESHOLD; // Added a velocity threshold as we want the standing texture to be rendered right away not when velocity reaches zero

        if (isDead && isFirstDeath && !playScreen.game.playerDict.isEmpty()) {
            return State.SHELL;
        } else if (isDead) {
            return State.DEAD;
        } else if (isReviving) {
            return State.REVIVING;
        } else if (isMining) {
            return State.MINING;
        } else if (Math.abs(velocityX) < velocityThreshold && Math.abs(velocityY) < velocityThreshold) {
            return State.STANDING;
        }

        return State.RUNNING;
    }

    /**
     * Retrieves the current running direction of the player based on linear velocity and last shot angle
     * if the last shot occurred less than 1 second ago.
     *
     * @return The current running direction of the player.
     */
    private runDirection getRunDirection() {
        float velocityX = Math.abs(b2body.getLinearVelocity().x);
        float velocityY = b2body.getLinearVelocity().y;

        if (recentlyShot()) {
            float lastShotAngle = getLastShotAngle();

            if (lastShotAngle > 67.5 && lastShotAngle <= 112.5)
                return runDirection.UP;
            else if ((lastShotAngle > 22.5 && lastShotAngle <= 67.5) || (lastShotAngle > 112.5 && lastShotAngle <= 157.5)) {
                return runDirection.UPPER;
            } else if ((lastShotAngle > -22.5 && lastShotAngle <= 22.5) || (lastShotAngle < -157.5 || lastShotAngle > 157.5)) {
                return runDirection.RIGHT;
            } else if ((lastShotAngle > -67.5 && lastShotAngle <= -22.5) || (lastShotAngle < -112.5 && lastShotAngle >= -157.5)) {
                return runDirection.LOWER;
            } else {
                return runDirection.DOWN;
            }
        } else {
            if (velocityX > 0) {
                if (velocityY > 0) return runDirection.UPPER;
                else if (velocityY < 0) return runDirection.LOWER;
                else return runDirection.RIGHT;
            } else {
                if (velocityY > 0) return runDirection.UP;
                else if (velocityY < 0) return runDirection.DOWN;
                else return null; // We should never reach this point hopefully as it means that the player isn't running
            }
        }
    }


    /**
     * Defines the player's Box2D body and fixture.
     */
    public void definePlayer(float startX, float startY) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startX / MyGDXGame.PPM, startY / MyGDXGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        b2body.setUserData(this);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(PLAYER_RADIUS);

        fdef.shape = shape;
        fdef.filter.categoryBits = MyGDXGame.PLAYER_CATEGORY;
        fdef.filter.maskBits = MyGDXGame.BULLET_CATEGORY | MyGDXGame.OTHER_PLAYER_CATEGORY | MyGDXGame.WORLD_CATEGORY | MyGDXGame.OPPONENT_CATEGORY;

        b2body.createFixture(fdef);

        // Set linear damping to simulate friction
        b2body.setLinearDamping(LINEAR_DAMPING);
    }

    public int getHealth() {
        return health;
    }

    /**
     * Reduces the robot's health by the specified amount.
     *
     * @param damage The amount of damage to apply.
     */
    public void takeDamage(int damage) {
        if (isDead) return;
        health -= damage;

        if (health <= 0) {
            isDead = true;
        }
    }

    public void recoverHealth(int recoverHealth) {
        health = Math.min(MAX_HEALTH, health + recoverHealth);
    }

    public void revive() {
        health = 20;
        isDead = false;
        isFirstDeath = false;
    }

    /**
     * Getter method
     *
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    public void setIsMining(boolean isMining) {
        this.isMining = isMining;
    }

    public boolean getIsMining() {
        return isMining;
    }

    public void setIsReviving(boolean isReviving) {
        this.isReviving = isReviving;
    }

    public boolean getIsReviving() {
        return isReviving;
    }

    public boolean isInShell() {
        return isFirstDeath && isDead;
    }

    public long getLastShotTime() {
        return lastShotTime;
    }

    public void setLastShotTime(long lastShotTime) {
        this.lastShotTime = lastShotTime;
    }

    public float getLastShotAngle() {
        return lastShotAngle;
    }

    public void setLastShotAngle(float lastShotAngle) {
        this.lastShotAngle = lastShotAngle;
    }

    /**
     * Check if we have recently shot a bullet.
     * Recent in this case means less than 0.5s ago.
     * @return true if player has recently shot, otherwise false
     */
    public boolean recentlyShot() {
        long currentTime = TimeUtils.nanoTime();
        long lastShotTime = getLastShotTime();

        return (currentTime - lastShotTime) < 500_000_000L; // 0.5 second in nanoseconds
    }
}
