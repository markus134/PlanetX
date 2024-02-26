package wfc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Render the map to the screen.
 * 
 * This is the only class in the package that
 * should be interfaced with. No other class
 * in this package should be accessible.
 */
public class TileMapRenderer {
    public static final int GRID_WIDTH = 16;
    public static final int GRID_HEIGHT = 16;
    public static final int GRID_CELL_SIZE = 32;
    
    private TileMap mapData;

    private int playerX = 0;
    private int playerY = 0;
    
    Texture cellTexture = new Texture("map.png");
    Texture mapTexture = new Texture("map.png");

    /**
     * Create cells, get tiles and run the algorithm.
     * TODO: File creates tiles. The file is a parameter to this constructor.
     */
    public TileMapRenderer() {
    	mapData = new TileMap(GRID_WIDTH, GRID_HEIGHT, GRID_CELL_SIZE);
    }

    /**
     * Render the 16x16 grid around the player's position.
     */
    public void render(SpriteBatch batch) {
        int startX = playerX - (GRID_WIDTH / 2 * GRID_CELL_SIZE);
        int startY = playerY - (GRID_HEIGHT / 2 * GRID_CELL_SIZE);

        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                int pixelX = startX + x * GRID_CELL_SIZE;
                int pixelY = startY + y * GRID_CELL_SIZE;

                Cell cell = mapData.getCells()[y][x];
                
                if (cell.getOptions().size() > 0) {
                	cellTexture = TileSetLoader.tileMap.get(cell.getOptions().get(0)).getTexture();
                } else {
                	cellTexture = mapTexture;
                }
                
                renderCell(batch, pixelX, pixelY);
            }
        }
    }

    /**
     * Render a single cell at the specified position.
     */
    private void renderCell(SpriteBatch batch, int x, int y) {
        batch.draw(cellTexture, x, y, GRID_CELL_SIZE, GRID_CELL_SIZE);
    }
}
