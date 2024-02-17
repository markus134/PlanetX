package OverridenClasses;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * The SoundButton class extends the libGDX Button class to provide a customized button with a specific size.
 * It overrides the getPrefWidth and getPrefHeight methods to set custom preferred dimensions for the button.
 */
public class SoundButton extends com.badlogic.gdx.scenes.scene2d.ui.Button {
    private float size;

    /**
     * Creates a SoundButton with the specified skin, style, and size.
     *
     * @param skin  The skin to be used for the button.
     * @param style The style to be applied to the button.
     * @param size  The custom size of the button.
     */
    public SoundButton(Skin skin, String style, float size) {
        // in libGDX the default value for the width is 140, and it cannot be changed via any built in method
        super(skin, style);
        this.size = size;

        // Set the initial state and disable the button
        this.setChecked(true);
        this.setDisabled(true);
    }

    /**
     * Returns the preferred width of the button, overriding the default behavior.
     *
     * @return The preferred width of the button.
     */
    @Override
    public float getPrefWidth() {
        return size;
    }

    /**
     * Returns the preferred height of the button, overriding the default behavior.
     *
     * @return The preferred height of the button.
     */
    @Override
    public float getPrefHeight() {
        return size;
    }
}
