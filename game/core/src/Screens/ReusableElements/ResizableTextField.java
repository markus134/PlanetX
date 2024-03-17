package Screens.ReusableElements;

public class ResizableTextField extends com.badlogic.gdx.scenes.scene2d.ui.TextField {
    private final float width;
    private final float height;

    /**
     * Constructor.
     * <p>
     * It allows to make a textfield that can be resized
     *
     * @param text
     * @param skin
     * @param width
     * @param height
     */
    public ResizableTextField(String text, TextFieldStyle skin, float width, float height) {
        super(text, skin);
        this.width = width;
        this.height = height;
    }

    /**
     * Changes the build in method
     *
     * @return
     */
    @Override
    public float getPrefWidth() {
        return width;
    }

    /**
     * Changes the build in method
     *
     * @return
     */
    @Override
    public float getPrefHeight() {
        return height;
    }
}
