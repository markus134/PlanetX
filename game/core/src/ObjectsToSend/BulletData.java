package ObjectsToSend;

import java.io.Serializable;

public class BulletData implements Serializable {
    private float x;
    private float y;
    private float linVelX;
    private float linVelY;

    public BulletData() {
        this.linVelX = 0;
        this.linVelY = 0;
        this.x = 0;
        this.y = 0;
    }
    public BulletData(float linVelX, float linVelY, float x, float y) {
        this.linVelX = linVelX;
        this.linVelY = linVelY;
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLinVelX() {
        return linVelX;
    }

    public float getLinVelY() {
        return linVelY;
    }
}
