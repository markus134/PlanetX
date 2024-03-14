package Screens.ReusableElements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * The labelStyle class.
 */
public class LabelForTable extends Label.LabelStyle {

    private final Label.LabelStyle labelForTable;

    /**
     * Constructor.
     */
    public LabelForTable(int size) {
        labelForTable = new Label.LabelStyle();

        FreeTypeFontGenerator.setMaxTextureSize(2048);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Nunito-VariableFont_wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        labelForTable.font = font;
    }

    /**
     * Getter method.
     *
     * @return labelStyle
     */
    public Label.LabelStyle getLabelStyle() {
        return labelForTable;
    }
}
