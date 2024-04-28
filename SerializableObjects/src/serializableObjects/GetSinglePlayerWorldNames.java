package serializableObjects;

import java.util.Map;

public class GetSinglePlayerWorldNames {
    private Map<String, String> worldNamesAndIDs;
    private final String playerID;

    /**
     * Empty constructor
     */
    public GetSinglePlayerWorldNames() {
        this.playerID = "";
    }

    /**
     * Constructor
     *
     * @param playerID
     */
    public GetSinglePlayerWorldNames(String playerID) {
        this.playerID = playerID;
    }

    /**
     * Sets the map
     *
     * @param worldNamesAndIDs the map to set
     */
    public void setWorldNamesAndIDs(Map<String, String> worldNamesAndIDs) {
        this.worldNamesAndIDs = worldNamesAndIDs;
    }

    /**
     * Gets the map
     *
     * @return map with worldName - key and worldID - value
     */
    public Map<String, String> getWorldNamesAndIDs() {
        return worldNamesAndIDs;
    }

    /**
     * Getter method
     *
     * @return playerID
     */
    public String getPlayerID() {
        return playerID;
    }
}
