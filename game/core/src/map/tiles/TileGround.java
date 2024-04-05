package map.tile;

import com.badlogic.gdx.graphics.Texture;

public class TileGround extends Tile {
    private Texture texture = new Texture("map_test_images/empty.png");

    public TileGround(int id) {
        super(id);
    }

    public Texture getTexture() {
        return this.texture;
    }
}

