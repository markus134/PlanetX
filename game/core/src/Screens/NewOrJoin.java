package Screens;

import Screens.ReusableElements.BackGround;
import Screens.ReusableElements.PurpleTextButtonStyle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

public class NewOrJoin extends ScreenAdapter {

    private Stage stage;
    private final MultiPlayerScreen multiPlayerScreen;
    private final MyGDXGame game;
    private Batch batch;
    private BackGround backGround;
    private CreateMultiPlayerWorld createMenu;
    private Join joinMenu;

    public NewOrJoin(MultiPlayerScreen screen, MyGDXGame game) {
        this.multiPlayerScreen = screen;
        this.game = game;
        this.createMenu = new CreateMultiPlayerWorld(screen, game);
        this.joinMenu = new Join(screen, game);
    }

    @Override
    public void show() {
        Viewport viewport = new ExtendViewport(MyGDXGame.V_WIDTH, MyGDXGame.V_HEIGHT);
        stage = new Stage(viewport);

        backGround = new BackGround();
        batch = new SpriteBatch();

        Table table = new Table();
        table.setFillParent(true);

        TextButton.TextButtonStyle textButtonStyle = new PurpleTextButtonStyle().getTextButtonStyle();
        TextButton newButton = new TextButton("New", textButtonStyle);
        TextButton joinButton = new TextButton("Join", textButtonStyle);
        TextButton backButton = new TextButton("Back", textButtonStyle);

        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(createMenu);
        }});

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(multiPlayerScreen);
            }
        });

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(joinMenu);
            }
        });

        table.add(newButton).height(300).row();
        table.add(joinButton).height(300).row();
        table.add(backButton).height(300).row();

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage); // Start taking input from the UI
    }

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
