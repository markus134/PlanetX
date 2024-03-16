package serializableObjects;

public class AddMultiPlayerWorld {
    private String worldUUID;
    private int numberOfPlayers;

    public AddMultiPlayerWorld() {
        this.worldUUID = "";
        this.numberOfPlayers = 0;
    }

    public AddMultiPlayerWorld(String uuid) {
        String[] parts = uuid.split(":");
        this.worldUUID = uuid;
        this.numberOfPlayers = Integer.parseInt(parts[1]);
    }

    public String getWorldUUID() {
        return worldUUID;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
