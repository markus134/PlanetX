package serializableObjects;

public class PlayerLeavesTheWorld {

    private String worldID;

    public PlayerLeavesTheWorld() {
        this.worldID = "";
    }

    public PlayerLeavesTheWorld(String worldID) {
        this.worldID = worldID;
    }

    public String getWorldID() {
        return worldID;
    }
}
