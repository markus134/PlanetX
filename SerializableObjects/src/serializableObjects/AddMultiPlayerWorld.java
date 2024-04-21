package serializableObjects;

import java.util.HashMap;
import java.util.Map;

public class AddMultiPlayerWorld {
    private final String worldUUID;
    private final int numberOfPlayers;
    private final String playerID;
    private final Map<String, String> multiplayerWorlds;

    /**
     * Empty constructor
     */
    public AddMultiPlayerWorld() {
        this.worldUUID = "";
        this.numberOfPlayers = 0;
        this.playerID = "";
        this.multiplayerWorlds = new HashMap<>();
    }

    /**
     * Creates an instance of the class
     *
     * @param worldUUID of the world
     * @param playerID  of the player
     */
    public AddMultiPlayerWorld(String worldUUID, String playerID, Map<String, String> multiplayerWorlds) {
        String[] parts = worldUUID.split(":");
        this.worldUUID = worldUUID;
        this.numberOfPlayers = Integer.parseInt(parts[1]);
        this.playerID = playerID;
        this.multiplayerWorlds = multiplayerWorlds;
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

    /**
     * Gets the id of the player
     *
     * @return string
     */
    public String getPlayerID() {
        return playerID;
    }

    /**
     * Gets the worlds
     *
     * @return worlds
     */
    public Map<String, String> getMultiplayerWorlds() {
        return multiplayerWorlds;
    }
}
