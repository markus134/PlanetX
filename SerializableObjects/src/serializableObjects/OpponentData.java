package serializableObjects;

import java.io.Serializable;

public class OpponentData implements Serializable {
    private float x;
    private float y;
    private int health;
    private String uuid;
    private int mob;
    private final long mobSpawnTime;

    /**
     * Constructor for the object
     *
     * @param x coordinate
     * @param y coordinate
     * @param health hp points
     * @param uuid id
     * @param mob shows what mob it is
     * @param mobSpawnTime when the mob was spawned
     */
    public OpponentData(float x, float y, int health, String uuid, int mob, long mobSpawnTime) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.uuid = uuid;
        this.mob = mob;
        this.mobSpawnTime = mobSpawnTime;
    }

    /**
     * Empty constructor
     */
    public OpponentData() {
        this.x = 0;
        this.y = 0;
        this.health = 100;
        this.mobSpawnTime = 0;
    }

    /**
     * Getter method
     *
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * Getter method
     *
     * @return y
     */
    public float getY() {
        return y;
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
     * Getter method
     *
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Gets the id of the mob
     * @return mob id
     */
    public int getMob() {
        return mob;
    }

    /**
     * Gets the spawn time of the mob
     * @return spawnTime
     */
    public long getMobSpawnTime() {
        return mobSpawnTime;
    }
}