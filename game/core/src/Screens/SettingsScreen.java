package Screens;

import OverridenClasses.MenuSlider;
import OverridenClasses.SoundButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

import static Screens.MenuScreen.labelStyle;
import static Screens.MenuScreen.skin;
import static Screens.MenuScreen.textButtonStyle;


public class SettingsScreen extends ScreenAdapter {
    private Stage stage;
    private final MenuScreen menuScreen;
    private final MyGDXGame game;
    private Batch batch;
    private Texture backgroundTexture;
    private Sprite backgroundSprite;

    /**
     * Creates a new instance of SettingsScreen.
     *
     * @param menuScreen The MenuScreen instance to return to when the user clicks the "Back" button.
     * @param game       The main game instance.
     */
    SettingsScreen(MenuScreen menuScreen, MyGDXGame game) {
        this.menuScreen = menuScreen;
        this.game = game;
    }

    /**
     * Called when this screen is displayed. Initializes the UI elements and sets up the input processor.
     */
    @Override
    public void show() {

        Viewport viewport = new ExtendViewport(MyGDXGame.V_WIDTH, MyGDXGame.V_HEIGHT);
        stage = new Stage(viewport);

        backgroundTexture = new Texture(Gdx.files.internal("MenuBack.jpg"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        Table table = new Table();
        table.setFillParent(true);

        Label titleLabel = new Label("Settings", labelStyle);

        MenuSlider soundSlider = new MenuSlider(0, 100, 10, false, skin, 300f);
        MenuSlider musicSlider = new MenuSlider(0, 100, 10, false, skin, 300f);

        SoundButton soundButton = new SoundButton(skin, "sound", 100f);
        SoundButton musicButton = new SoundButton(skin, "music", 100f);

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
        backgroundSprite.draw(batch);
        batch.end();

        stage.draw();
    }

    /**
     * Called when the screen is hidden. Disposes of resources used by the screen.
     */
    @Override
    public void hide() {
        backgroundTexture.dispose();
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
