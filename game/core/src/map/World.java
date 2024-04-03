package map;

/**
 * Handle the world data. Load entities and chunks from a file.
 * Generate new chunks, entities, ores.
 */
public class World {
    private static int worldId;

    // Parameters for the world generator. TODO: Create a config file

    // Id number of this world.
    private final int ID;

    // Seed for this world.
    private final long SEED;


   /**
    * Create a World.
    *
    * @param seed Long Integer representing the seed.
    */
    public World(long seed) {
        this.SEED = seed;
        this.ID = worldId++;
    }

   /**
    * Generate the world according to input parameters.
    */
    private void generateWorld() {
    }

   /**
    * Render the current and surrounding chunks near the player.
    */
    private void renderWorld() {
    }

   /**
    * Get the seed of this world.
    *
    * @return Long Integer containing the seed.
    */
    public long getSeed() {
        return this.SEED;
    }

   /**
    * Get the id number of this world.
    *
    * @return Integer containing the id.
    */
    public long getId() {
        return this.ID;
    }

   /**
    * Get the string representation of this object.
    *
    * @return String representation of a World.
    */
    public String toString() {
        return "World{"
                + "id=" + this.ID + ";"
                + "seed=" + this.SEED + "}";
    }
}

