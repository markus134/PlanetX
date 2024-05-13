package Screens;

import Screens.ReusableElements.BackGround;
import Screens.ReusableElements.LabelForTable;
import Screens.ReusableElements.LabelStyle;
import Screens.ReusableElements.PurpleTextButtonStyle;
import Screens.ReusableElements.ResizableTextField;
import Screens.ReusableElements.TextFieldStyleForInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

import java.util.UUID;

public class CreateMultiPlayerWorld extends ScreenAdapter {
    private Stage stage;
    private final MultiPlayerScreen multiPlayerScreen;
    private final MyGDXGame game;
    private Batch batch;
    private BackGround backGround;

    /**
     * Constructor
     *
     * @param screen
     * @param game
     */
    public CreateMultiPlayerWorld(MultiPlayerScreen screen, MyGDXGame game) {
        this.multiPlayerScreen = screen;
        this.game = game;
    }

    /**
     * This is what is shown on the screen
     */
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

        TextField.TextFieldStyle textFieldStyle = new TextFieldStyleForInput(60).getStyle();
        ResizableTextField worldNameTextField = new ResizableTextField("", textFieldStyle, 500, 100);

        Label enterNumberOfPlayersLabel = new Label("Enter number of players", labelForTable);
        ResizableTextField numberOfPlayersTextField = new ResizableTextField("", textFieldStyle, 200, 100);

        TextButton.TextButtonStyle textButtonStyle = new PurpleTextButtonStyle().getTextButtonStyle();
        TextButton backButton = new TextButton("Back", textButtonStyle);
        TextButton saveButton = new TextButton("Save", textButtonStyle);

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String worldName = worldNameTextField.getText();
                String numberOfPlayers = numberOfPlayersTextField.getText();
                table.clear();
                if (!MultiPlayerScreen.multiPlayerWorlds.containsKey(worldName) && !worldName.isBlank() &&
                        !numberOfPlayers.isBlank() && isConvertibleToInt(numberOfPlayers)) {

                    MultiPlayerScreen.multiPlayerWorlds.put(worldName, UUID.randomUUID() + ":" + numberOfPlayers);
                    Label titleLabel2 = new Label("Info", labelStyle);

                    Label worldCodeLabel1 = new Label("We have generated a unique code for this world", labelForTable);
                    Label worldCodeLabel2 = new Label("Others can use it to join you", labelForTable);
                    Label worldCodeLabel3 = new Label("It will be automatically copied to your ClipBoard", labelForTable);
                    Label worldCodeLabel4 = new Label("When you press the 'OK' button below", labelForTable);

                    TextButton okButton = new TextButton("OK", textButtonStyle);

                    okButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event1, float x, float y) {
                            Gdx.app.getClipboard().setContents(MultiPlayerScreen.multiPlayerWorlds.get(worldName));
                            game.setScreen(multiPlayerScreen);
                        }
                    });

                    table.add(titleLabel2).expandX().center().row();
                    table.add(worldCodeLabel1).row();
                    table.add(worldCodeLabel2).row();
                    table.add(worldCodeLabel3).row();
                    table.add(worldCodeLabel4).row();
                    table.add(okButton);
                } else {

                    Label errorLabel1 = new Label("ERROR", labelForTable);
                    Label errorLabel2 = new Label("Enter world name", labelForTable);

                    table.add(titleLabel).expandX().center().colspan(2).padBottom(10).row();
                    table.add(errorLabel1).center().colspan(2).padTop(10).row();
                    table.add(errorLabel2).center().colspan(2).padTop(10);
                    table.row();
                    table.add(worldNameTextField).center().colspan(2).padTop(30);
                    table.row();
                    table.add(enterNumberOfPlayersLabel).center().colspan(2).row();
                    table.add(numberOfPlayersTextField).center().colspan(2).row();
                    table.add(backButton).padTop(30);
                    table.add(saveButton).padTop(30);
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(multiPlayerScreen);
            }
        });

        // Add components to table
        table.add(titleLabel).expandX().center().colspan(2).padBottom(10).row();
        table.add(enterWorldNameLabel).center().colspan(2).padTop(10);
        table.row();
        table.add(worldNameTextField).center().colspan(2).padTop(30);
        table.row();
        table.add(enterNumberOfPlayersLabel).center().colspan(2).row();
        table.add(numberOfPlayersTextField).center().colspan(2).row();
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

    /**
     * Check if the given string is convertible to integer and bigger than 1
     *
     * @param str to check
     * @return boolean
     */
    private boolean isConvertibleToInt(String str) {
        try {
            int a = Integer.parseInt(str);
            return a > 1;
        } catch (Exception e) {
            return false;
        }
    }
}
