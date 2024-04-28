package serializableObjects;

public class PlayerLeavesTheWorld {

    private final String worldID;

    /**
     * Empty constructor
     */
    public PlayerLeavesTheWorld() {
        this.worldID = "";
    }

    /**
     * Constructor
     * @param worldID world id
     */
    public PlayerLeavesTheWorld(String worldID) {
        this.worldID = worldID;
    }

    /**
     * Getter method
     * @return worldID
     */
    public String getWorldID() {
        return worldID;
    }
}
