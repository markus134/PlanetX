package serializableObjects;

public class RemoveMultiPlayerWorld {
    private String playerID;
    private String worldID;
    private String worldName;

    /**
     * Empty constructor
     */
    public RemoveMultiPlayerWorld() {
        this.playerID = "";
        this.worldID = "";
        this.worldName = "";
    }

    /**
     * Normal constructor
     *
     * @param playerID
     * @param worldID
     * @param worldName
     */
    public RemoveMultiPlayerWorld(String playerID, String worldID, String worldName) {
        this.playerID = playerID;
        this.worldID = worldID;
        this.worldName = worldName;
    }

    /**
     * Getter method
     *
     * @return player id
     */
    public String getPlayerID() {
        return playerID;
    }

    /**
     * Getter method
     *
     * @return world id
     */
    public String getWorldID() {
        return worldID;
    }

    /**
     * Getter method
     *
     * @return world name
     */
    public String getWorldName() {
        return worldName;
    }
}
