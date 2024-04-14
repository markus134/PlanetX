package serializableObjects;

import java.io.Serializable;

public class CrystalToRemove implements Serializable {
    private final int id;

    /**
     * Empty constructor
     */
    public CrystalToRemove() {
        this.id = 0;
    }

    /**
     * Constructor
     * @param id crystal id
     */
    public CrystalToRemove(int id) {
        this.id = id;
    }

    /**
     * Getter method
     * @return crystal id
     */
    public int getId() {
        return id;
    }
}
