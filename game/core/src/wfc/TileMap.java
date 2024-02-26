package wfc;

import java.util.ArrayList;

/**
 * Generate map data.
 */
class TileMap {
    private final int gridWidth;
    private final int gridHeight;
    private final int cellSize;
    
    private Cell[][] cells;
    private TileSetLoader tiles;

    /**
     * Create cells, get tiles and run the algorithm.
     */
    public TileMap(int gridWidth, int gridHeight, int cellSize) {
    	this.gridWidth = gridWidth;
    	this.gridHeight = gridHeight;
    	this.cellSize = cellSize;
    	
        cells = new Cell[gridWidth][gridHeight];
        tiles = new TileSetLoader();

        initializeMap();
        runWaveFunctionCollapse();
        for (int row = 0; row < gridWidth; row++) {
            for (int col = 0; col < gridHeight; col++) {
            	//System.out.println(cells[row][col].getOptions());
            }
        }   
    }

    /**
     * Initialize the map with rules found in {@link TileSetLoader}.
     */
    private void initializeMap() {
        for (int row = 0; row < gridWidth; row++) {
            for (int col = 0; col < gridHeight; col++) {
            	cells[row][col] = new Cell(new ArrayList<>(TileSetLoader.tileMap.keySet()));
            }
        }
    }

   /**
     * Run the map generator.
     */
    private void runWaveFunctionCollapse() {
        WaveFunctionCollapse wfc = new WaveFunctionCollapse(cells);
        while (!wfc.isComplete()) {
            wfc.iterate();
        }
        cells = wfc.getCells();
    }
    
    public Cell[][] getCells() {
    	return cells;
    }
}
