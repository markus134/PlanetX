package Scenes;

import Screens.MenuScreen;
import Screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.MyGDXGame;

import java.io.IOException;

public class ExitToMainMenu implements Disposable {

    public final Stage stage;
    private final MenuScreen menuScreen;
    private Dialog pauseDialog;
    private final SpriteBatch sb;
    private boolean toShow = false;
    private final PlayScreen playScreen;

    /**
     * Constructor
     *
     * @param sb
     * @param menuScreen
     * @param playScreen
     */
    public ExitToMainMenu(SpriteBatch sb, MenuScreen menuScreen, PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.sb = sb;
        this.stage = new Stage(new ExtendViewport((float) MyGDXGame.V_WIDTH / 2, (float) MyGDXGame.V_HEIGHT / 2), sb);
        this.menuScreen = menuScreen;

        Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        pauseDialog = new Dialog("Pause", skin) {
            protected void result(Object object) {
                if (object.equals(true)) {
                    System.out.println("Exiting to main menu...");
                    try {
                        playScreen.goToMenuWhenPlayerIsDead();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Resuming game...");
                    playScreen.changeInputToHandler();
                }
                toShow = !toShow;
            }
        };
        pauseDialog.text("Do you want to exit to the main menu?");
        pauseDialog.button("Yes", true);
        pauseDialog.button("No", false);
        pauseDialog.key(Input.Keys.ESCAPE, false);
        pauseDialog.center();
    }

    /**
     * Show the popUp menu
     */
    public void showStage() {
        if (!pauseDialog.hasParent()) {
            stage.addActor(pauseDialog);
        }
        pauseDialog.show(stage);
        toShow = !toShow;
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
