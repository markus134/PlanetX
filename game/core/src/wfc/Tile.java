package wfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.Texture;

/**
 * Create Tile objects from (file) input.
 */
class Tile {
	private int id;
	private HashMap<Integer, HashSet<Integer>> allowedNeighbors = new HashMap<>();
	private Texture imageTexture;
	private double weight;
	private int collision;
	
	public Tile() {
		
	}
	
	/**
	 * Create Tile object.
	 * 
	 * @param rule Rules as a 2D array clockwise from top.
	 * @param imagePath Path of the image in the assets file.
	 */
	public Tile(int id, HashMap<Integer, HashSet<Integer>> allowedNeighbors, String imagePath, double weight, int collision) {
		this.id = id;
		this.allowedNeighbors = allowedNeighbors;
		imageTexture = new Texture(imagePath);
		this.weight = weight;
		this.collision = collision;
	}
	
	public Tile(int id, HashMap<Integer, HashSet<Integer>> allowedNeighbors, String imagePath, double weight) {
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
	 * Get the id of the tile.
	 * 
	 * @return Integer containing the id.
	 */
	public int getId() {
		return id;
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
	
	public int getCollision() {
		return collision;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tile{id=").append(id);
		sb.append(", allowedNeighbors=").append(allowedNeighbors);
		sb.append(", texture='").append(imageTexture).append("'");
		sb.append(", weight=").append(weight);
		sb.append(", collision=").append(collision);
		sb.append("}");
		
		return sb.toString();
	}
}
