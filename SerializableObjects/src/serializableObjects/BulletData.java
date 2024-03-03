package serializableObjects;

import java.io.Serializable;

public class BulletData implements Serializable {
    private float x;
    private float y;
    private float linVelX;
    private float linVelY;

    /**
     * Empty constructor
     */
    public BulletData() {
        this.linVelX = 0;
        this.linVelY = 0;
        this.x = 0;
        this.y = 0;
    }

    /**
     * Constructor for the object
     *
     * @param linVelX
     * @param linVelY
     * @param x
     * @param y
     */
    public BulletData(float linVelX, float linVelY, float x, float y) {
        this.linVelX = linVelX;
        this.linVelY = linVelY;
        this.x = x;
        this.y = y;
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
     * @return linVelX
     */
    public float getLinVelX() {
        return linVelX;
    }

    /**
     * Getter method
     *
     * @return linVelY
     */
    public float getLinVelY() {
        return linVelY;
    }
}
