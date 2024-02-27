package serializableObjects;

import java.io.Serializable;

public class RobotData implements Serializable {
    private float x;
    private float y;

    public RobotData(float x, float y){
        this.x = x;
        this.y = y;
    }

    public RobotData(){
        this.x = 0;
        this.y = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}