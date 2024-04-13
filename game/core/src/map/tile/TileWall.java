package map.tile;

import com.badlogic.gdx.graphics.Texture;

public class TileWall extends Tile {
    private Texture texture = new Texture("map_test_images/full.png");

    public TileWall(int id) {
        super(id);
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public int getWeight() {
        return 1;
    }
}

