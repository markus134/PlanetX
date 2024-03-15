package Screens;

import Screens.ReusableElements.BackGround;
import Screens.ReusableElements.LabelForTable;
import Screens.ReusableElements.LabelStyle;
import Screens.ReusableElements.PurpleTextButtonStyle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SinglePlayerScreen extends ScreenAdapter {

    private final MenuScreen menuScreen;
    private final MyGDXGame game;
    private Stage stage;
    private Batch batch;
    private Music music;
    private BackGround backGround;
    public static Map<String, String> singlePlayerWorlds = new HashMap<>();
    private CreateSinglePlayerWorld createMenu;
    private Label.LabelStyle labelForTable = new LabelForTable(60).getLabelStyle();
    private TextButton.TextButtonStyle textButtonStyle = new PurpleTextButtonStyle().getTextButtonStyle();
    private Table displayTable;
    private TextButton newButton;
    private Table table;
    private List<Container> containers = new ArrayList<>();
    private String chosenWorld;
    private Pixmap pixmapNormal;
    private Pixmap pixmapClicked;
    private Drawable drawableNormal;
    private Drawable drawableClicked;

    SinglePlayerScreen(MenuScreen menuScreen, MyGDXGame game, Music music) {
        this.menuScreen = menuScreen;
        this.game = game;
        this.music = music;
        this.createMenu = new CreateSinglePlayerWorld(this, game);
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

        table = new Table();
        table.setFillParent(true);
        updateTable();
        updateDisplayTable();

        pixmapNormal = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapNormal.setColor(Color.CLEAR);
        pixmapNormal.fill();
        drawableNormal = new TextureRegionDrawable(new TextureRegion(new Texture(pixmapNormal)));

        pixmapClicked = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapClicked.setColor(Color.PURPLE);
        pixmapClicked.fill();
        drawableClicked = new TextureRegionDrawable(new TextureRegion(new Texture(pixmapClicked)));

        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(createMenu);
            }
        });

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    public void updateDisplayTable() {
        if (singlePlayerWorlds.size() == 5) newButton.remove();
        displayTable.clear();
        if (singlePlayerWorlds.isEmpty()){
            Label emptyWorldsLabel = new Label("You have not created any worlds yet", labelForTable);
            Label emptyWorldsLabel2 = new Label("Press the 'Create New' button", labelForTable);
            displayTable.add(emptyWorldsLabel).row();
            displayTable.add(emptyWorldsLabel2);
        } else {
            for (Map.Entry<String, String> entry: singlePlayerWorlds.entrySet()) {
                String worldName = entry.getKey();
                Label label = new Label(worldName, labelForTable);
                TextButton removeButton = new TextButton("remove", textButtonStyle);
                removeButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        singlePlayerWorlds.remove(worldName);
                        updateDisplayTable();
                    }
                });

                Container<Table> container = new Container<Table>();
                containers.add(container);

                Table rowTable = new Table();
                rowTable.add(label).width(600).padRight(280).padLeft(100);
                rowTable.add(removeButton).width(400);
                rowTable.row();
                container.setActor(rowTable);
                container.setBackground(drawableNormal);

                container.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        container.setBackground(drawableClicked);
                        for (Container container1 : containers) {
                            if (!container1.equals(container)) {
                                container1.setBackground(drawableNormal);
                            }
                        }
                        chosenWorld = ((Label) container.getActor().getChildren().get(0)).getText().toString();
                        System.out.println(chosenWorld);
                        System.out.println(singlePlayerWorlds.get(chosenWorld));
                    }
                });

                displayTable.add(container).fillX();
                displayTable.row();
            }
        }
    }

    public void updateTable() {
        // from the ReusableElements directory
        Label.LabelStyle labelStyle = new LabelStyle(200).getLabelStyle();
        Label titleLabel = new Label("SinglePlayer", labelStyle);

        newButton = new TextButton("Create New", textButtonStyle);
        TextButton backButton = new TextButton("Back", textButtonStyle);
        TextButton connectButton = new TextButton("Connect", textButtonStyle);

        displayTable = new Table();
        Label.LabelStyle labelForTable = new LabelForTable(60).getLabelStyle();
        Label label1 = new Label("World name", labelForTable);
        Label label2 = new Label("Options", labelForTable);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(menuScreen);
            }
        });

        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.createScreenAndClient(singlePlayerWorlds.get(chosenWorld), 1);
                game.setScreen(MyGDXGame.playScreen);
                music.dispose();
            }
        });

        table.add(titleLabel).expandX().center().colspan(2).row();
        table.add();
        table.add(newButton).height(100).row();
        table.add(label1);
        table.add(label2).row();
        table.add(displayTable).height(500).center().colspan(2);
        table.row();
        table.add(backButton).bottom();
        table.add(connectButton);
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
        pixmapNormal.dispose();
        pixmapClicked.dispose();
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
