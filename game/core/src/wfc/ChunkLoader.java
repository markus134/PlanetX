package wfc;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for loading chunks.
 * 
 * Chunks get stored in a HashMap to reduce redundancy.
 * Chunks get loaded around the player.
 */
class ChunkLoader {
	// Linear congruential generation values for every chunk.
	private static final long LCG_MULTIPLIER = 22695477;
	private static final long LCG_MODULUS = 1073741824;
	
    private Map<Integer, Map<Integer, Chunk>> chunks = new HashMap<>();
    private int chunkSize;
    
    private int lastChunkX = 0;
    private int lastChunkY = 0;

    /**
     * Initialize this object with the size of each chunk.
     * 
     * @param chunkSize Size of each chunk (NxN).
     */
    public ChunkLoader(int chunkSize) {
    	this.chunkSize = chunkSize;
    }
    
    /**
     * Load a chunk from the HashMap.
     * If the chunk has not been generated, generate it and
     * store it into the HashMap.
     * 
     * @param chunkX X-Coordinate of the chunk.
     * @param chunkY Y-Coordinate of the chunk.
     * @return Chunk object with collapsed cells.
     */
    public Chunk loadChunk(int chunkX, int chunkY) {
        if (chunks.containsKey(chunkX) && chunks.get(chunkX).containsKey(chunkY)) {
            return chunks.get(chunkX).get(chunkY);
        } else {
            Chunk chunk = generateChunk(chunkX, chunkY);
            chunks.computeIfAbsent(chunkX, k -> new HashMap<>()).put(chunkY, chunk);
            return chunk;
        }
    }

    /**
     * Generate a new chunk.
     * 
     * @param x X-Coordinate of the chunk.
     * @param y Y-Coordiante of the chunk.
     * @return Chunk object with collapsed cells.
     */
    private Chunk generateChunk(int x, int y) {
    	long seed = WfcMapRenderer.seed + x * 17 + y * 31;
        Chunk chunk = new Chunk(x, y, chunkSize, seed);
    	System.out.println("GENERATING CHUNK -> " + chunk);
        
        WaveFunctionCollapse wfc = new WaveFunctionCollapse(chunk.getCells(), chunkSize, seed);
        while (!wfc.isComplete()) {
            wfc.iterate();
        }
        
        chunk.setCells(wfc.getCells());
        return chunk;
    }
    
    /**
     * Update the Player's position and check if they are
     * still in the same chunk. If the player has moved chunks,
     * get new ones.
     * 
     * NOTE: This method may be useful in the future. It is currently redundant
     * The renderer renders around the player.
     * 
     * @param newX X-Coordinate of the Player.
     * @param newY Y-Coordinate of the Player.
     */
    public void updatePlayerPosition(int newX, int newY) {
        int playerChunkX = newX / chunkSize;
        int playerChunkY = newY / chunkSize;
        
        if (playerChunkX != lastChunkX || playerChunkY != lastChunkY) {
            lastChunkX = playerChunkX;
        	lastChunkY = playerChunkY;
        }
    }
}
