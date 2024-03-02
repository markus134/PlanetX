package serializableObjects;

import java.io.Serializable;

public class PlayerData implements Serializable {

    private float x;
    private float y;
    private int frame;
    private boolean runningRight;
    private int health;
    private String uuid;

    public PlayerData() {
        this.x = 0;
        this.y = 0;
        this.frame = 0;
        this.runningRight = true;
        this.health = 100;
        this.uuid = "";

    }

    public PlayerData(float x, float y, int frame, boolean runningRight, int health, String uuid) {
        this.x = x;
        this.y = y;
        this.frame = frame;
        this.runningRight = runningRight;
        this.health = health;
        this.uuid = uuid;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getFrame() {
        return frame;
    }

    public boolean isRunningRight() {
        return runningRight;
    }
    public int getHealth() {
        return health;
    }
    public String getUuid() {
        return uuid;
    }


}
