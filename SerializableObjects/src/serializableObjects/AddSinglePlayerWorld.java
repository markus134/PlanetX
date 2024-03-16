package serializableObjects;

public class AddSinglePlayerWorld {

    private String worldUUID;

    public AddSinglePlayerWorld() {
        this.worldUUID = "";
    }

    public AddSinglePlayerWorld(String uuid) {
        this.worldUUID = uuid;
    }

    public String getWorldUUID() {
        return worldUUID;
    }
}
