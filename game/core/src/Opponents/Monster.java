package Opponents;

import Screens.PlayScreen;
import Sprites.OtherPlayer;
import Sprites.Player;
import Tools.B2WorldCreator;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MyGDXGame;
import serializableObjects.PlayerData;

public class Monster extends Opponent {

    private static final float MONSTER_RADIUS = 24 / MyGDXGame.PPM;
    private static final float MONSTER_HEIGHT = 100 / MyGDXGame.PPM;
    private static final float MONSTER_WIDTH = 100 / MyGDXGame.PPM;
    private static final int FRAME_WIDTH = 64;
    private static final int FRAME_HEIGHT = 64;

    public static final int MAX_HEALTH = 100;

    public World world;
    private Animation<TextureRegion> monsterEast;
    private Animation<TextureRegion> monsterSouth;
    private Animation<TextureRegion> monsterNorth;
    private Animation<TextureRegion> monsterDeath;
    public TextureRegion region;
    private static final int timeForDeath = 40;
    private static final int mobId = 3;
    private static final float DAMAGE_DISTANCE_THRESHOLD = 1.0f;
    private static final float ATTACK_DURATION = 0.8f;
    private static final int ATTACK_DAMAGE = 10;
    private PlayerData closestPlayer;


    /**
     * First constructor for a monster that gets created in the center of the map
     * if "M" is pressed.
     *
     * @param world
     * @param screen
     */
    public Monster(World world, PlayScreen screen) {
        super(screen.getMonsterAtlas().findRegion("monster"), world, screen, MAX_HEALTH, timeForDeath);

        initializeAnimations();
        defineOpponent(screen.startPosX, screen.startPosY, MONSTER_RADIUS);

        setBounds(0, 0, MONSTER_WIDTH, MONSTER_HEIGHT);
    }

    /**
     * Second constructor.
     * It is used when a new player enters the game. Then the monster is spawned at
     * the right place on the screen.
     *
     * @param world
     * @param screen
     * @param posX
     * @param posY
     */
    public Monster(World world, PlayScreen screen, float posX, float posY, int health, String uuid) {
        super(screen.getMonsterAtlas().findRegion("monster"), world, screen, health, timeForDeath);

        setUuid(uuid);

        initializeAnimations();
        defineOpponent(posX * MyGDXGame.PPM, posY * MyGDXGame.PPM, MONSTER_RADIUS);
        region = monsterSouth.getKeyFrame(0, true);
        setRegion(region);
        b2body.setAwake(true);


        setBounds(0, 0, MONSTER_WIDTH, MONSTER_HEIGHT);
    }

    /**
     * Initializes monster animations using sprite sheet regions.
     */
    private void initializeAnimations() {
        monsterEast = createAnimation(0, 5, 0, Monster.FRAME_WIDTH, Monster.FRAME_HEIGHT);
        monsterSouth = createAnimation(0, 5, 1, Monster.FRAME_WIDTH, Monster.FRAME_HEIGHT);
        monsterNorth = createAnimation(0, 5, 2, Monster.FRAME_WIDTH, Monster.FRAME_HEIGHT);
        monsterDeath = createAnimation(0, 8, 3, Monster.FRAME_WIDTH, Monster.FRAME_HEIGHT);
    }

    /**
     * Updates the monster's position and sets the appropriate animation frame.
     *
     * @param delta The time elapsed since the last frame.
     */
    public void update(float delta) {
        if (health <= 0) {
            counter++;
            if (counter >= timeForDeath) {
                B2WorldCreator.markOpponentAsDestroyed(this);
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
     * Checks if any player is close to the monster.
     *
     * @return true if some player is close, otherwise false.
     */
    private boolean playerIsClose() {
        // Get the position of the monster
        float monsterX = b2body.getPosition().x;
        float monsterY = b2body.getPosition().y;

        // Initialize variables to keep track of closest player
        float shortestDistance = Float.MAX_VALUE;

        // Iterate over all players to find the closest one
        for (PlayerData info : playScreen.game.playerDataMap.values()) {
            float playerX = info.getX();
            float playerY = info.getY();

            float deltaX = playerX - monsterX;
            float deltaY = playerY - monsterY;

            float actualDistance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (actualDistance < shortestDistance) {
                shortestDistance = actualDistance;
                closestPlayer = info;
            }
        }

        // Check if the distance to the closest player is less than the threshold
        return shortestDistance < DAMAGE_DISTANCE_THRESHOLD;
    }
    /**
     * Retrieves the current animation frame based on the monster's state and direction.
     *
     * @param dt The time elapsed since the last frame.
     * @return The current animation frame.
     */
    public TextureRegion getFrame(float dt) {
        if (playerIsClose()) {
            if (stateTimer >= ATTACK_DURATION) {
                // Cause damage to the closest player
                String closestPlayerUuid = closestPlayer.getUuid();

                if (playScreen.game.playerHashMapByUuid.containsKey(closestPlayerUuid)) {
                    OtherPlayer otherPlayer = playScreen.game.playerHashMapByUuid.get(closestPlayerUuid);

                    otherPlayer.takeDamage(ATTACK_DAMAGE);
                } else {
                    playScreen.player.takeDamage(ATTACK_DAMAGE);
                }
                // Reset state timer to restart the animation
                stateTimer = 0;
            }
        }


        currentDirection = getRunDirection();

        TextureRegion region = null;

        // We will only check the right side directions. If it's left, then we can flip the region
        // The UPPER and LOWER mean upper right and lower right respectively (maybe change the names)
        switch (currentDirection) {
            case UPPER:
            case RIGHT:
            case LOWER:
                region = monsterEast.getKeyFrame(stateTimer, true);
                break;
            case UP:
                region = monsterNorth.getKeyFrame(stateTimer, true);
                break;
            case DOWN:
                region = monsterSouth.getKeyFrame(stateTimer, true);
                break;
            case DEAD:
                region = monsterDeath.getKeyFrame(stateTimer, true);
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
