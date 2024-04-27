package serializableObjects;

public class RemoveMultiPlayerWorld {
    private String playerID;
    private String worldID;
    private String worldName;

    public RemoveMultiPlayerWorld() {
        this.playerID = "";
        this.worldID = "";
        this.worldName = "";
    }

    public RemoveMultiPlayerWorld(String playerID, String worldID, String worldName) {
        this.playerID = playerID;
        this.worldID = worldID;
        this.worldName = worldName;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getWorldID() {
        return worldID;
    }

    public String getWorldName() {
        return worldName;
    }
}
