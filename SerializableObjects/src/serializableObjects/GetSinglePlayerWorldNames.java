package serializableObjects;

import java.util.Map;

public class GetSinglePlayerWorldNames {
    private Map<String, String> worldNamesAndIDs;
    private final String playerID;

    public GetSinglePlayerWorldNames() {
        this.playerID = "";
    }

    public GetSinglePlayerWorldNames(String playerID) {
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
