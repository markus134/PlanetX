package Opponents;

import Opponents.astar.AStar;
import Opponents.astar.Node;
import Opponents.astar.TileMapReader;
import Screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGDXGame;
import serializableObjects.PlayerData;

import java.util.*;

public abstract class Opponent extends Sprite {
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float LINEAR_DAMPING = 4f;
    private static final float VELOCITY_THRESHOLD = 0.5f;
    private static final long PATH_UPDATE_INTERVAL = 300;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    protected int health;
    private String uuid;
    protected int counter = 0;
    protected final ArrayList<TextureRegion> allFrames = new ArrayList<>();
    protected Body b2body;
    protected World world;
    protected PlayScreen playScreen;
    protected State currentState;
    protected State prevState;
    protected RunDirection currentDirection;
    protected RunDirection prevRunDirection;
    protected boolean runningRight;
    protected float stateTimer;
    private final Timer pathUpdateTimer;
    private List<Node> path;
    protected long spawnTime;
    private int currentTargetIndex = 0;

    public Opponent(TextureAtlas.AtlasRegion atlas, World world, PlayScreen screen, int health) {
        super(atlas);

        this.world = world;
        this.playScreen = screen;
        this.health = health;

        currentState = State.STANDING;
        currentDirection = RunDirection.RIGHT;
        stateTimer = 0;
        runningRight = true;
        spawnTime = System.currentTimeMillis();

        pathUpdateTimer = new Timer();

        if (playScreen.isSinglePlayerWorld()) {
            pathUpdateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updatePath();
                }
            }, PATH_UPDATE_INTERVAL, PATH_UPDATE_INTERVAL);
        }
    }

    /**
     * Creates an animation from specified sprite sheet region parameters.
     *
     * @param startFrame The starting frame index in the sprite sheet.
     * @param endFrame   The ending frame index in the sprite sheet.
     * @param row        The row index in the sprite sheet.
     * @return The created animation.
     */
    protected Animation<TextureRegion> createAnimation(int startFrame, int endFrame, int row, int frameWidth, int frameHeight) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = startFrame; i <= endFrame; i++) {
            TextureRegion textureRegion = new TextureRegion(getTexture(), i * frameWidth, row * frameHeight, frameWidth, frameHeight);
            frames.add(textureRegion);
            allFrames.add(textureRegion);
        }
        return new Animation<>(ANIMATION_SPEED, frames);
    }

    /**
     * Seeks for the closest enemy and moves the body of the opponent in that direction.
     */
    protected void updatePosition() {
        if (playScreen.isSinglePlayerWorld()) {
            updatePositionSinglePlayerWorld();
        } else {
            updatePositionMultiplayerWorld();
        }
    }

    /**
     * Update position on single player world. On single player worlds the opponent uses the A* algorithm
     */
    public void updatePositionSinglePlayerWorld() {
        if (path == null || path.isEmpty()) return;

        float opponentX = this.b2body.getPosition().x - getWidth() / 2;
        float opponentY = this.b2body.getPosition().y - getHeight() / 2;

        int enemyX = (int) (opponentX * 100 / TILE_WIDTH);
        int enemyY = (int) (opponentY * 100 / TILE_HEIGHT);

        // Ensure currentTargetIndex is within bounds
        if (currentTargetIndex >= path.size()) {
            currentTargetIndex = path.size() - 1;
        }

        Node target = path.get(currentTargetIndex);

        int targetY = target.getRow();
        int targetX = target.getCol();

        // If the opponent reaches the current target node, update the target
        if (enemyX == targetX && enemyY == targetY) {
            currentTargetIndex++;
            if (currentTargetIndex >= path.size()) {
                // Reached the end of the path
                return;
            }
            target = path.get(currentTargetIndex);
            targetY = target.getRow();
            targetX = target.getCol();
        }


        if (targetX - enemyX != 0) {
            if (targetX > enemyX) {
                this.b2body.applyLinearImpulse(new Vector2(0.05f, 0f), this.b2body.getWorldCenter(), true);
            } else {
                this.b2body.applyLinearImpulse(new Vector2(-0.05f, 0f), this.b2body.getWorldCenter(), true);
            }
        }

        if (targetY - enemyY != 0) {
            if (targetY > enemyY) {
                this.b2body.applyLinearImpulse(new Vector2(0f, 0.05f), this.b2body.getWorldCenter(), true);
            } else {
                this.b2body.applyLinearImpulse(new Vector2(0f, -0.05f), this.b2body.getWorldCenter(), true);
            }
        }
    }

    /**
     * Update position on multiplayer world. This doesn't use A* as it caused significant issues with synchronization.
     */
    public void updatePositionMultiplayerWorld() {
        float shortestDistance = Float.MAX_VALUE;
        float closestX = 0;
        float closestY = 0;

        float opponentX = this.b2body.getPosition().x;
        float opponentY = this.b2body.getPosition().y;


        for (PlayerData info : playScreen.game.playerDataMap.values()) {
            if (info.getHealth() <= 0) continue; // Don't try to go towards dead players

            float playerX = info.getX();
            float playerY = info.getY();

            float deltaX = playerX - opponentX;
            float deltaY = playerY - opponentY;

            float actualDistance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (actualDistance < shortestDistance) {
                shortestDistance = actualDistance;
                closestX = playerX;
                closestY = playerY;
            }
        }


        // If we haven't found any player then don't go anywhere
        if (shortestDistance == Float.MAX_VALUE) return;

        if (Math.abs(closestX - opponentX) > 0.1) {
            if (closestX > opponentX) {
                this.b2body.applyLinearImpulse(new Vector2(0.05f, 0), this.b2body.getWorldCenter(), true);
            } else {
                this.b2body.applyLinearImpulse(new Vector2(-0.05f, 0), this.b2body.getWorldCenter(), true);
            }
        }

        if (Math.abs(closestY - opponentY) > 0.1) {
            if (closestY > opponentY) {
                this.b2body.applyLinearImpulse(new Vector2(0, 0.05f), this.b2body.getWorldCenter(), true);
            } else {
                this.b2body.applyLinearImpulse(new Vector2(0, -0.05f), this.b2body.getWorldCenter(), true);
            }
        }

    }

    /**
     * Update path. This is called every PATH_UPDATE_INTERVAL milliseconds.
     */
    public void updatePath() {
        if (health < 0) {
            pathUpdateTimer.cancel(); // Stop the timer if opponent is dead
            return;
        }
        float shortestDistance = Float.MAX_VALUE;
        float closestX = 0;
        float closestY = 0;

        float opponentX = this.b2body.getPosition().x - getWidth() / 2;
        float opponentY = this.b2body.getPosition().y - getHeight() / 2;

        // Find the closest alive player
        for (PlayerData info : playScreen.game.playerDataMap.values()) {
            if (info.getHealth() <= 0) continue; // Don't try to go towards dead players

            float playerX = info.getX() - (64 / MyGDXGame.PPM) / 2;
            float playerY = info.getY() - (64 / MyGDXGame.PPM) / 2;

            float deltaX = playerX - opponentX;
            float deltaY = playerY - opponentY;

            float actualDistance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (actualDistance < shortestDistance) {
                shortestDistance = actualDistance;
                closestX = playerX;
                closestY = playerY;
            }
        }


        // If we haven't found any player then don't go anywhere
        if (shortestDistance == Float.MAX_VALUE) return;

        int playerX = (int) (closestX * MyGDXGame.PPM / TILE_WIDTH);
        int playerY = (int) (closestY * MyGDXGame.PPM / TILE_HEIGHT);

        int enemyX = (int) (opponentX * MyGDXGame.PPM / TILE_WIDTH);
        int enemyY = (int) (opponentY * MyGDXGame.PPM / TILE_HEIGHT);

        Node finalNode = new Node(enemyY, enemyX);
        Node initialNode = new Node(playerY, playerX);

        int[][] collisions = TileMapReader.getCollisions();

        // I put the finalNode and initialNode backwards, so we would get the path from initial to final, not final
        // to initial
        AStar aStar = new AStar(collisions[0].length, collisions.length, finalNode, initialNode);
        Collections.reverse(Arrays.asList(collisions));
        aStar.setBlocks(collisions);

        path = aStar.findPath();

        currentTargetIndex = 0;
    }

    /**
     * Defines the opponent's Box2D body and fixture.
     */
    public void defineOpponent(float startX, float startY, float radius) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startX / MyGDXGame.PPM, startY / MyGDXGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        fdef.shape = shape;
        fdef.filter.categoryBits = MyGDXGame.OPPONENT_CATEGORY;
        fdef.filter.maskBits = MyGDXGame.BULLET_CATEGORY | MyGDXGame.OTHER_PLAYER_CATEGORY | MyGDXGame.WORLD_CATEGORY | MyGDXGame.PLAYER_CATEGORY | MyGDXGame.OPPONENT_CATEGORY;
        b2body.createFixture(fdef);

        b2body.setUserData(this);

        // Set linear damping to simulate friction
        b2body.setLinearDamping(LINEAR_DAMPING);
    }

    /**
     * Retrieves the current running direction of the opponent based on linear velocity.
     *
     * @return The current running direction of the opponent (right side).
     */
    protected RunDirection getRunDirection() {
        if (health <= 0) {
            return RunDirection.DEAD;
        }
        float velocityX = Math.abs(b2body.getLinearVelocity().x); // We only care for positive x here so for example if running direction is upper left, then we want to return upper right
        float velocityY = b2body.getLinearVelocity().y;

        if (velocityX > VELOCITY_THRESHOLD) {
            if (velocityY > VELOCITY_THRESHOLD) return RunDirection.UPPER;
            else if (velocityY < -VELOCITY_THRESHOLD) return RunDirection.LOWER;
            else return RunDirection.RIGHT;
        } else {
            if (velocityY > 0) return RunDirection.UP;
            else if (velocityY < 0) return RunDirection.DOWN;
            else return RunDirection.DOWN; // We should never reach this point hopefully as it means that the opponet isn't running
        }
    }

    public Body getBody() {
        return b2body;
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
     * Reduces the opponent's health by the specified amount.
     *
     * @param damage The amount of damage to apply.
     */
    public void takeDamage(int damage) {
        health -= damage;
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

    /**
     * Return mob id. This is used to differentiate different mobs.
     * @return mob id
     */
    public abstract int getMobId();

    public long getSpawnTime() {
        return spawnTime;
    }

    public void setSpawnTime(long spawnTime) {
        this.spawnTime = spawnTime;
    }


}
