package ObjectsToSend;

import java.io.Serializable;

public class RobotData implements Serializable {
    private float x;
    private float y;
    private int frame;
    private boolean runningRight;

    public RobotData(float x, float y, int frame, boolean runningRight){
        this.x = x;
        this.y = y;
        this.frame = frame;
        this.runningRight = runningRight;
    }

    public RobotData(){
        this.x = 0;
        this.y = 0;
        this.frame = 0;
        this.runningRight = true;
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