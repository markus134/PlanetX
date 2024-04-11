package wfc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Initializes a cell.
 * <p>
 *      Cells are discrete elements that form a tilemap.
 *      <br>
 *      Each cell has n number of opitons that get collapsed as {@link WaveFunctionCollapse}
 *      collapses cells on the grid. When a cell is collapsed, it is
 *      set to be drawn to the screen in {@link TileMap}.
 * </p>
 *
 */
class Cell implements Comparable<Cell> {
	private long seed;
    private ArrayList<Integer> options;
    private boolean collapsed = false;
    private Random random;

    /**
     * Create Cell object with all possible rules.
     *
     * @param options Array of all options.
     */
    public Cell(ArrayList<Integer> options, long seed) {
    	this.random = new Random(seed);
        this.options = options;
    }

    /**
     * Get the total amount of entropy in current cell.
     * Entropy is defined as the number of options available.
     *
     * @return Integer specifying entropy.
     */
    public int entropy() {
        return options.size();
    }

    /**
     * Collapses the current cell, which has least entropy.
     */
    public void collapseCell() {
    	/*
    	if (options.size() > 0) {
    		double totalWeight = 0;
    		
    		for (int id : options) {
    			totalWeight += TileSetLoader.tileMap.get(id).getWeight();
    		}
    		
    		Random random = new Random(seed);
    		double randomWeight = random.nextFloat() * totalWeight;
    		double cumulativeWeight = 0;
    		
    		for (int id : options) {
    			cumulativeWeight += TileSetLoader.tileMap.get(id).getWeight();
    			if (cumulativeWeight >= randomWeight) {
    				options = new ArrayList<>(Collections.singletonList(id));
    				break;
    			}
    		}    		
    	}
    	else {
    	}
    	*/
		options = new ArrayList<>(Collections.singletonList(random.nextInt(options.size())));
        collapsed = true;
    }

    /**
     * Setter for new options.
     * <p>
     *  New options from the loss of cell entropy.
     * </p>
     *
     * @param options 2D Integer array of tile rules.
     */
    public void setOptions(ArrayList<Integer> options) {
        this.options = options;
    }

    /**
     * Get {@link ArrayList} of possible options.
     *
     * @return ArrayList containing options.
     */
    public ArrayList<Integer> getOptions() {
        return options;
    }

    /**
     * Get the state of the Cell.
     *
     * @return Current state of the cell as true/false.
     */
    public boolean isCollapsed() {
        return collapsed;
    }

    /**
     * Compare two Cell objects according to their option amounts.
     *
     * @param other the object to be compared.
     * @return The difference between two Cell entropies.
     * @see #entropy()
     */
    @Override
    public int compareTo(Cell other) {
        return Integer.compare(this.entropy(), other.entropy());
    }
    
    @Override
    public String toString() {
    	return "Cell{" +
    			"options=" + options + ", " +
    			"collapsed=" + collapsed + "}";
    }
}
