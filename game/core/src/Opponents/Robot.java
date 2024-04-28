package Opponents;

import Screens.PlayScreen;
import Screens.SettingsScreen;
import Tools.B2WorldCreator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MyGDXGame;

public class Robot extends Opponent {

    private static final float ROBOT_RADIUS = 16 / MyGDXGame.PPM;
    private static final float ROBOT_HEIGHT = 64 / MyGDXGame.PPM;
    private static final float ROBOT_WIDTH = 64 / MyGDXGame.PPM;
    private static final int FRAME_WIDTH = 48;
    private static final int FRAME_HEIGHT = 48;

    public static final int MAX_HEALTH = 100;

    public World world;
    private Animation<TextureRegion> robotRunUpper;
    private Animation<TextureRegion> robotRun;
    private Animation<TextureRegion> robotRunLower;
    private Animation<TextureRegion> robotRunUp;
    private Animation<TextureRegion> robotRunDown;
    private Animation<TextureRegion> robotExplode;
    public TextureRegion region;
    private final Sound explosion = Gdx.audio.newSound(Gdx.files.internal("WeaponSounds/explosion.mp3"));
    public static final int EXPLOSION_DAMAGE = 20;
    private static final int timeForExplosion = 40;
    private static final int mobId = 1;


    /**
     * First constructor for a robot that gets created in the center of the map
     * if "B" is pressed.
     *
     * @param world
     * @param screen
     */
    public Robot(World world, PlayScreen screen) {
        super(screen.getRobotAtlas().findRegion("Robot"), world, screen, MAX_HEALTH);

        System.out.println("spawning robot 1");


        initializeAnimations();
        defineOpponent(screen.startPosX, screen.startPosY, ROBOT_RADIUS);

        setBounds(0, 0, ROBOT_WIDTH, ROBOT_HEIGHT);
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
    public Robot(World world, PlayScreen screen, float posX, float posY, int health, String uuid) {
        super(screen.getRobotAtlas().findRegion("Robot"), world, screen, health);

        System.out.println("spawning robot 2");

        setUuid(uuid);

        initializeAnimations();
        defineOpponent(posX * MyGDXGame.PPM, posY * MyGDXGame.PPM, ROBOT_RADIUS);
        region = robotRunUp.getKeyFrame(0, true);
        setRegion(region);
        b2body.setAwake(true);


        setBounds(0, 0, ROBOT_WIDTH, ROBOT_HEIGHT);
    }

    /**
     * Initializes robot animations using sprite sheet regions.
     */
    private void initializeAnimations() {
        robotRun = createAnimation(0, 3, 0, Robot.FRAME_WIDTH, Robot.FRAME_HEIGHT);
        robotRunUpper = createAnimation(0, 3, 1, Robot.FRAME_WIDTH, Robot.FRAME_HEIGHT);
        robotRunLower = createAnimation(0, 3, 2, Robot.FRAME_WIDTH, Robot.FRAME_HEIGHT);
        robotRunDown = createAnimation(0, 3, 3, Robot.FRAME_WIDTH, Robot.FRAME_HEIGHT);
        robotRunUp = createAnimation(0, 3, 4, Robot.FRAME_WIDTH, Robot.FRAME_HEIGHT);
        robotExplode = createAnimation(0, 3, 5, Robot.FRAME_WIDTH, Robot.FRAME_HEIGHT);
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
                playScreen.b2WorldCreator.markOpponentAsDestroyed(this);
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
            case DEAD:
                region = robotExplode.getKeyFrame(stateTimer, true);
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
     * Get mob id.
     * @return mob id
     */
    public int getMobId() {
        return mobId;
    }

}
