package Tools;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;
public class UUIDFileManager {
    private static final String STATUS_IN_USE = "in use";
    private static final String STATUS_AVAILABLE = "available";
    private final Path uuidPath;
    private JSONObject uuidJson;

    public UUIDFileManager() throws IOException {
        String homeDir = System.getProperty("user.home");
        this.uuidPath = Paths.get(homeDir, ".PlanetX", ".config", ".uniqueID", "uuids.json");

        if (Files.exists(uuidPath)) {
            String content = Files.readString(uuidPath);
            this.uuidJson = new JSONObject(content);
        } else {
            this.uuidJson = new JSONObject();
            Files.createDirectories(uuidPath.getParent());
            Files.writeString(uuidPath, uuidJson.toString());
        }
    }

    public String assignUUID() throws IOException {
        String content = Files.readString(uuidPath);
        this.uuidJson = new JSONObject(content);

        // Find an available UUID
        for (String key : uuidJson.keySet()) {
            JSONObject entry = uuidJson.getJSONObject(key);
            if (entry.getString("status").equals(STATUS_AVAILABLE)) {
                entry.put("status", STATUS_IN_USE);
                Files.writeString(uuidPath, uuidJson.toString());
                return entry.getString("uuid");
            }
        }

        // If no available UUIDs, generate a new one
        String newUUID = UUID.randomUUID().toString();
        JSONObject newEntry = new JSONObject();
        newEntry.put("uuid", newUUID);
        newEntry.put("status", STATUS_IN_USE);

        uuidJson.put(newUUID, newEntry);
        Files.writeString(uuidPath, uuidJson.toString());

        return newUUID;
    }

    public void releaseUUID(String uuid) throws IOException {
        String content = Files.readString(uuidPath);
        this.uuidJson = new JSONObject(content);

        // Mark the UUID as available when the game ends
        for (String key : uuidJson.keySet()) {
            JSONObject entry = uuidJson.getJSONObject(key);
            if (entry.getString("uuid").equals(uuid)) {
                entry.put("status", STATUS_AVAILABLE);
                Files.writeString(uuidPath, uuidJson.toString());
                break;
            }
        }
    }
}
