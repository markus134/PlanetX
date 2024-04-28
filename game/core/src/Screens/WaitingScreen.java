package Screens;

import Screens.ReusableElements.BackGround;
import Screens.ReusableElements.LabelForTable;
import Screens.ReusableElements.LabelStyle;
import Screens.ReusableElements.PurpleTextButtonStyle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
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
import serializableObjects.PlayerLeavesWaitingScreen;

public class WaitingScreen extends ScreenAdapter {

    private Stage stage;
    private final MyGDXGame game;
    private final MultiPlayerScreen multiPlayerScreen;
    private Batch batch;
    private BackGround backGround;
    public static int currentPlayers;
    public int previousNumberOfPlayers;
    public static int maxPlayers;
    private final Music music;
    private Table table;
    /**
     * Constructor
     *
     * @param game
     */
    public WaitingScreen(MultiPlayerScreen screen, MyGDXGame game, Music music) {
        this.multiPlayerScreen = screen;
        this.game = game;
        this.music = music;
    }

    /**
     * This is what is shown when the screen is displayed
     */
    @Override
    public void show() {
        Viewport viewport = new ExtendViewport(MyGDXGame.V_WIDTH, MyGDXGame.V_HEIGHT);
        stage = new Stage(viewport);

        backGround = new BackGround();
        batch = new SpriteBatch();

        table = new Table();
        table.setFillParent(true);

        updateWaitingScreen();
    }

    /**
     * Updates the screen. It is used to change the numbers
     */
    public void updateWaitingScreen() {
        System.out.println("Updating screen");
        System.out.println(currentPlayers + " current players");
        System.out.println(maxPlayers + " max players");

        table.clear();
        stage.clear();

        Label.LabelStyle waitingLabelStyle = new LabelStyle(200).getLabelStyle();
        Label waitingLabel = new Label("Waiting...", waitingLabelStyle);

        Label.LabelStyle textStyle = new LabelForTable(60).getLabelStyle();
        Label textLabel = new Label(currentPlayers + " / " + maxPlayers + " are ready", textStyle);

        TextButton.TextButtonStyle textButtonStyle = new PurpleTextButtonStyle().getTextButtonStyle();
        TextButton backButton = new TextButton("Back", textButtonStyle);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(multiPlayerScreen);
                game.client.sendTCP(new PlayerLeavesWaitingScreen(game.playScreen.worldUUID));
            }
        });

        table.add(waitingLabel).height(300).row();
        table.add(textLabel).height(50).row();
        table.add(backButton).height(150);

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

    }

    /**
     * Renders the screen
     *
     * @param delta The time in seconds since the last render.
     */
    public void render(float delta) {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        if (currentPlayers == 0) {
            currentPlayers = 1;
        }

        if (previousNumberOfPlayers > currentPlayers) {
            previousNumberOfPlayers = 1;
            updateWaitingScreen();
        }

        if (currentPlayers > previousNumberOfPlayers) {
            previousNumberOfPlayers++;
            updateWaitingScreen();
        }

        if (currentPlayers == maxPlayers) {
            game.setScreen(game.playScreen);
            music.dispose();

            previousNumberOfPlayers = 1;
            currentPlayers = 1;

            return;
        }

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
