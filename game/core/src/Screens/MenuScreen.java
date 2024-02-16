package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

import java.io.IOException;

import static com.mygdx.game.MyGDXGame.client;

public class MenuScreen extends ScreenAdapter {
    private Stage stage;
    private Game game;
    private SpriteBatch batch;
    private Sprite backgroundSprite;
    private Texture backgroundTexture;
    private SettingsScreen settingsScreen;

    public MenuScreen(MyGDXGame game) {
        this.game = game;
        settingsScreen = new SettingsScreen(this, game);
    }

    @Override
    public void show() {

        Viewport viewport = new ExtendViewport(MyGDXGame.V_WIDTH, MyGDXGame.V_HEIGHT);
        stage = new Stage(viewport);

        backgroundTexture = new Texture(Gdx.files.internal("MenuBack.jpg")); // Replace with your image file name
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        Table table = new Table();
        table.setFillParent(true);

        // font file downloaded from google fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("PermanentMarker-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 200;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose(); // if not disposed it might cause memory issues

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        Label titleLabel = new Label("Planet X", labelStyle);

        Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        TextButton.TextButtonStyle style = skin.get("round", TextButton.TextButtonStyle.class);
        style.font.getData().setScale(2.5f);

        TextButton singlePlayerButton = new TextButton("SinglePlayer", style);
        TextButton multiPlayerButton = new TextButton("MultiPlayer", style);
        TextButton exitButton = new TextButton("Exit", style);
        TextButton settingsButton = new TextButton("Settings", style);

        singlePlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(MyGDXGame.playScreen);
            }
        });

        multiPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(MyGDXGame.playScreen);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                // this block terminates connection with the server
                client.close();
                try {
                    client.dispose();
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
        table.row(); // Move to the next row for the buttons
        table.add(singlePlayerButton).pad(20f).row();
        table.add(multiPlayerButton).pad(20f).row();
        table.add(settingsButton).pad(20f).row();
        table.add(exitButton).pad(20f);

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    public void render(float delta) {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backgroundSprite.draw(batch);
        batch.end();

        stage.draw();
    }

    @Override
    public void hide() {
        backgroundTexture.dispose();
        batch.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
