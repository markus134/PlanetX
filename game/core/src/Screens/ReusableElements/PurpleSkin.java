package Screens.ReusableElements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * The PurpleSkin class.
 */
public class PurpleSkin extends Skin{

    private final Skin skin;

    /**
     * Constructor.
     */
    public PurpleSkin() {
        skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
    }

    /**
     * Getter method.
     * @return skin
     */
    public Skin getSkin() {
        return skin;
    }
}
