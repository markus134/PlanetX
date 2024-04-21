package serializableObjects;

import java.util.HashMap;
import java.util.Map;

public class AddSinglePlayerWorld {

    private final String worldUUID;
    private final String playerID;
    private final Map<String, String> singlePlayerWorlds;

    /**
     * Empty constructor
     */
    public AddSinglePlayerWorld() {
        this.worldUUID = "";
        this.playerID = "";
        this.singlePlayerWorlds = new HashMap<>();
    }

    /**
     * Constructor
     *
     * @param worldUUID world unique id
     * @param playerID  player unique id
     */
    public AddSinglePlayerWorld(String worldUUID, String playerID, Map<String, String> singlePlayerWorlds) {
        this.worldUUID = worldUUID;
        this.playerID = playerID;
        this.singlePlayerWorlds = singlePlayerWorlds;
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
     * Gets the id of the player
     *
     * @return string
     */
    public String getPlayerID() {
        return playerID;
    }

    /**
     * Gets the singleplayer worlds
     * @return map
     */
    public Map<String, String> getSinglePlayerWorlds() {
        return singlePlayerWorlds;
    }
}
