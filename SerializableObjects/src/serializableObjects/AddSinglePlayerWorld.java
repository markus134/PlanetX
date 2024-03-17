package serializableObjects;

public class AddSinglePlayerWorld {

    private String worldUUID;

    /**
     * Empty constructor
     */
    public AddSinglePlayerWorld() {
        this.worldUUID = "";
    }

    /**
     * Constructor
     *
     * @param uuid world unique id
     */
    public AddSinglePlayerWorld(String uuid) {
        this.worldUUID = uuid;
    }

    /**
     * Gets the uuid of the world
     *
     * @return uuid
     */
    public String getWorldUUID() {
        return worldUUID;
    }
}
