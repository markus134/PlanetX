package serializableObjects;

import java.io.Serializable;

public class PlayerData implements Serializable {

    private float x;
    private float y;
    private int frame;
    private boolean runningRight;
    private int health;
    private String uuid;
    private String worldUUID;

    /**
     * Empty constructor
     */
    public PlayerData() {
        this.x = 0;
        this.y = 0;
        this.frame = 0;
        this.runningRight = true;
        this.health = 100;
        this.uuid = "";
        this.worldUUID = "";

    }

    /**
     * Constructor for the object
     *
     * @param x
     * @param y
     * @param frame
     * @param runningRight
     * @param health
     * @param uuid
     */
    public PlayerData(float x, float y, int frame, boolean runningRight, int health, String uuid, String worldUUID) {
        this.x = x;
        this.y = y;
        this.frame = frame;
        this.runningRight = runningRight;
        this.health = health;
        this.uuid = uuid;
        this.worldUUID = worldUUID;
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
     * @return frame
     */
    public int getFrame() {
        return frame;
    }

    /**
     * Getter method
     *
     * @return runningRight
     */
    public boolean isRunningRight() {
        return runningRight;
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
     * Getter method
     *
     * @return worldUUID
     */
    public String getWorldUUID() {
        return worldUUID;
    }
}
