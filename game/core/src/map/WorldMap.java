package map;

import map.rendering.ChunkRenderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Handle the world data. Load entities and chunks from a file.
 * Generate new chunks, entities, ores.
 */
public class WorldMap {
    private static int worldId;

    // Parameters for the world generator. TODO: Create a config file

    // Id number of this world.
    private final int id;

    // Seed for this world.
    private final long seed;

    // SpriteBatch for this world.
    private SpriteBatch spriteBatch;

    // TEMPORARY VARIABLES
    private Chunk chunk;


   /**
    * Create a World.
    *
    * @param seed Long Integer representing the seed.
    */
    public WorldMap(long seed, SpriteBatch spriteBatch) {
        this.seed = seed;
        this.spriteBatch = spriteBatch;

        this.id = worldId++;
    }

   /**
    * Generate the world according to input parameters.
    */
    public void generateWorld() {
        chunk = new Chunk(this, 0, 0);
    }

   /**
    * Render the current and surrounding chunks near the player.
    */
    public void renderWorld() {
        new ChunkRenderer(this, this.chunk, spriteBatch);
    }

   /**
    * Get the seed of this world.
    *
    * @return Long Integer containing the seed.
    */
    public long getSeed() {
        return this.seed;
    }

   /**
    * Get the id number of this world.
    *
    * @return Integer containing the id.
    */
    public long getId() {
        return this.id;
    }

   /**
    * Get the string representation of this object.
    *
    * @return String representation of a World.
    */
    public String toString() {
        return "World{"
                + "id=" + this.id + ";"
                + "seed=" + this.seed + "}";
    }
}

