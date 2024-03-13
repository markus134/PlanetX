package serializableObjects;

import java.io.Serializable;

public class RobotData implements Serializable {
    private float x;
    private float y;
    private int health;
    private String uuid;
    private float linX;
    private float linY;


    /**
     * Constructor for the object
     *
     * @param x
     * @param y
     * @param health
     * @param uuid
     */
    public RobotData(float x, float y, int health, String uuid, float linX, float linY) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.uuid = uuid;
        this.linX = linX;
        this.linY = linY;
    }

    /**
     * Empty constructor
     */
    public RobotData() {
        this.x = 0;
        this.y = 0;
        this.health = 100;
        this.linX = 0;
        this.linY = 0;
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
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getLinX() {
        return linX;
    }

    public void setLinX(float linX) {
        this.linX = linX;
    }

    public float getLinY() {
        return linY;
    }

    public void setLinY(float linY) {
        this.linY = linY;
    }

}