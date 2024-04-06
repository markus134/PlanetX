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

import java.io.IOException;

import static Screens.PlayScreen.allDestroyedPlayers;

public class DeathScene implements Disposable {

    public final Stage stage;
    private Dialog deathScene;
    private boolean toShow = false;

    /**
     * Constructor
     *
     * @param sb
     * @param playScreen
     */
    public DeathScene(SpriteBatch sb, PlayScreen playScreen, Player player) {
        this.stage = new Stage(new ExtendViewport((float) MyGDXGame.V_WIDTH / 2,
                (float) MyGDXGame.V_HEIGHT / 2), sb);

        Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        deathScene = new Dialog("", skin) {
            protected void result(Object object) {
                if (object.equals(true)) {
                    try {
                        allDestroyedPlayers.add(player.getUuid());
                        playScreen.goToMenuWhenPlayerIsDead();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                toShow = false;
            }
        };
        deathScene.text("Game Over!");
        deathScene.button("Go to menu", true);
        deathScene.center();
        deathScene.setMovable(false);
    }

    /**
     * Show the popUp menu
     */
    public void showStage() {
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
