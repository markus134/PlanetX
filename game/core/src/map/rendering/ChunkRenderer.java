package map.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import map.Chunk;
import map.CustomWorld;

/**
 * Render a chunk. Chunks can contain terrain, ores, decorative elements,
 * structures and other things. Make use of the {@link Renderer} abstract
 * class.
 */
public class ChunkRenderer extends Renderer {
   /**
    * Create the ChunkRenderer object.
    */
    public ChunkRenderer(CustomWorld world, Chunk chunk, SpriteBatch spriteBatch) {
        super(world, chunk, spriteBatch);
    }

    public void draw() {
        for (int y = 0; y < chunk.HEIGHT; y++) {
            for (int x = 0; x < chunk.WIDTH; x++) {
                int posX = chunk.X_POSITION * Chunk.WIDTH + x;
                int posY = chunk.Y_POSITION * Chunk.WIDTH + y;

                spriteBatch.draw(chunk.getTile(x, y).getTexture(), posX, posY);
            }
        }
    }
}

