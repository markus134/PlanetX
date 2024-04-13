package map.generation;

import map.CustomWorld;
import map.Chunk;
import util.OpenSimplexNoise;

import java.util.Random;

public abstract class Generator {

    protected CustomWorld world;
    protected Chunk chunk;
    protected OpenSimplexNoise noise;
    protected Random random;

   /**
    * Create a generator object.
    *
    * @param world World to generate in.
    * @param chunk Chunk to generate in.
    */
    public Generator(CustomWorld world, Chunk chunk) {
        this.world = world;
        this.chunk = chunk;
        this.noise = new OpenSimplexNoise(this.world.getSeed());
        this.random = new Random(this.world.getSeed());
    }

   /**
    * Generate the world.
    */
    public abstract void generate();
}

