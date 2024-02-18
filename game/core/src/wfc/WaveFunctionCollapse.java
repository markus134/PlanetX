package wfc;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Logic for the Wave Function Collapse algorithm.
 *
 * <p>
 *  This class specifies the possible tiling combinaitions and
 *  collapses the cell with the lowest entropy.
 *  Uses Simple Tiling Model.
 *  Tiles can be connected using adjacency data from {@link}.
 * </p>
 */
public class WaveFunctionCollapse {
    private Cell[][] superpositionCells;
    private boolean complete;

    private ArrayList<Cell> leastEntropy;

    public WaveFunctionCollapse(Cell[][] cells) {
        this.superpositionCells = cells;
        complete = false;
    }

    /**
     * Iterate through the algorithm once.
     */
    public void iterate() {
        leastEntropy = new ArrayList<>();

        propagateWave();
        complete = observe(getLeastEntropy());
    }

    /**
     * Iterate through every cell in the grid while updating possibilities according to neighboring cells.
     * This method also checks for cells with the least entropy, provided that the cell is not collapsed.
     */
    private void propagateWave() {
        int minimumEntropy = 999;  // Bad solution

        for (Cell[] superpositionCell : superpositionCells) {
            for (Cell currentCell : superpositionCell) {
                if (currentCell.isCollapsed()) continue;
                reduceEntropy(currentCell);

                // Check entropy.
                if (currentCell.entropy() == minimumEntropy) {
                    leastEntropy.add(currentCell);
                } else if (currentCell.entropy() < minimumEntropy) {
                    minimumEntropy = currentCell.entropy();
                    leastEntropy = new ArrayList<>();
                    leastEntropy.add(currentCell);
                }
            }
        }
    }

    /**
     * Helper method to reduce entropy of current cell.
     *
     * @param cell Cell to reduce the entropy on.
     */
    private void reduceEntropy(Cell cell) {
        int row = cell.getRow();
        int col = cell.getCol();

        ArrayList<int[][]> options = cell.getOptions();

        // Check up
        if (row > 0) {
            options = getValidOptions(options, superpositionCells[row - 1][col].getOptions(), 0);
        }

        // Check down
        if (row < superpositionCells.length - 1) {
            options = getValidOptions(options, superpositionCells[row + 1][col].getOptions(), 2);
        }

        // Check left
        if (col > 0) {
            options = getValidOptions(options, superpositionCells[row][col - 1].getOptions(), 3);
        }

        // Check right
        if (col < superpositionCells[cell.getRow()].length - 1) {
            options = getValidOptions(options, superpositionCells[row][col + 1].getOptions(), 1);

        }
        cell.setOptions(options);
    }

    /**
     * Get the intersection of the current array of objects and the array of objects that are being compared.
     *
     * @param current The current set of options.
     * @param compare The options to compare to.
     * @param position The position of the current side (0-3).
     * @return ArrayList containing options that are in current and compare.
     */
    private ArrayList<int[][]> getValidOptions(ArrayList<int[][]> current, ArrayList<int[][]> compare, int position) {
        ArrayList<int[][]> returnOptions = new ArrayList<>();

        current.forEach(option -> {
            for (int[][] compareOptions : compare) {
                if (option[position] == compareOptions[(position + 2) % 4]) {
                    returnOptions.add(option);
                }
            }
        });

        return returnOptions;
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
     * @return Boolean indicating whether the current cell is collapsable.
     */
    private boolean observe(Cell cell) {
        if (cell.isCollapsed()) return true;

        cell.collapseCell();
        return false;
    }

    /**
     * Get collapsed cell grid.
     *
     * @return 2D array containing collapsed cells.
     */
    public Cell[][] getCells() {
        return superpositionCells;
    }

    public boolean isComplete() {
        return complete;
    }
}
