package crystals;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyGDXGame;

public class Crystal extends Sprite {
    private final float x;
    private final float y;
    private final Texture texture;
    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 32;

    public Crystal(float x, float y) {
        texture = new Texture("items/crystal.png");

        this.x = x;
        this.y = y;

        setBounds(x / MyGDXGame.PPM, y / MyGDXGame.PPM, FRAME_WIDTH / MyGDXGame.PPM, FRAME_HEIGHT / MyGDXGame.PPM);
        setRegion(texture);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
