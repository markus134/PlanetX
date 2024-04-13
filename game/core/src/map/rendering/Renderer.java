package map.rendering;

import map.CustomWorld;
import map.Chunk;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Define the methods and attributes that every renderer class
 * should contain.
 */
public abstract class Renderer {
    protected SpriteBatch spriteBatch;
    protected CustomWorld world;
    protected Chunk chunk;

    public Renderer(CustomWorld world, Chunk chunk, SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        this.chunk = chunk;
        this.world = world;
    }

    public abstract void draw();
}

