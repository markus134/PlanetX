package Scenes;

import Screens.PlayScreen;
import Sprites.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.MyGDXGame;


public class GameOverScene implements Disposable {

    public final Stage stage;
    private final Dialog deathScene;
    private boolean toShow = false;

    /**
     * Constructor
     *
     * @param sb
     * @param playScreen
     */
    public GameOverScene(SpriteBatch sb, PlayScreen playScreen, Player player) {
        this.stage = new Stage(new ExtendViewport((float) MyGDXGame.V_WIDTH / 2,
                (float) MyGDXGame.V_HEIGHT / 2), sb);

        Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        deathScene = new Dialog("", skin) {
            protected void result(Object object) {
                if (object.equals(true)) {
                    playScreen.allDestroyedPlayers.add(player.getUuid());
                    playScreen.goToMenu();
                }
                toShow = false;
            }
        };
        deathScene.text("Game Over!");
        deathScene.button("Go to menu", true);
        centerScene();
        deathScene.setMovable(false);
    }

    /**
     * Show the popUp menu
     */
    public void showStage() {
        System.out.println("showing");
        if (!deathScene.hasParent()) {
            stage.addActor(deathScene);
        }
        deathScene.show(stage);
        toShow = true;
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Checks if it is the time to show the menu
     *
     * @return
     */
    public boolean isToShow() {
        return toShow;
    }

    /**
     * Center the scene. Made a separate method instead of using just doing pauseDialog.center() to avoid bugs
     * with resizing
     */
    public void centerScene() {
        float stageWidth = stage.getWidth();
        float stageHeight = stage.getHeight();

        float dialogWidth = deathScene.getWidth();
        float dialogHeight = deathScene.getHeight();

        // Center the dialog based on the stage dimensions
        float posX = (stageWidth - dialogWidth) / 2;
        float posY = (stageHeight - dialogHeight) / 2;

        deathScene.setPosition(posX, posY);
    }

    public void addText(String text) {
        deathScene.text(text);
    }

    /**
     * Renders the stage
     */
    public void renderStage() {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    /**
     * Dispose of the stage
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
