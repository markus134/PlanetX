package map;

import map.tile.Tile;

/**
 * Each chunk is defined as an area spanning x by y tiles.
 * Chunks can see what chunks are next to it. Chunks do not
 * generate or render anything, they are meant to be containers
 * to other classes which generate the chunk content.
 */
public class Chunk {
    // Width and height in tiles. These should always be the same.
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;

    // The world the chunk is in.
    private final CustomWorld world;

    // Position in the world.
    public final int X_POSITION;
    public final int Y_POSITION;

    // Tiles in this chunk.
    private Tile[][] tileArray = new Tile[HEIGHT][WIDTH];

    // Entities.
    // TODO

    // Tiles containing ores.
    // TODO

   /**
    * Create a chunk.
    *
    * @param world CustomWorld of this chunk.
    * @param x X-Coordinate of this chunk.
    * @param y Y-Coordinate of this chunk.
    */
    public Chunk(CustomWorld world, int x, int y) {
        this.world = world;
        this.X_POSITION = x;
        this.Y_POSITION = y;
    }

   /**
    * Get a specific tile from this chunk.
    *
    * @param x X-Coordinate of the Tile.
    * @param y Y-Coordinate of the Tile.
    * @return Tile object in this position.
    */
    public Tile getTile(int x, int y) {
        if (0 > x || x > WIDTH) {
            return null;
        }

        if (0 > y || y > HEIGHT) {
            return null;
        }

        return tileArray[y][x];
    }

   /**
    * Get a 2-D array of all tiles.
    *
    * @return All tiles.
    */
    public Tile[][] getTiles() {
        return tileArray;
    }

   /**
    * Set the tile.
    * This means that the generator has run over this tile.
    *
    * @param x X-Coordinate of the tile in this chunk.
    * @param y Y-Coordinate of the tile in this chunk.
    * @param type Type of the tile.
    */
    public void setTile(int x, int y, int type) {
        tileArray[y][x] = Tile.TILES[type];
    }

   /**
    * Get the CustomWorld object of this chunk.
    *
    * @return CustomWorld object associated with this chunk.
    */
    public CustomWorld getCustomWorld() {
        return this.world;
    }

   /**
    * Get the string representation of this object.
    *
    * @return String representation of a Chunk.
    */
    @Override
    public String toString() {
        return "Chunk{"
                + "x=" + X_POSITION + ";"
                + "y=" + Y_POSITION + "}";
    }
}

