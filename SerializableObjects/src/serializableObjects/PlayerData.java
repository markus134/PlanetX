package serializableObjects;

import java.io.Serializable;

public class PlayerData implements Serializable {

    private float x;
    private float y;
    private int frame;
    private boolean runningRight;

    public PlayerData() {
        this.x = 0;
        this.y = 0;
        this.frame = 0;
        this.runningRight = true;
    }

    public PlayerData(float x, float y, int frame, boolean runningRight) {
        this.x = x;
        this.y = y;
        this.frame = frame;
        this.runningRight = runningRight;
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
}
