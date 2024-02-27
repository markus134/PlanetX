package wfc;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Set;

/**
 * Create Tile objects from (file) input.
 */
class Tile {
	private HashMap<Integer, Set<Integer>> allowedNeighbors = new HashMap<>();
	private final Texture imageTexture;
	private final double weight;  // Add weights later.
	
	/**
	 * Create Tile object.
	 * 
	 * @param rule Rules as a 2D array clockwise from top.
	 * @param imagePath Path of the image in the assets file.
	 */
	public Tile(HashMap<Integer, Set<Integer>> allowedNeighbors, String imagePath, double weight) {
		this.allowedNeighbors = allowedNeighbors;
		imageTexture = new Texture(imagePath);
		this.weight = weight;
	}
	
	/**
	 * Get the valid neighbors on a side.
	 * 
	 * @param position Side to get neighbors from.
	 * @return Set containing valid neighbor codes.
	 */
	public Set<Integer> getAllowedNeighbors(int position) {
		return allowedNeighbors.get(position);
	}
	
	/**
	 * Get the object image path.
	 * 
	 * @return String holding the path of the image in assets.
	 */
	public Texture getTexture() {
		return imageTexture;
	}
	
	/**
	 * Get the weight of the tile.
	 * 
	 * @return Integer representing the frequency which tile should appear.
	 */
	public double getWeight() {
		return weight;
	}
}
