package serializableObjects;

public class AskIfSessionIsFull {
    private String worldID;
    private boolean isFull;

    /**
     * Empty constructor
     */
    public AskIfSessionIsFull() {
        this.worldID = "";
        this.isFull = false;
    }

    /**
     * Constructor
     * @param worldID string
     */
    public AskIfSessionIsFull(String worldID) {
        this.worldID = worldID;
    }

    /**
     * Sets the value
     * @param full boolean
     */
    public void setFull(boolean full) {
        isFull = full;
    }

    /**
     * Checks if it is full
     * @return boolean
     */
    public boolean isFull() {
        return isFull;
    }

    /**
     * Getter method
     * @return the world id
     */
    public String getWorldID() {
        return worldID;
    }
}
