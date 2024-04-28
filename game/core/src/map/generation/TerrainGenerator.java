package map.generation;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import map.Chunk;
import map.CustomWorld;
import map.tile.Tile;

/**
 * Generate Terrain from Simplex noise.
 */
public class TerrainGenerator extends Generator {

   /** * Prepare the terrain generator for a world.
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
                int tileX = chunk.X_POSITION * Chunk.WIDTH + x;
                int tileY = chunk.Y_POSITION * Chunk.HEIGHT + y;

                double h = this.noise.eval(tileX, tileY);

                if (h <= 0.4) {
                    tile = Tile.GROUND.id;
                } else {
                    tile = Tile.WALL.id;
                }

                chunk.setTile(x, y, tile);
                // System.out.println("Generating tiles for: " + chunkX + " " + chunkY);
            }
        }
    }

   /**
    * Add collision to the chunk.
    */
    private void collision() {
        for (int y = 0; y < chunk.HEIGHT; y++) {
            for (int x = 0; x < chunk.WIDTH; x++) {
                if (chunk.getTile(x, y).isSolid()) {
                    int tileX = chunk.X_POSITION * Chunk.WIDTH + x;
                    int tileY = chunk.Y_POSITION * Chunk.HEIGHT + y;

                    addBox2DBody(tileX, tileY);
                }
            }
        }
    }

   /**
    * Helper method for adding Box2D bodies to the tile.
    *
    * @param x X-Coordinate of the tile.
    * @param y Y-Coordinate of the tile.
    */
    private void addBox2DBody(int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((x + 0.5f) * Tile.WIDTH, (y + 0.5f) * Tile.HEIGHT);

        Body body = world.getBox2DWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Tile.WIDTH * 0.5f, Tile.HEIGHT * 0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.0f;

        body.createFixture(fixtureDef);

        shape.dispose();
    }

   /**
    * Generate all aspects of the terrain.
    */
    @Override
    public void generate() {
        this.base();
        this.collision();
    }
}

