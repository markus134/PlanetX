package map;

import util.TileType;

import com.badlogic.gdx.graphics.Texture;

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

    // CustomWorld and chunk.
    private final CustomWorld world;
    private final Chunk chunk;

    // Coordinates within the chunk.
    private int x;
    private int y;

    // General attributes of this tile.
    private Texture texture;
    private Integer type;
    private boolean isSolid = false;


    // TODO: Destruction.
    // TODO: Entities.
    // TODO: Ores.


    // TEMPORARY VARIABLES
    private static Texture tex1 = new Texture("map_test_images/empty.png");
    private static Texture tex2 = new Texture("map_test_images/full.png");
    private static Texture[] textures = { tex1, tex2 };
    // END OF TEMPORARY VARIABLES

   /**
    * Create a tile object.
    *
    * @param chunk Chunk to associate the tile with.
    */
    public Tile(Chunk chunk, int x, int y) {
        this.chunk = chunk;
        this.x = x;
        this.y = y;

        this.world = chunk.getCustomWorld();
        this.texture = textures[(x + y) % 2];
    }

   /**
    * Set the type of this tile.
    *
    * @param tileType Integer indicating what type this tile will be.
    */
    public void setType(int tileType) {
        switch (tileType) {
            case TileType.GROUND: this.tileType
        }
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
    * Get the Texture of this tile.
    *
    * @return Texture object of this tile.
    */
    public Texture getTexture() {
        return this.texture;
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

