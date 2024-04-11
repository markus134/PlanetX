package wfc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Render the map to the screen.
 * 
 * This is the only class in the package that
 * should be interfaced with. No other class
 * in this package should be accessible to outsiders.
 */
public class WfcMapRenderer {
	public static final int CHUNK_RADIUS = 64;  // View distance. Chunks rendered around the player.
    public static final int CHUNK_SIZE = 4;
    public static final int CELL_SIZE = 2;
    
    public static final int RANDOM_MAX = 999999;

    public static long seed;
    
    private ChunkLoader chunkLoader;
    private TileSetLoader tileSetLoader;

    private int playerX = 0;
    private int playerY = 0;
    
    Texture cellTexture = new Texture("map.png");  // This is for testing.
    Texture emptyTexture = new Texture("map.png");  // This is for testing. 

    /**
     * Create cells, get tiles and run the algorithm.
     * TODO: Files creates tiles. The file is a parameter to this constructor.
     */
    public WfcMapRenderer(Vector2 playerPosition, long seed) {
    	chunkLoader = new ChunkLoader(CHUNK_SIZE);
    	tileSetLoader = new TileSetLoader("levels/test.json");
    	updatePlayerPosition(playerPosition);
    	
    	this.seed = (seed == 0) ? new Random().nextInt(RANDOM_MAX) : seed;
    }

    /**
     * Render chunks that are a grid of cells around the player.
     * 
     * @param batch SpriteBatch object.
     */
    public void render(SpriteBatch batch) {
        int playerChunkX = playerX / (CHUNK_SIZE * CELL_SIZE);
        int playerChunkY = playerY / (CHUNK_SIZE * CELL_SIZE);
        
        int startX = playerChunkX - (CHUNK_RADIUS / 2);
        int startY = playerChunkY - (CHUNK_RADIUS / 2);

        for (int y = 0; y < CHUNK_RADIUS; y++) {
            for (int x = 0; x < CHUNK_RADIUS; x++) {
                int chunkX = startX + x;
                int chunkY = startY + y;
                
                renderChunk(batch, chunkX, chunkY);
            }
        }
    }
    
    /**
     * Render a chunk.
     * 
     * @param batch SpriteBatch object.
     * @param chunkX X-position of the chunk.
     * @param chunkY Y-position of the chunk.
     */
    private void renderChunk(SpriteBatch batch, int chunkX, int chunkY) {
        Chunk chunk = chunkLoader.loadChunk(chunkX, chunkY);
        // System.out.println("LOADING CHUNK -> " + chunk);
        
        if (chunk != null) {
            int chunkStartX = chunkX * CHUNK_SIZE * CELL_SIZE;
            int chunkStartY = chunkY * CHUNK_SIZE * CELL_SIZE;

            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int x = 0; x < CHUNK_SIZE; x++) {
                    int pixelX = chunkStartX + x * CELL_SIZE;
                    int pixelY = chunkStartY + y * CELL_SIZE;

                    Cell cell = chunk.getCells()[x][y];

                	cellTexture = TileSetLoader.tileMap.get(cell.getOptions().get(0)).getTexture();
                	int collision = TileSetLoader.tileMap.get(cell.getOptions().get(0)).getCollision();

                    renderCell(batch, pixelX, pixelY);
                }
            }
        }
    }

    /**
     * Render a single cell at the specified position.
     */
    private void renderCell(SpriteBatch batch, int x, int y) {
        batch.draw(cellTexture, x, y, CELL_SIZE, CELL_SIZE);
    }
    
    public void updatePlayerPosition(Vector2 position) {
    	int newX = (int) position.x;
    	int newY = (int) position.y;
    	
        chunkLoader.updatePlayerPosition(newX, newY);
        playerX = newX;
        playerY = newY;
    }
}
