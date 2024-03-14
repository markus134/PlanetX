package Screens;

import Screens.ReusableElements.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

public class CreateSinglePlayerWorld extends ScreenAdapter {
    private Stage stage;
    private final SinglePlayerScreen singlePlayerScreen;
    private final MyGDXGame game;
    private Batch batch;
    private BackGround backGround;

    CreateSinglePlayerWorld(SinglePlayerScreen screen, MyGDXGame game) {
        this.singlePlayerScreen = screen;
        this.game = game;
    }

    @Override
    public void show() {
        Viewport viewport = new ExtendViewport(MyGDXGame.V_WIDTH, MyGDXGame.V_HEIGHT);
        stage = new Stage(viewport);

        backGround = new BackGround();
        batch = new SpriteBatch();

        Table table = new Table();
        table.setFillParent(true);

        // Create UI components
        Label.LabelStyle labelStyle = new LabelStyle(200).getLabelStyle();
        Label titleLabel = new Label("Create new World", labelStyle);

        Label.LabelStyle labelForTable = new LabelForTable(80).getLabelStyle();
        Label enterWorldNameLabel = new Label("Enter world name", labelForTable);

        TextField.TextFieldStyle textFieldStyle = new TextFieldStyleForInput(50).getStyle();
        ResizableTextField worldNameTextField = new ResizableTextField("", textFieldStyle, 500, 100);

        TextButton.TextButtonStyle textButtonStyle = new PurpleTextButtonStyle().getTextButtonStyle();
        TextButton backButton = new TextButton("Back", textButtonStyle);
        TextButton saveButton = new TextButton("Save", textButtonStyle);

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String worldName = worldNameTextField.getText();
                if (!SinglePlayerScreen.worlds.contains(worldName)) {
                    SinglePlayerScreen.worlds.add(worldName);
                    game.setScreen(singlePlayerScreen);
                } else {
                    enterWorldNameLabel.remove();

                    table.clear();

                    Label errorLabel1 = new Label("The world with such name already exists", labelForTable);
                    Label errorLabel2 = new Label("Enter another name", labelForTable);

                    table.add(titleLabel).expandX().center().colspan(2).padBottom(10).row();
                    table.add(errorLabel1).center().colspan(2).padTop(10).row();
                    table.add(errorLabel2).center().colspan(2).padTop(10);
                    table.row();
                    table.add(worldNameTextField).center().colspan(2).padTop(30);
                    table.row();
                    table.add(backButton).padTop(30);
                    table.add(saveButton).padTop(30);
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(singlePlayerScreen);
            }
        });

        // Add components to table
        table.add(titleLabel).expandX().center().colspan(2).padBottom(10).row();
        table.add(enterWorldNameLabel).center().colspan(2).padTop(10);
        table.row();
        table.add(worldNameTextField).center().colspan(2).padTop(30);
        table.row();
        table.add(backButton).padTop(30);
        table.add(saveButton).padTop(30);

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