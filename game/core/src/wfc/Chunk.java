package wfc;

import java.util.ArrayList;

/**
 * Chunk data.
 */
class Chunk {
    private int x;
    private int y;
    private Cell[][] cells;
    private int gridSize;

    /**
     * Initialize chunk at specific position with size.
     *
     * @param x        X-position of the chunk.
     * @param y        Y-position of the chunk.
     * @param gridSize Number of cells in the chunk (NxN).
     */
    public Chunk(int x, int y, int gridSize) {
        this.x = x;
        this.y = y;
        this.gridSize = gridSize;

        cells = new Cell[gridSize][gridSize];
        initializeCells();
    }

    /**
     * Initialize cells in this chunk to maximum entropy.
     */
    private void initializeCells() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new Cell(new ArrayList<>(TileSetLoader.tileMap.keySet()));
            }
        }
    }

    /**
     * Get cells.
     *
     * @return 2D Array of cells.
     */
    public Cell[][] getCells() {
        return cells;
    }

    /**
     * Set cells.
     *
     * @param cells 2D Array of cells.
     */
    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    /**
     * Display the x-coordinate, y-coordinate and the number of cells in the chunk.
     */
    @Override
    public String toString() {
        return "Chunk{" +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "cells=" + cells.length + "}";
    }
}
