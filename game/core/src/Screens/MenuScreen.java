package Screens;

import Screens.ReusableElements.BackGround;
import Screens.ReusableElements.LabelStyle;
import Screens.ReusableElements.PurpleTextButtonStyle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
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

import java.io.IOException;


public class MenuScreen extends ScreenAdapter {
    private Stage stage;
    private final MyGDXGame game;
    private SpriteBatch batch;
    private BackGround backGround;
    private final SettingsScreen settingsScreen;
    public final Music music;
    public final SinglePlayerScreen singlePlayerScreen;
    public final MultiPlayerScreen multiPlayerScreen;

    /**
     * Constructor for the MenuScreen.
     *
     * @param game The Game instance representing the main game.
     */
    public MenuScreen(MyGDXGame game, Music music) {
        this.game = game;
        this.music = music;
        this.singlePlayerScreen = new SinglePlayerScreen(this, game, music);
        this.multiPlayerScreen = new MultiPlayerScreen(this, game, music);
        this.settingsScreen = new SettingsScreen(this, game, music);
    }

    /**
     * Called when the MenuScreen is displayed.
     */
    @Override
    public void show() {

        Viewport viewport = new ExtendViewport(MyGDXGame.V_WIDTH, MyGDXGame.V_HEIGHT);
        stage = new Stage(viewport);

        // from the ReusableElements directory
        backGround = new BackGround();
        batch = new SpriteBatch();

        Table table = new Table();
        table.setFillParent(true);

        // from the ReusableElements directory
        Label.LabelStyle labelStyle = new LabelStyle(200).getLabelStyle();
        Label titleLabel = new Label("Planet X", labelStyle);

        // from the ReusableElements directory
        TextButton.TextButtonStyle textButtonStyle = new PurpleTextButtonStyle().getTextButtonStyle();
        TextButton singlePlayerButton = new TextButton("SinglePlayer", textButtonStyle);
        TextButton multiPlayerButton = new TextButton("MultiPlayer", textButtonStyle);
        TextButton exitButton = new TextButton("Exit", textButtonStyle);
        TextButton settingsButton = new TextButton("Settings", textButtonStyle);

        singlePlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(singlePlayerScreen);
            }
        });

        multiPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(multiPlayerScreen);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                // this block terminates connection with the server
                game.client.close();
                try {
                    game.client.dispose();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    game.uuidFileManager.releaseUUID(game.playerUUID);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // terminates the window
                Gdx.app.exit();

                // terminates the process
                System.exit(0);
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(settingsScreen);
            }
        });

        table.add(titleLabel).padBottom(20f).center();
        table.row();
        table.add(singlePlayerButton).pad(20f).row();
        table.add(multiPlayerButton).pad(20f).row();
        table.add(settingsButton).pad(20f).row();
        table.add(exitButton).pad(20f);

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Renders the MenuScreen.
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
     * Opens the HandleFullWorld screen when called.
     */
    public HandleFullWorld getHandleFullWorldScreen() {
        return this.multiPlayerScreen.getHandleFullWorld();
    }

    /**
     * Called when the MenuScreen is no longer the current screen.
     */
    @Override
    public void hide() {
        backGround.getBackgroundTexture().dispose();
        batch.dispose();
        stage.dispose();
    }

    /**
     * Called when the window is resized.
     *
     * @param width  The new width.
     * @param height The new height.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
