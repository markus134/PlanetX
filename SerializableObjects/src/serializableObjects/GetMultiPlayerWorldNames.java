package serializableObjects;

import java.util.Map;

public class GetMultiPlayerWorldNames {
    private Map<String, String> worldNamesAndIDs;
    private Map<String, Integer[]> worldNameToWaveData;
    private final String playerId;

    /**
     * Empty constructor
     */
    public GetMultiPlayerWorldNames() {
        this.playerId = "";
    }

    /**
     * Constructor
     *
     * @param playerID
     */
    public GetMultiPlayerWorldNames(String playerID) {
        this.playerId = playerID;
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


    public String getPlayerId() {
        return playerId;
    }

    public void setWorldNameToWaveData(Map<String, Integer[]> worldNameToWaveData) {
        this.worldNameToWaveData = worldNameToWaveData;
    }

    public Map<String, Integer[]> getWorldNameToWaveData() {
        return worldNameToWaveData;
    }
}
