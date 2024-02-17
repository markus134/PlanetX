package OverridenClasses;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

/**
 * The MenuSlider class extends the libGDX Slider class to provide a customized slider with a specific width.
 * It overrides the getPrefWidth method to set a custom preferred width for the slider.
 */
public class MenuSlider extends Slider {
    private final float width;

    /**
     * Creates a MenuSlider with the specified minimum, maximum, step size, orientation, skin, and width.
     *
     * @param min      The minimum value of the slider.
     * @param max      The maximum value of the slider.
     * @param stepSize The step size between values.
     * @param vertical True if the slider is vertical, false if horizontal.
     * @param skin     The skin to be used for the slider.
     * @param width    The custom width of the slider.
     */
    public MenuSlider(float min, float max, float stepSize, boolean vertical, Skin skin, float width) {
        // in libGDX the default value for the width is 140, and it cannot be changed via any built in method
        super(min, max, stepSize, vertical, skin);
        this.width = width;

        // Set the minimum height and width of the knob for customization
        this.getStyle().knob.setMinHeight(50f);
        this.getStyle().knob.setMinWidth(50f);
    }

    /**
     * Returns the preferred width of the slider, overriding the default behavior.
     *
     * @return The preferred width of the slider.
     */
    @Override
    public float getPrefWidth() {
        return width;
    }
}
