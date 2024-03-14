package Screens.ReusableElements;

public class ResizableTextField extends  com.badlogic.gdx.scenes.scene2d.ui.TextField {
    private final float width;
    private final float height;
    public ResizableTextField(String text, TextFieldStyle skin, float width, float height) {
        super(text, skin);
        this.width = width;
        this.height = height;
    }

    @Override
    public float getPrefWidth() {
        return width;
    }

    @Override
    public float getPrefHeight() {
        return height;
    }

    public int getAlignment () {
        return 2;
    }
}
