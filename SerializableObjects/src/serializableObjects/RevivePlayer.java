package serializableObjects;

import java.io.Serializable;

public class RevivePlayer implements Serializable {
    private final String uuid;
    public RevivePlayer() {
        this.uuid = "";
    }

    public RevivePlayer(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
