package serializableObjects;

import java.util.HashMap;

public class RobotDataMap {
    private HashMap<String, RobotData> map;

    /**
     * Constructor
     */
    public RobotDataMap() {
        map = new HashMap<String, RobotData>();
    }

    /**
     * Getter method
     *
     * @return map
     */
    public HashMap<String, RobotData> getMap() {
        return this.map;
    }

    /**
     * Puts a key-value pair into the map.
     *
     * @param key
     * @param value
     */
    public void put(String key, RobotData value) {
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
}
