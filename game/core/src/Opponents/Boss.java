package Opponents;

import Screens.PlayScreen;
import Sprites.OtherPlayer;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.MyGDXGame;
import serializableObjects.PlayerData;

public class Boss extends Opponent {

    private static final float BOSS_RADIUS = 50 / MyGDXGame.PPM;
    private static final float BOSS_HEIGHT = 300 / MyGDXGame.PPM;
    private static final float BOSS_WIDTH = 300 / MyGDXGame.PPM;
    public static final int MAX_HEALTH = 300;
    private static final int FRAME_WIDTH = 100;
    private static final int FRAME_HEIGHT = 100;
    private Animation<TextureRegion> bossEastAndSouthEast;
    private Animation<TextureRegion> bossNorthAndNorthEast;
    private Animation<TextureRegion> bossSouth;
    private Animation<TextureRegion> bossSkill;
    private Animation<TextureRegion> bossAttackBottom;
    private Animation<TextureRegion> bossAttackTop;
    private Animation<TextureRegion> bossDeath;
    private TextureRegion region;
    private final PlayScreen playScreen;

    private static final int timeForDeath = 100;
    private static final float PLAYER_PROXIMITY_THRESHOLD = 1.3f;
    private static final float VELOCITY_THRESHOLD = 0.8f;
    private static final int mobId = 2;
    private static final int ATTACK_DAMAGE = 20;
    private static final float ATTACK_DURATION = 0.8f;
    private static final int TELEPORT_INTERVAL = 15; // Teleport interval in seconds
    private PlayerData closestPlayer;
    private boolean teleporting = false;


    /**
     * First constructor for a boss that gets created in the center of the map
     * if "R" is pressed.
     *
     * @param world
     * @param screen
     */
    public Boss(World world, PlayScreen screen) {
        super(screen.getBossAtlas().findRegion("boss"), world, screen, MAX_HEALTH);

        this.playScreen = screen;
        initializeAnimations();
        defineOpponent(screen.startPosX, screen.startPosY, BOSS_RADIUS);
        region = bossSouth.getKeyFrame(0, true);
        setRegion(region);
        b2body.setAwake(true);

        setBounds(0, 0, BOSS_WIDTH, BOSS_HEIGHT);

        startTeleportTimer();
    }

    /**
     * Second constructor.
     * It is used when a new player enters the game. Then the boss is spawned at
     * the right place on the screen.
     *
     * @param world
     * @param screen
     * @param posX
     * @param posY
     */
    public Boss(World world, PlayScreen screen, float posX, float posY, int health, String uuid, long bossSpawnTime) {
        super(screen.getBossAtlas().findRegion("boss"), world, screen, health);

        this.playScreen = screen;
        setUuid(uuid);

        setSpawnTime(bossSpawnTime);

        initializeAnimations();
        defineOpponent(posX * MyGDXGame.PPM, posY * MyGDXGame.PPM, BOSS_RADIUS);
        region = bossSouth.getKeyFrame(0, true);
        setRegion(region);
        b2body.setAwake(true);

        setBounds(0, 0, BOSS_WIDTH, BOSS_HEIGHT);

        startTeleportTimer();
    }

    /**
     * Initializes boss animations using sprite sheet regions.
     */
    private void initializeAnimations() {
        bossEastAndSouthEast = createAnimation(0, 7, 0, Boss.FRAME_WIDTH, Boss.FRAME_HEIGHT);
        bossNorthAndNorthEast = createAnimation(0, 7, 1, Boss.FRAME_WIDTH, Boss.FRAME_HEIGHT);
        bossSouth = createAnimation(0, 7, 2, Boss.FRAME_WIDTH, Boss.FRAME_HEIGHT);
        bossSkill = createAnimation(0, 11, 3, Boss.FRAME_WIDTH, Boss.FRAME_HEIGHT);
        bossAttackBottom = createAnimation(0, 6, 4, Boss.FRAME_WIDTH, Boss.FRAME_HEIGHT);
        bossAttackTop = createAnimation(0, 5, 5, Boss.FRAME_WIDTH, Boss.FRAME_HEIGHT);
        bossDeath = createAnimation(0, 17, 6, Boss.FRAME_WIDTH, Boss.FRAME_HEIGHT);
    }

    /**
     * Updates the boss's position and sets the appropriate animation frame.
     *
     * @param delta The time elapsed since the last frame.
     */
    public void update(float delta) {
        if (health <= 0) {
            counter++;
            if (counter >= timeForDeath) {
                playScreen.b2WorldCreator.markOpponentAsDestroyed(this);
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
     * Retrieves the current state of the player (standing or running) based on linear velocity.
     *
     * @return The current state of the player.
     */
    private State getState() {
        float velocityX = b2body.getLinearVelocity().x;
        float velocityY = b2body.getLinearVelocity().y;
        float velocityThreshold = VELOCITY_THRESHOLD; // Added a velocity threshold as we want the standing texture to be rendered right away not when velocity reaches zero

        if (health <= 0) {
            return State.DEAD;
        } else if (playerIsClose()) {
            if (playerIsAbove()) {
                return State.ATTACK_TOP;
            } else {
                return State.ATTACK_BOTTOM;
            }
        } else if (Math.abs(velocityX) < velocityThreshold && Math.abs(velocityY) < velocityThreshold) {
            return State.STANDING;
        }

        return State.RUNNING;
    }

    /**
     * Retrieves the current animation frame based on the boss's state and direction.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame.
     */
    public TextureRegion getFrame(float dt) {
        currentState = getState();

        if (currentState == State.ATTACK_TOP || currentState == State.ATTACK_BOTTOM) {
            if (stateTimer >= ATTACK_DURATION) {
                // Cause damage to the closest player
                String closestPlayerUuid = closestPlayer.getUuid();

                if (playScreen.game.playerHashMapByUuid.containsKey(closestPlayerUuid)) {
                    OtherPlayer otherPlayer =  playScreen.game.playerHashMapByUuid.get(closestPlayerUuid);

                    otherPlayer.takeDamage(ATTACK_DAMAGE);
                } else {
                    playScreen.player.takeDamage(ATTACK_DAMAGE);
                }
                // Reset state timer to restart the animation
                stateTimer = 0;
            }

            TextureRegion textureRegion = currentState == State.ATTACK_TOP ?
                    bossAttackTop.getKeyFrame(stateTimer, true) :
                    bossAttackBottom.getKeyFrame(stateTimer, true);
            flipRegionIfNeeded(dt, textureRegion);

            return textureRegion;

        } else if (teleporting) {
            // Use bossSkill animation while teleporting
            TextureRegion textureRegion = bossSkill.getKeyFrame(stateTimer, true);
            flipRegionIfNeeded(dt, textureRegion);
            return textureRegion;
        } else if (currentState == State.DEAD) {
            // Handle dead state animation
            TextureRegion textureRegion = bossDeath.getKeyFrame(stateTimer, true);
            flipRegionIfNeeded(dt, textureRegion);

            return textureRegion;
        } else if (currentState == State.STANDING) {
            // Handle standing state animation
            TextureRegion textureRegion = bossSouth.getKeyFrame(stateTimer, true);
            flipRegionIfNeeded(dt, textureRegion);

            return textureRegion;
        }

        // Handle other states (running)
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
        }

        flipRegionIfNeeded(dt, region);

        return region;
    }


    /**
     * Flip region if needed.
     * @param dt
     * @param region
     */
    private void flipRegionIfNeeded(float dt, TextureRegion region) {
        // Start the animation from the start if currentState or currentDirection have changed
        stateTimer = (currentState == prevState && currentDirection == prevRunDirection) ? stateTimer + dt : 0;

        if (currentState != State.RUNNING && currentState != State.STANDING) {
            if ((!runningRight && !region.isFlipX()) || (runningRight && region.isFlipX())) {
                region.flip(true, false);
            }
        } else {
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
     * Check if any player is close to the boss.
     * @return true if some player is close, otherwise false
     */
    private boolean playerIsClose() {
        float shortestDistance = calculateClosestPlayer();

        System.out.println(shortestDistance);
        // Check if the distance to the closest player is less than the threshold
        return shortestDistance < PLAYER_PROXIMITY_THRESHOLD;
    }


    /**
     * Check whether the player is above the boss. This function assumes that you have already found the closest player.
     * @return true if player is above the boss, otherwise false
     */
    private boolean playerIsAbove() {
        // Get the position of the player
        float playerY = closestPlayer.getY();

        // Get the position of the boss
        float bossY = b2body.getPosition().y;

        // Check if player is above the boss
        return playerY > bossY;
    }

    /**
     * Starts a timer to teleport the boss to the closest player every 15 seconds.
     */
    public void startTeleportTimer() {
        // Calculate the time elapsed since the boss was spawned. This is done to avoid synchronization issues.
        float timeElapsed = (System.currentTimeMillis() - getSpawnTime()) / 1000f;

        // Calculate the remaining time before the next teleport event
        float remainingTime = TELEPORT_INTERVAL - (timeElapsed % TELEPORT_INTERVAL);

        // Start the teleport timer with the adjusted start time
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                teleportToClosestPlayer();
            }
        }, remainingTime, TELEPORT_INTERVAL);
    }


    /**
     * Teleports the boss to the closest player.
     */
    private void teleportToClosestPlayer() {
        calculateClosestPlayer();

        if (closestPlayer != null && health > 0) {
            // Set teleporting to true so we could start the animation
            teleporting = true;

            // Add a bit of time to so animation could run its course before we teleport
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // Teleport to player's position
                    b2body.setTransform(closestPlayer.getX(), closestPlayer.getY(), 0);
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            teleporting = false;
                        }
                    }, 0.2f);
                }
            }, 0.8f);
        }
    }

    /**
     * Finds the closest alive player to the boss.
     */
    private float calculateClosestPlayer() {
        closestPlayer = null;

        // Get the position of the boss
        float bossX = b2body.getPosition().x;
        float bossY = b2body.getPosition().y;

        // Initialize variables to keep track of closest player
        float shortestDistance = Float.MAX_VALUE;

        // Iterate over all players to find the closest one
        for (PlayerData info : playScreen.game.playerDataMap.values()) {
            if (info.getHealth() <= 0) continue;
            float playerX = info.getX();
            float playerY = info.getY();

            float deltaX = playerX - bossX;
            float deltaY = playerY - bossY;

            float actualDistance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (actualDistance < shortestDistance) {
                shortestDistance = actualDistance;
                closestPlayer = info;
            }
        }

        return shortestDistance;
    }

    /**
     * Get mob id.
     * @return mob id
     */
    public int getMobId() {
        return mobId;
    }
}
