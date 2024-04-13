package map.tile;

import com.badlogic.gdx.graphics.Texture;

public abstract class Tile {
    public static final int WIDTH = 1;
    public static final int HEIGHT = 1;

    public static final int MAX_TILES = 64;
    public static final Tile[] TILES = new Tile[MAX_TILES];

    // public static final TileAir AIR = new TileAir(0);
    public static final TileGround GROUND = new TileGround(1);
    public static final TileWall WALL = new TileWall(2);

    public final int id;

   /**
    * Create a tile.
    * Tiles must have a unique id.
    *
    * @param id ID number of the tile.
    */
    public Tile(int id) {
        this.id = id;
        assert(Tile.TILES[this.id] == null);
        Tile.TILES[this.id] = this;
    }

   /**
    * Get the texture of this tile
    *
    * @return Texture of the tile.
    */
    public abstract Texture getTexture();

   /**
    * Check if this tile is solid.
    *
    * @return boolean indicating the solidity of this tile.
    */
    public boolean isSolid() {
        return false;
    }

    public int getId() {
        return this.id;
    }

    public int getWeight() {
        return 0;
    }
}
