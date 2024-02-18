package wfc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Generate a map.
 */
public class TileMap {
    public static final int GRID_WIDTH = 16;
    public static final int GRID_HEIGHT = 16;
    public static final int GRID_CELL_SIZE = 64;

    private Cell[][] cells;
    private final TileSetLoader rules;

    /**
     * Create TileMap object.
     */
    public TileMap() {
        cells = new Cell[GRID_HEIGHT][GRID_WIDTH];
        rules = new TileSetLoader();

        initializeMap();
        runWaveFunctionCollapse();
    }

    /**
     * Initialize the map with rules found in {@link TileSetLoader}.
     */
    private void initializeMap() {
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                cells[row][col] = new Cell(rules.getOptions(), row, col);
            }
        }
    }

    /**
     * Run the map generator.
     * <p>
     *  This method has side effects on the local 'cells' variable.
     * </p>
     */
    private void runWaveFunctionCollapse() {
        WaveFunctionCollapse wfc = new WaveFunctionCollapse(cells);
        while (!wfc.isComplete()) {
            wfc.iterate();
        }

        cells = wfc.getCells();
    }
}
