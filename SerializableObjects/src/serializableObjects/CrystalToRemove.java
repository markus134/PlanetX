package serializableObjects;

import java.io.Serializable;

public class CrystalToRemove implements Serializable {
    private final int id;
    public CrystalToRemove() {
        this.id = 0;
    }

    public CrystalToRemove(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
