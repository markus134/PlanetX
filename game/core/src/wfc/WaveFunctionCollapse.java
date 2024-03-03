package wfc;

import java.security.SecureRandom;
import java.util.ArrayList;


/**
 * Logic for the Wave Function Collapse algorithm.
 * <p>
 * This class specifies the possible tiling combinations and
 * collapses the cell with the lowest entropy.
 * Uses Simple Tiling Model.
 * Tiles can be connected using adjacency data from {@link}.
 */
class WaveFunctionCollapse {
    private final int gridSize;

    private Cell[][] cells;
    private boolean complete;
    private ArrayList<Cell> leastEntropy = new ArrayList<>();

    public WaveFunctionCollapse(Cell[][] cells, int gridSize) {
        System.out.println("Wave Function Collapse Algorithm Has Run!");
        this.cells = cells;
        this.gridSize = gridSize;
        complete = false;
    }

    /**
     * Iterate through the algorithm once.
     */
    public void iterate() {
        leastEntropy.clear();

        propagateWave();

        if (leastEntropy.size() > 0) {
            Cell leastEntropy = getLeastEntropy();
            observe(leastEntropy);
        } else {
            complete = true;
        }
    }

    /**
     * Iterate through every cell in the grid while updating possibilities according to neighboring cells.
     * This method also checks for cells with the least entropy, provided that the cell is not collapsed.
     */
    private void propagateWave() {
        int minimumEntropy = 999;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Cell entry = cells[row][col];
                if (entry.isCollapsed()) continue;
                reduceEntropy(entry, row, col);

                if (entry.entropy() == 0) {
                    entry.setOptions(new ArrayList<>(TileSetLoader.tileMap.keySet()));
                    continue;
                }
                // Find cells with least entropy.
                if (entry.entropy() < minimumEntropy) {
                    minimumEntropy = entry.entropy();
                    leastEntropy.clear();
                    leastEntropy.add(entry);
                } else if (entry.entropy() == minimumEntropy) {
                    leastEntropy.add(entry);
                }
            }
        }
    }

    /**
     * Helper method to reduce entropy of current cell.
     * <p>
     * 0 - up
     * 1 - right
     * 2 - down
     * 3 - left
     *
     * @param cell Cell to reduce the entropy on.
     */
    private void reduceEntropy(Cell cell, int row, int col) {
        ArrayList<Integer> options = cell.getOptions();

        // Compare against neighbor above.
        if (row > 0) {
            Cell upNeighbor = cells[row - 1][col];  // It smells like updog in here. Updog? What is updog?
            options = validOptions(options, upNeighbor.getOptions(), 2);
        }

        // Compare against neighbor below.
        if (row < gridSize - 1) {
            Cell downNeighbor = cells[row + 1][col];
            options = validOptions(options, downNeighbor.getOptions(), 0);
        }

        // Compare against neighbor to the left.
        if (col > 0) {
            Cell leftNeighbor = cells[row][col - 1];
            options = validOptions(options, leftNeighbor.getOptions(), 1);
        }

        // Compare against neighbor to the right.
        if (col < gridSize - 1) {
            Cell rightNeighbor = cells[row][col + 1];
            options = validOptions(options, rightNeighbor.getOptions(), 3);
        }

        cell.setOptions(options);
    }

    /**
     * See if the options are valid against a neighboring cell.
     * This method gets the intersection between the current cell
     * and all cells in the neighboring cell that are valid.
     *
     * @param current   The Cell under scrutiny.
     * @param neighbor  The Cell to compare against.
     * @param direction Which side of the neighbor to compare against.
     * @return Array of Tile objects that are still valid.
     */
    private ArrayList<Integer> validOptions(ArrayList<Integer> current, ArrayList<Integer> neighbor, int direction) {
        ArrayList<Integer> returnArray = new ArrayList<>();

        for (int i : current) {
            for (int tileId : neighbor) {
                if (TileSetLoader.tileMap.get(tileId).getAllowedNeighbors(direction).contains(i)) {
                    returnArray.add(i);
                    break;
                }
            }
        }

        return returnArray;
    }

    /**
     * Randomly get a cell Object from possible picks with the least entropy.
     *
     * @return Randomly picked Cell object.
     */
    private Cell getLeastEntropy() {
        return leastEntropy.get(new SecureRandom().nextInt(leastEntropy.size()));
    }

    /**
     * Observe the selected cell and try to collapse it into a single tile.
     *
     * @param cell Cell with the least entropy.
     */
    private void observe(Cell cell) {
        cell.collapseCell();
    }

    /**
     * Get cell grid.
     *
     * @return 2D Cell array containing cells.
     */
    public Cell[][] getCells() {
        return cells;
    }

    public boolean isComplete() {
        return complete;
    }
}
