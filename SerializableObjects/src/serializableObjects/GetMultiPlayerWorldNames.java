package serializableObjects;

import java.util.Map;

public class GetMultiPlayerWorldNames {
    private Map<String, String> worldNamesAndIDs;
    private final String playerID;

    public GetMultiPlayerWorldNames() {
        this.playerID = "";
    }

    public GetMultiPlayerWorldNames(String playerID) {
        this.playerID = playerID;
    }

    public void setWorldNamesAndIDs(Map<String, String> worldNamesAndIDs) {
        this.worldNamesAndIDs = worldNamesAndIDs;
    }

    public Map<String, String> getWorldNamesAndIDs() {
        return worldNamesAndIDs;
    }

    public String getPlayerID() {
        return playerID;
    }
}
