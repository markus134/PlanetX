package map;

import map.generation.TerrainGenerator;
import map.rendering.ChunkRenderer;
import Sprites.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;

import java.util.Map;
import java.util.HashMap;

/**
 * Handle the world data. Load entities and chunks from a file.
 * Generate new chunks, entities, ores.
 */
public class CustomWorld {
    // Generate around the player (current chunk and n surrounding)
    private static final int GEN_RADIUS = 1;
    // Render around the player (all surrounding generated chunks)
    private static final int REN_RADIUS = 1;

    // Parameters for the world generator.
    // TODO: Create a config file

    // Seed for this world.
    private final long seed;

    // Box2D world.
    private World box2DWorld;

    // SpriteBatch for this world.
    private SpriteBatch spriteBatch;

    // HashMap of all chunks.
    Map<Integer, HashMap<Integer, Chunk>> chunks = new HashMap<>();


   /**
    * Create a CustomWorld.
    *
    * @param seed Long Integer representing the seed.
    */
    public CustomWorld(long seed, World world, SpriteBatch spriteBatch) {
        this.seed = seed;
        this.box2DWorld = world;
        this.spriteBatch = spriteBatch;
    }

   /**
    * Generate the world according to input parameters.
    */
    public void generateWorld(Player player) {
        Vector2 playerPosition = player.b2body.getPosition();
        int chunkX = (int) Math.floor(playerPosition.x / Chunk.WIDTH);
        int chunkY = (int) Math.floor(playerPosition.y / Chunk.HEIGHT);

        // Generate new chunks around the player.
        for (int y = -GEN_RADIUS; y <= GEN_RADIUS; y++) {
            for (int x = -GEN_RADIUS; x <= GEN_RADIUS; x++) {
                Vector2 chunkCoords = new Vector2(chunkX + x, chunkY + y);

                if (!hasKey(chunkCoords)) {
                    // System.out.println(chunkCoords);
                    Chunk chunk = new Chunk(this, chunkX + x, chunkY + y);
                    new TerrainGenerator(this, chunk).generate();
                    this.putToChunks(chunkCoords, chunk);
                }
            }
        }
    }

   /**
    * Render the current and surrounding chunks near the player.
    */
    public void renderWorld(Player player) {
        Vector2 playerPosition = player.b2body.getPosition();
        int chunkX = (int) Math.floor(playerPosition.x / Chunk.WIDTH);
        int chunkY = (int) Math.floor(playerPosition.y / Chunk.HEIGHT);

        // Render around the player.
        for (int y = -GEN_RADIUS; y <= GEN_RADIUS; y++) {
            for (int x = -GEN_RADIUS; x <= GEN_RADIUS; x++) {
                Vector2 chunkCoords = new Vector2(chunkX + x, chunkY + y);
                Chunk chunk = getChunk(chunkCoords);

                if (chunk != null) {
                    new ChunkRenderer(this, chunk, this.spriteBatch).draw();
                }
            }
        }
    }

   /**
    * Helper method for seeing if a chunk is already generated.
    *
    * @param vector Vector2 object to check.
    * @return Boolean showing if the chunk is in the hashmap.
    */
    private boolean hasKey(Vector2 vector) {
        int x = (int) vector.x;
        int y = (int) vector.y;

        if (!this.chunks.containsKey(x))
            return false;

        if (!this.chunks.get(x).containsKey(y))
            return false;

        return true;
    }

   /**
    * Put a Vector2 with a chunk into the chunks hashmap.
    *
    * @param vector Vector2 object to add.
    * @param chunk Chunk object to add.
    */
    private void putToChunks(Vector2 vector, Chunk chunk) {
        int x = (int) vector.x;
        int y = (int) vector.y;

        if (!this.chunks.containsKey(x))
            this.chunks.put(x, new HashMap<>());

        this.chunks.get(x).put(y, chunk);
    }

   /**
    * Get a Chunk from Vector2 coordinates.
    *
    * @param vector Vector2 object.
    * @return Chunk or null.
    */
    private Chunk getChunk(Vector2 vector) {
        int x = (int) vector.x;
        int y = (int) vector.y;

        if (!this.chunks.containsKey(x))
            return null;

        if (!this.chunks.get(x).containsKey(y))
            return null;

        return this.chunks.get(x).get(y);
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
    * Get the Box2D World object associated with this world.
    *
    * @return Box2D World object.
    */
    public World getBox2DWorld() {
        return this.box2DWorld;
    }

   /**
    * Get the string representation of this object.
    *
    * @return String representation of a CustomWorld.
    */
    public String toString() {
        return "CustomWorld{"
                + "seed=" + this.seed + "}";
    }
}

