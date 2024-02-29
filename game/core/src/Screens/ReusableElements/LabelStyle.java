package Screens.ReusableElements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * The labelStyle class.
 */
public class LabelStyle extends Label.LabelStyle {

    private final Label.LabelStyle labelStyle;

    /**
     * Constructor.
     */
    public LabelStyle() {
        // font file downloaded from google fonts
        FreeTypeFontGenerator.setMaxTextureSize(2048);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/PermanentMarker-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 200;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose(); // if not disposed it might cause memory issues

        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
    }

    /**
     * Getter method.
     *
     * @return labelStyle
     */
    public Label.LabelStyle getLabelStyle() {
        return labelStyle;
    }
}
