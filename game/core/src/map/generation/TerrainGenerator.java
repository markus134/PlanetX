package map.generator;

import map.CustomWorld;
import map.Chunk;

/**
 * Generate Terrain from Simplex noise.
 */
public class TerrainGenerator extends Generator {

   /**
    * Prepare the terrain generator for a world.
    *
    * @param world CustomWorld to generate for.
    */
    public TerrainGenerator(CustomWorld world, Chunk chunk) {
        super(world, chunk);
    }

   /**
    * Generate basic terrain.
    */
    private void base() {

        for (int y = 0; y < chunk.HEIGHT; y++) {
            for (int x = 0; x < chunk.WIDTH; x++) {
                int tile;
                double h = this.noise.eval(x, y);

                if (h <= 0.5) {
                    tile = Tile.GROUND.getId;
                } else {
                    tile = Tile.WALL.getId;
                }

                chunk.setTile(x, y, tile);
            }
        }
    }

   /**
    * Generate all the terrain.
    */
    @Override
    public void generate() {
        this.base();
        print(chunk.getTiles());
    }
}

