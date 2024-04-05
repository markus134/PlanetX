package map.tile;

import com.badlogic.gdx.graphics.Texture;

public class TileWall extends Tile {
    public TileWall(int id) {
        super(id);
    }

    public boolean getTexture() {
        return super.getTexture("map_test_images/full.png");
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}

