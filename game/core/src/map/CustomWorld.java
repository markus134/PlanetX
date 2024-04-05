package map;

import map.rendering.ChunkRenderer;
import map.generaiton.Generator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Handle the world data. Load entities and chunks from a file.
 * Generate new chunks, entities, ores.
 */
public class CustomWorld {
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
    * Create a CustomWorld.
    *
    * @param seed Long Integer representing the seed.
    */
    public CustomWorld(long seed, SpriteBatch spriteBatch) {
        this.seed = seed;
        this.spriteBatch = spriteBatch;

        this.id = worldId++;
    }

   /**
    * Generate the world according to input parameters.
    */
    public void generateCustomWorld() {
        chunk = new Chunk(this, 0, 0);
        Generator.generate();
    }

   /**
    * Render the current and surrounding chunks near the player.
    */
    public void renderCustomWorld() {
        ChunkRenderer.draw(this, this.chunk, spriteBatch);
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
    * @return String representation of a CustomWorld.
    */
    public String toString() {
        return "CustomWorld{"
                + "id=" + this.id + ";"
                + "seed=" + this.seed + "}";
    }
}

