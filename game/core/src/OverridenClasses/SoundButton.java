package OverridenClasses;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SoundButton extends com.badlogic.gdx.scenes.scene2d.ui.Button {
    private float size;
    public SoundButton(Skin skin, String style, float size){
        // extending the class to change the size fo the button.
        // in libGDX the default value for the width is 140, and it cannot be changed via any built in method
        super(skin, style);
        this.size = size;

        this.setChecked(true);
        this.setDisabled(true);
    }

    @Override
    public float getPrefWidth(){
        return size;
    }

    @Override
    public float getPrefHeight(){
        return size;
    }
}
