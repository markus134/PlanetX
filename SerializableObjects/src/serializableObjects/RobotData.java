package serializableObjects;

import java.io.Serializable;

public class RobotData implements Serializable {
    private float x;
    private float y;
    private int health;
    private String uuid;

    /**
     * Constructor for the object
     *
     * @param x
     * @param y
     * @param health
     * @param uuid
     */
    public RobotData(float x, float y, int health, String uuid) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.uuid = uuid;
    }

    /**
     * Empty constructor
     */
    public RobotData() {
        this.x = 0;
        this.y = 0;
        this.health = 100;
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
}