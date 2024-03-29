package wfc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Load rules for each tile from file.
 * <p>
 * Loads Tile rules, neighbors, images and weights into tile objects.
 * </p>
 */
class TileSetLoader {
	public static LinkedHashMap<Integer, Tile> tileMap = new LinkedHashMap<>();

    /**
     * Load Tile data into tileMap.
     * 
     * @param filePath Path of the file in /assets/levels/.
     */
    public TileSetLoader(String filePath) {
    	// Read Json file.
    	FileHandle file = Gdx.files.internal(filePath);
    	
    	JsonReader json = new JsonReader();
    	JsonValue jsonFile = json.parse(file);
    	
    	// Add values.
    	int chunkRadius = jsonFile.getInt("chunkRadius");
    	int chunkSize = jsonFile.getInt("chunkSize");
    	int cellSize = jsonFile.getInt("cellSize");
    	
    	// Add tile.
    	for (JsonValue tile : jsonFile.get("tiles")) {
    		int id = tile.getInt("id");
            HashMap<Integer, HashSet<Integer>> allowedNeighbors = new HashMap<>();
            
    		for (JsonValue neighbors : tile.get("allowedNeighbors")) {
    			int key = Integer.parseInt(neighbors.name);
    			HashSet<Integer> values = new HashSet<>();
    			
    			for (JsonValue value : neighbors) {
    				values.add(value.asInt());
    			}
    			
    			allowedNeighbors.put(key, values);
    		}
        	
        	String imagePath = tile.getString("imagePath");
        	
        	double weight = tile.getDouble("weight");
        	
        	JsonValue collisionGrid = tile.get("collisionGrid");
        	
        	tileMap.put(id, new Tile(id, allowedNeighbors, imagePath, weight));
    	}
    }	
}
