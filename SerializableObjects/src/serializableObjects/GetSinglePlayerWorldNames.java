package serializableObjects;

import java.util.List;
import java.util.Map;

public class GetSinglePlayerWorldNames {
    private Map<String, String> worldNamesAndIDs;
    private Map<String, Integer[]> worldNameToWaveData;
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

    public void setWorldNameToWaveData(Map<String, Integer[]> worldNameToWaveData) {
        this.worldNameToWaveData = worldNameToWaveData;
    }

    /**
     * Gets the map
     *
     * @return map with worldName - key and worldID - value
     */
    public Map<String, String> getWorldNamesAndIDs() {
        return worldNamesAndIDs;
    }

    public Map<String, Integer[]> getWorldNameToWaveData() {
        return worldNameToWaveData;
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
