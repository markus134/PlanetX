package Screens;

import Screens.ReusableElements.BackGround;
import Screens.ReusableElements.LabelStyle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

public class WaitingScreen extends ScreenAdapter {

    private Stage stage;
    private final MyGDXGame game;
    private Batch batch;
    private BackGround backGround;
    public int currentPlayers;
    public int maxPlayers;
    private final Music music;

    /**
     * Constructor
     *
     * @param game
     */
    public WaitingScreen(MyGDXGame game, Music music) {
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

        updateWaitingScreen();
    }

    public void updateWaitingScreen() {
        System.out.println("Updating screen");
        System.out.println(currentPlayers);
        System.out.println(maxPlayers);


        Table table = new Table();
        table.setFillParent(true);

        Label.LabelStyle waitingLabelStyle = new LabelStyle(200).getLabelStyle();
        Label waitingLabel = new Label("Waiting...", waitingLabelStyle);

        table.add(waitingLabel).row();


        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

    }

    public void render(float delta) {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        if (currentPlayers == maxPlayers) {
            game.setScreen(game.playScreen);
            music.dispose();

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
