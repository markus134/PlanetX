package Screens;

import Screens.ReusableElements.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

public class SettingsScreen extends ScreenAdapter {
    private Stage stage;
    private final MenuScreen menuScreen;
    private final MyGDXGame game;
    private Batch batch;
    private BackGround backGround;
    private final Music music;
    public static float musicValue = .1f;
    public static float soundValue = .1f;
    private boolean soundON = true;
    private float tempForSoundValue;
    private float tempForMusicValue;

    /**
     * Creates a new instance of SettingsScreen.
     *
     * @param menuScreen The MenuScreen instance to return to when the user clicks the "Back" button.
     * @param game       The main game instance.
     */
    SettingsScreen(MenuScreen menuScreen, MyGDXGame game, Music music) {
        this.menuScreen = menuScreen;
        this.game = game;
        this.music = music;
    }

    /**
     * Called when this screen is displayed. Initializes the UI elements and sets up the input processor.
     */
    @Override
    public void show() {

        Viewport viewport = new ExtendViewport(MyGDXGame.V_WIDTH, MyGDXGame.V_HEIGHT);
        stage = new Stage(viewport);

        backGround = new BackGround();
        batch = new SpriteBatch();

        Table table = new Table();
        table.setFillParent(true);

        // from the ReusableElements directory
        Label.LabelStyle labelStyle = new LabelStyle(200).getLabelStyle();
        Label titleLabel = new Label("Settings", labelStyle);

        // from the ReusableElements directory
        Skin skin = new PurpleSkin().getSkin();

        MenuSlider soundSlider = new MenuSlider(0, 1, .01f, false, skin, 300f);
        soundSlider.setValue(soundValue);
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // When the slider value changes, update the volume of the music
                soundValue = soundSlider.getValue();
            }
        });

        MenuSlider musicSlider = new MenuSlider(0, 1, .01f, false, skin, 300f);
        musicSlider.setValue(music.getVolume());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // When the slider value changes, update the volume of the music
                music.setVolume(musicSlider.getValue());
                musicValue = music.getVolume();
            }
        });

        SoundButton soundButton = new SoundButton(skin, "sound", 100f);
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                // one might argue that the temporary value is redundant,
                // but it is actually necessary due to the soundSlider listener
                // that overrides the soundValue every time the sliders value
                // is set to 0.
                if (soundON) {
                    // if the sound is on, turn it off
                    tempForSoundValue = soundSlider.getValue();
                    soundSlider.setValue(0);
                    soundON = false;
                } else {
                    // vice versa
                    soundSlider.setValue(tempForSoundValue);
                    soundON = true;
                }
            }
        });

        SoundButton musicButton = new SoundButton(skin, "music", 100f);
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (music.isPlaying()) {
                    // If the music is currently playing, pause it
                    music.pause();
                    tempForMusicValue = music.getVolume();
                    musicSlider.setValue(0);
                } else {
                    // If the music is currently paused, play it
                    music.play();
                    musicSlider.setValue(tempForMusicValue);
                }
            }
        });

        // from the ReusableElements directory
        TextButton.TextButtonStyle textButtonStyle = new PurpleTextButtonStyle().getTextButtonStyle();
        TextButton backButton = new TextButton("Back", textButtonStyle);
        TextButton someButton = new TextButton("For future use", textButtonStyle);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(menuScreen);
            }
        });

        // aligning the table
        table.add(titleLabel).padBottom(30f).center().colspan(2).row();
        table.row();
        table.add(soundButton).pad(20f);
        table.add(soundSlider).padBottom(20f).row();
        table.add(musicButton).pad(20f);
        table.add(musicSlider).padBottom(20f).row();
        table.add(someButton).pad(20f).row();
        table.add(backButton).center().colspan(2).padTop(40f);

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Renders the screen with the specified delta time.
     *
     * @param delta The time in seconds since the last render.
     */
    public void render(float delta) {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backGround.getBackgroundSprite().draw(batch);
        batch.end();

        stage.draw();
    }

    /**
     * Called when the screen is hidden. Disposes of resources used by the screen.
     */
    @Override
    public void hide() {
        backGround.getBackgroundTexture().dispose();
        batch.dispose();
        stage.dispose();
    }

    /**
     * Called when the screen is resized. Updates the stage's viewport accordingly.
     *
     * @param width  The new width.
     * @param height The new height.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
