package serializableObjects;

public class RemoveSinglePlayerWorld {
    private String playerID;
    private String worldID;
    private String worldName;

    public RemoveSinglePlayerWorld() {
        this.playerID = "";
        this.worldID = "";
        this.worldName = "";
    }

    public RemoveSinglePlayerWorld(String playerID, String worldID, String worldName) {
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
