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
     * @param x
     * @param y
     * @param health
     * @param uuid
     * @param mob
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

    public int getMob() {
        return mob;
    }

    public long getMobSpawnTime() {
        return mobSpawnTime;
    }
}