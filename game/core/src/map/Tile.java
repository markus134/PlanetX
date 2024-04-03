package map;

/**
 * Tiles are the smallest discrete elements of a map. Tiles
 * contain information about the entities on top of them, the
 * ores contained within them, the state of its solidity,
 * the state of its destructibility.
 * All tiles are associated with a {@link Chunk}.
 */
public class Tile {
    // Tile width and height.
    public static final int WIDTH = 1;
    public static final int HEIGHT = 1;

    // Associated World object.
    private final World world;

    // Associated Chunk object.
    private final Chunk chunk;

    // Coordinates within the chunk.
    private int x;
    private int y;

    // General attributes of this tile.
    private boolean isSolid = false;


    // TODO: Destruction.
    // TODO: Entities.
    // TODO: Ores.

   /**
    * Create a tile object.
    *
    * @param chunk Chunk to associate the tile with.
    */
    public Tile(Chunk chunk, int x, int y) {
        this.chunk = chunk;
        this.x = x;
        this.y = y;

        this.world = chunk.getWorld();
    }

   /**
    * Get the chunk.
    *
    * @return Chunk object associated with this tile.
    */
    public Chunk getChunk() {
        return this.chunk;
    }

   /**
    * Get the X-Coordinate.
    *
    * @return X-Coordinate of this tile within its chunk.
    */
    public int getX() {
        return this.x;
    }

   /**
    * Get the Y-Coordinate.
    *
    * @return Y-Coordinate of this tile within its chunk.
    */
    public int getY() {
        return this.y;
    }

   /**
    * Check if this tile is solid.
    *
    * @return Boolean indicating whether this tile is solid.
    */
    public boolean isSolid() {
        return isSolid;
    }

   /**
    * Set this tile to a solid.
    */
    public void setSolid() {
        this.isSolid = true;
    }

   /**
    * Get the string representation of this object.
    *
    * @return String representation of a Tile.
    */
    @Override
    public String toString() {
        return "Tile{"
                + "chunk=" + this.chunk.toString() + ";"
                + "x=" + this.x + ";"
                + "y=" + this.y + "}";
    }
}

