package serializableObjects;

public class AddMultiPlayerWorld {
    private final String worldUUID;
    private final int numberOfPlayers;

    /**
     * Empty constructor
     */
    public AddMultiPlayerWorld() {
        this.worldUUID = "";
        this.numberOfPlayers = 0;
    }

    /**
     * Creates an instance of the class
     *
     * @param uuid of the world
     */
    public AddMultiPlayerWorld(String uuid) {
        String[] parts = uuid.split(":");
        this.worldUUID = uuid;
        this.numberOfPlayers = Integer.parseInt(parts[1]);
    }

    /**
     * Gets the uuid of the world
     *
     * @return uuid
     */
    public String getWorldUUID() {
        return worldUUID;
    }

    /**
     * Gets the number of players in th world
     *
     * @return int
     */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
