package serializableObjects;

public class PlayerLeavesTheWorld {

    private final String worldID;
    private final int currentWave;
    private final int currentTime;

    /**
     * Empty constructor
     */
    public PlayerLeavesTheWorld() {
        this.worldID = "";
        this.currentWave = 1;
        this.currentTime = 0;
    }

    /**
     * Constructor
     * @param worldID world id
     */
    public PlayerLeavesTheWorld(String worldID, int currentWave, int currentTime) {
        this.worldID = worldID;
        this.currentWave = currentWave;
        this.currentTime = currentTime;
    }

    /**
     * Getter method
     * @return worldID
     */
    public String getWorldID() {
        return worldID;
    }

    /**
     * Getter method
     * @return current wave
     */
    public int getCurrentWave() {
        return currentWave;
    }

    /**
     * Getter method
     * @return current time
     */
    public int getCurrentTime() {
        return currentTime;
    }
}
