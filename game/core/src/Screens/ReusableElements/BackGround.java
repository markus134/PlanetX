package Screens.ReusableElements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * The background class.
 */
public class BackGround {

    private Texture backgroundTexture;
    private Sprite backgroundSprite;

    /**
     * Constructor for the class object.
     */
    public BackGround() {
        backgroundTexture = new Texture(Gdx.files.internal("MenuBack.jpg")); // Replace with your image file name
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Getter method.
     *
     * @return the backgroundSprite.
     */
    public Sprite getBackgroundSprite() {
        return backgroundSprite;
    }

    /**
     * Getter method.
     *
     * @return the backgroundTexture.
     */
    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }
}
