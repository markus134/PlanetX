package serializableObjects;

import java.io.Serializable;

public class RevivePlayer implements Serializable {
    private final String uuid;

    /**
     * Empty constructor
     */
    public RevivePlayer() {
        this.uuid = "";
    }

    /**
     * Constructor
     * @param uuid of the player
     */
    public RevivePlayer(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Getter method
     * @return id of the player
     */
    public String getUuid() {
        return uuid;
    }
}
