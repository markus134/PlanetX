package wfc;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Initializes a cell.
 *
 * <p>
 *  Cells are discrete elements that form a tilemap.
 *  Each cell has n number of opitons that get collapsed as {@link WaveFunctionCollapse}
 *  collapses cells on the grid. When a cell is collapsed, it is
 *  set to be drawn to the screen in {@link TileMap}.
 * </p>
 *
 */
public class Cell implements Comparable<Cell> {
    private ArrayList<int[][]> options;
    private final int row;
    private final int col;
    private boolean collapsed = false;
    private final SecureRandom random = new SecureRandom();

    /**
     * Create Cell object with all possible rules.
     *
     * @param options Array of all options.
     */
    public Cell(ArrayList<int[][]> options, int row, int col) {
        this.options = options;
        this.row = row;
        this.col = col;
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
     * <p>
     *  If the current cell has multiple possible options,
     *  pick one option at random.
     *  Sets the collapsed state to true.
     * </p>
     */
    public void collapseCell() {
        options = new ArrayList<>(Collections.singletonList(options.get(random.nextInt(options.size()))));
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
    public void setOptions(ArrayList<int[][]> options) {
        this.options = options;
    }

    /**
     * Get {@link ArrayList} of possible options.
     *
     * @return ArrayList containing options.
     */
    public ArrayList<int[][]> getOptions() {
        return options;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
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
}
