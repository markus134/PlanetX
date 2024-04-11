package serializableObjects;

import java.util.HashMap;

public class OpponentDataMap {
    private HashMap<String, OpponentData> map;
    private String worldUUID;

    /**
     * Constructor
     */
    public OpponentDataMap(String worldUUID) {
        map = new HashMap<>();
        this.worldUUID = worldUUID;
    }

    public OpponentDataMap() {
        map = new HashMap<>();
        worldUUID = "";
    }

    /**
     * Getter method
     *
     * @return map
     */
    public HashMap<String, OpponentData> getMap() {
        return this.map;
    }

    /**
     * Puts a key-value pair into the map.
     *
     * @param key
     * @param value
     */
    public void put(String key, OpponentData value) {
        this.map.put(key, value);
    }

    /**
     * Removes a key-value pair
     *
     * @param key
     */
    public void remove(String key) {
        this.map.remove(key);
    }

    /**
     * Getter method
     *
     * @return worldUUID
     */
    public String getWorldUUID() {
        return worldUUID;
    }
}
