package map.rendering;

import map.WorldMap;
import map.Chunk;
import map.Tile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Render a chunk. Chunks can contain terrain, ores, decorative elements,
 * structures and other things. Make use of the {@link Renderer} abstract
 * class.
 */
public class ChunkRenderer {
    private SpriteBatch spriteBatch;
    private WorldMap world;
    private Chunk chunk;

   /**
    * Create the ChunkRenderer object.
    public ChunkRenderer(WorldMap world, Chunk chunk, SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        this.chunk = chunk;
        this.world = world;

        draw();
    }
    */

    public static void draw(WorldMap world, Chunk chunk, SpriteBatch batch) {
        for (int y = 0; y < chunk.HEIGHT; y++) {
            for (int x = 0; x < chunk.WIDTH; x++) {
                Tile tile = chunk.getTile(x, y);
                batch.draw(tile.getTexture(), x, y);
            }
        }
    }
}

