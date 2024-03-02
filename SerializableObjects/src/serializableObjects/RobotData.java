package serializableObjects;

import java.io.Serializable;

public class RobotData implements Serializable {
    private float x;
    private float y;
    private int health;
    private String uuid;

    public RobotData(float x, float y, int health, String uuid){
        this.x = x;
        this.y = y;
        this.health = health;
        this.uuid = uuid;
    }

    public RobotData(){
        this.x = 0;
        this.y = 0;
        this.health = 100;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }
    public String getUuid() {
        return uuid;
    }
}