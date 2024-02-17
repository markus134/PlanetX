package OverridenClasses;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

public class MenuSlider extends Slider {
    private final float width;
    public MenuSlider(float min, float max, float stepSize, boolean vertical, Skin skin, float width) {
        // extending the class to change the size fo the slider.
        // in libGDX the default value for the width is 140, and it cannot be changed via any built in method
        super(min, max, stepSize, vertical, skin);
        this.width = width;

        this.getStyle().knob.setMinHeight(50f);
        this.getStyle().knob.setMinWidth(50f);
    }
    @Override
    public float getPrefWidth () {
        return width;
    }
}
