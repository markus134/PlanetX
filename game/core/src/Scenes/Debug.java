package Scenes;

import Sprites.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Debug implements Disposable {
    public Stage stage; // it is used for debug table
    private Table debugTable;
    private Player player;
    private Label[] labelArray = new Label[4]; // the number corresponds to the amount of displayed variables
    private float mapCenterX = 7.52667f;
    private float mapCenterY = 8.15333f;

    public Debug(SpriteBatch sb, Player player) {
        this.player = player;
        //debug table generation
        stage = new Stage(new ScreenViewport(), sb);
        debugTable = new Table();
        debugTable.top().left();
        debugTable.setFillParent(true);

        Label debugLabel = new Label("Debug Info", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        debugLabel.setAlignment(Align.left);
        debugTable.add(debugLabel);

        // you should write the variables that you want to display here
        // and update the updateLabelValues() method
        String[][] arraysOfVariablesWithValues = {
                {"X", Float.toString(player.b2body.getPosition().x)},
                {"Y", Float.toString(player.b2body.getPosition().y)},
                {"xFromCenter", "0"},
                {"yFromCenter", "0"}
        };
        addDebugLabels(arraysOfVariablesWithValues);

        stage.addActor(debugTable);
    }

    /**
     * Adds debug labels to the debug table.
     *
     * @param listOfVariablesWithValues Array of variable names and their initial values.
     */
    private void addDebugLabels(String[][] listOfVariablesWithValues) {
        // debug table layout
        for (int i = 0; i < listOfVariablesWithValues.length; i++) {
            Label name = new Label(listOfVariablesWithValues[i][0], new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            Label value = new Label(listOfVariablesWithValues[i][1], new Label.LabelStyle(new BitmapFont(), Color.WHITE));

            debugTable.row();
            debugTable.add(name).padRight(10);
            debugTable.add(value).row();

            labelArray[i] = value;
        }
    }

    /**
     * Updates the values of the debug labels.
     */
    public void updateLabelValues() {
        labelArray[0].setText(Float.toString(player.b2body.getPosition().x));
        labelArray[1].setText(Float.toString(player.b2body.getPosition().y));
        labelArray[2].setText(Float.toString(player.b2body.getPosition().x - mapCenterX));
        labelArray[3].setText(Float.toString(player.b2body.getPosition().y - mapCenterY));

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
