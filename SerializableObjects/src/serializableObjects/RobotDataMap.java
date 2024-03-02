package serializableObjects;

import java.util.HashMap;

public class RobotDataMap {
    private HashMap<String, RobotData> map;

    public RobotDataMap() {
        map = new HashMap<String, RobotData>();
    }
    public HashMap<String, RobotData> getMap() {
        return this.map;
    }
    public void put(String key, RobotData value) {
        this.map.put(key, value);
    }
    public void remove(String key) {
        this.map.remove(key);
    }
}
