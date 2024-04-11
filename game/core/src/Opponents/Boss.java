package Opponents;

import Screens.PlayScreen;
import Screens.SettingsScreen;
import Tools.B2WorldCreator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGDXGame;
import serializableObjects.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;

public class Boss extends Sprite {
    private static final int FRAME_WIDTH = 100;
    private static final int FRAME_HEIGHT = 100;
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float BOSS_RADIUS = 50 / MyGDXGame.PPM;
    private static final float LINEAR_DAMPING = 4f;
    private static final float BOSS_HEIGHT = 100 / MyGDXGame.PPM;
    private static final float BOSS_WIDTH = 100 / MyGDXGame.PPM;
    private static final float VELOCITY_THRESHOLD = 0.5f;
    private static final int MAX_HEALTH = 300;

    // Enums for robot state and direction
    public enum State {
        STANDING
    }

    public enum runDirection {
        UPPER,
        RIGHT,
        LOWER,
        UP,
        DOWN,
        DEAD
    }

    public State currentState;
    public State prevState;
    public runDirection currentDirection;
    public runDirection prevRunDirection;
    public World world;
    public Body b2body;
    private Animation<TextureRegion> bossEastAndSouthEast;
    private Animation<TextureRegion> bossNorthAndNorthEast;
    private Animation<TextureRegion> bossSouth;
    private Animation<TextureRegion> bossSkill;
    private Animation<TextureRegion> bossAttackBottom;
    private Animation<TextureRegion> bossAttackTop;
    private Animation<TextureRegion> bossDeath;
    public ArrayList<TextureRegion> bossAllFrames = new ArrayList<>();
    private HashMap<TextureRegion, Integer> frameIndexMap = new HashMap<>();
    public boolean runningRight;
    private float stateTimer;
    public TextureRegion region;
    private int health;
    public boolean shouldBeDestroyed = false;
    private String uuid;
    private int counter = 0;
    private final int timeForExplosion = 48;
    private final Sound explosion = Gdx.audio.newSound(Gdx.files.internal("WeaponSounds/explosion.mp3"));
    public static final int EXPLOSION_DAMAGE = 20;

    /**
     * First constructor for a robot that gets created in the center of the map
     * if "B" is pressed.
     *
     * @param world
     * @param screen
     */
    public Boss(World world, PlayScreen screen) {
        super(screen.getBossAtlas().findRegion("boss"));
        this.world = world;
        currentState = State.STANDING;
        currentDirection = runDirection.RIGHT;
        stateTimer = 0;
        runningRight = true;

        initializeAnimations();
        defineBoss(screen.startPosX, screen.startPosY);

        // Put all frames into a hashmap, so we wouldn't have to search the whole list everytime we want to get the current frame's index
        for (int i = 0; i < bossAllFrames.size(); i++) {
            frameIndexMap.put(bossAllFrames.get(i), i);
        }

        setBounds(0, 0, BOSS_WIDTH, BOSS_HEIGHT);
        health = MAX_HEALTH;
    }

    /**
     * Second constructor.
     * It is used when a new player enters the game. Then the robot is spawned at
     * the right place on the screen.
     *
     * @param world
     * @param screen
     * @param posX
     * @param posY
     */
    public Boss(World world, PlayScreen screen, float posX, float posY, int health, String uuid) {
        super(screen.getRobotAtlas().findRegion("Robot"));
        this.world = world;
        currentState = State.STANDING;
        currentDirection = runDirection.RIGHT;
        stateTimer = 0;
        runningRight = true;

        initializeAnimations();
        defineBoss(posX * MyGDXGame.PPM, posY * MyGDXGame.PPM);
        region = bossSouth.getKeyFrame(0, true);
        setRegion(region);
        b2body.setAwake(true);

        // Put all frames into a hashmap, so we wouldn't have to search the whole list everytime we want to get the current frame's index
        for (int i = 0; i < bossAllFrames.size(); i++) {
            frameIndexMap.put(bossAllFrames.get(i), i);
        }

        setBounds(0, 0, BOSS_WIDTH, BOSS_HEIGHT);

        this.health = health;
        this.uuid = uuid;
    }

    /**
     * Initializes robot animations using sprite sheet regions.
     */
    private void initializeAnimations() {
        bossEastAndSouthEast = createAnimation(0, 7, 1);
        bossNorthAndNorthEast = createAnimation(0, 7, 2);
        bossSouth = createAnimation(0, 7, 3);
        bossSkill = createAnimation(0, 11, 4);
        bossAttackBottom = createAnimation(0, 6, 5);
        bossAttackTop = createAnimation(0, 5, 6);
        bossDeath = createAnimation(0, 17, 7);
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
            bossAllFrames.add(textureRegion);
        }
        return new Animation<>(ANIMATION_SPEED, frames);
    }

    /**
     * Updates the robot's position and sets the appropriate animation frame.
     *
     * @param delta The time elapsed since the last frame.
     */
    public void update(float delta) {
        if (health <= 0) {
            counter++;
            if (counter <= 1) {
                explosion.play(SettingsScreen.soundValue);
            }
            if (counter >= timeForExplosion) {
                B2WorldCreator.bossesToDestroy.add(this);
                explosion.dispose();
            }
        } else {
            updatePosition();
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
        region = getFrame(delta);
        setRegion(region);
        b2body.setAwake(true);
    }

    /**
     * Seeks for the closest enemy and moves the body of the robot in that direction.
     */
    private void updatePosition() {
        float shortestDistance = Float.MAX_VALUE;
        float closestX = 0;
        float closestY = 0;

        float robotX = this.b2body.getPosition().x;
        float robotY = this.b2body.getPosition().y;


        for (PlayerData info : MyGDXGame.playerDataMap.values()) {
            float playerX = info.getX();
            float playerY = info.getY();

            float deltaX = playerX - robotX;
            float deltaY = playerY - robotY;

            float actualDistance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (actualDistance < shortestDistance) {
                shortestDistance = actualDistance;
                closestX = playerX;
                closestY = playerY;
            }
        }
        if (closestX > robotX) {
            this.b2body.applyLinearImpulse(new Vector2(0.05f, 0), this.b2body.getWorldCenter(), true);
        } else {
            this.b2body.applyLinearImpulse(new Vector2(-0.05f, 0), this.b2body.getWorldCenter(), true);
        }
        if (closestY > robotY) {
            this.b2body.applyLinearImpulse(new Vector2(0, 0.05f), this.b2body.getWorldCenter(), true);
        } else {
            this.b2body.applyLinearImpulse(new Vector2(0, -0.05f), this.b2body.getWorldCenter(), true);
        }


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
            case UP:
            case UPPER:
                region = bossNorthAndNorthEast.getKeyFrame(stateTimer, true);
                break;
            case RIGHT:
            case LOWER:
                region = bossEastAndSouthEast.getKeyFrame(stateTimer, true);
                break;
            case DOWN:
                region = bossSouth.getKeyFrame(stateTimer, true);
                break;
            case DEAD:
                region = bossDeath.getKeyFrame(stateTimer, true);
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
        if (health <= 0) {
            return runDirection.DEAD;
        }
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
    public void defineBoss(float startX, float startY) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startX / MyGDXGame.PPM, startY / MyGDXGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(BOSS_RADIUS);

        fdef.shape = shape;
        fdef.filter.categoryBits = MyGDXGame.OPPONENT_CATEGORY;
        fdef.filter.maskBits = MyGDXGame.BULLET_CATEGORY | MyGDXGame.OTHER_PLAYER_CATEGORY | MyGDXGame.WORLD_CATEGORY | MyGDXGame.PLAYER_CATEGORY;
        b2body.createFixture(fdef);

        b2body.setUserData(this);

        // Set linear damping to simulate friction
        b2body.setLinearDamping(LINEAR_DAMPING);
    }

    /**
     * Getter method
     *
     * @return x
     */
    @Override
    public float getX() {
        return this.b2body.getPosition().x;
    }

    /**
     * Getter method
     *
     * @return y
     */
    @Override
    public float getY() {
        return this.b2body.getPosition().y;
    }

    /**
     * Getter method
     *
     * @return health
     */
    public int getHealth() {
        return health;
    }

    /**
     * Reduces the robot's health by the specified amount.
     *
     * @param damage The amount of damage to apply.
     */
    public void takeDamage(int damage) {
        health -= damage;

        if (health <= 0 && counter >= timeForExplosion) {
            shouldBeDestroyed = true;
            counter = 0;
        }
    }

    /**
     * Setter method
     *
     * @param UUID
     */
    public void setUuid(String UUID) {
        this.uuid = UUID;
    }

    /**
     * Getter method
     *
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }
}
