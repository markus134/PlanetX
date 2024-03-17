package Screens.ReusableElements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TextFieldStyleForInput extends TextField.TextFieldStyle {

    private TextField.TextFieldStyle style;

    /**
     * Constructor
     *
     * @param size
     */
    public TextFieldStyleForInput(int size) {
        style = new TextField.TextFieldStyle();
        FreeTypeFontGenerator.setMaxTextureSize(2048);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Nunito-VariableFont_wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        style.font = font;

        Pixmap pixmap = new Pixmap(1, 100, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Drawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        style.background = drawable;

        style.fontColor = Color.BLACK;
        style.cursor = drawable;
        style.selection = drawable;
    }

    /**
     * Gets the style
     *
     * @return
     */
    public TextField.TextFieldStyle getStyle() {
        return style;
    }
}
