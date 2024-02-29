package Screens.ReusableElements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * The PurpleTextButtonStyle class
 */
public class PurpleTextButtonStyle extends TextButton.TextButtonStyle {

    private final TextButton.TextButtonStyle textButtonStyle;

    /**
     * Constructor.
     */
    public PurpleTextButtonStyle() {
        PurpleSkin skin = new PurpleSkin();
        textButtonStyle = skin.getSkin().get("round", TextButton.TextButtonStyle.class);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.CLEAR); // Set your color here
        pixmap.fill();
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        textButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));

        FreeTypeFontGenerator.setMaxTextureSize(2048);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Nunito-VariableFont_wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        textButtonStyle.font = font;
    }

    /**
     * Getter method.
     *
     * @return textButtonStyle.
     */
    public TextButton.TextButtonStyle getTextButtonStyle() {
        return textButtonStyle;
    }
}
