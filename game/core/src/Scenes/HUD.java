package Scenes;

import Sprites.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HUD implements Disposable {
    public Stage stage;
    private Table debugTable;
    private Player player;
    private Image[] heartArray = new Image[5]; // For displaying health assets

    public HUD(SpriteBatch sb, Player player) {
        this.player = player;

        // Debug table generation
        stage = new Stage(new ScreenViewport(), sb);
        debugTable = new Table();
        debugTable.top().left();
        debugTable.setFillParent(true);

        // Add health assets to the table with padding
        for (int i = 0; i < heartArray.length; i++) {
            Texture heartTexture = new Texture(Gdx.files.internal("lives/heart_full.png"));
            heartArray[i] = new Image(new TextureRegion(heartTexture));
            debugTable.add(heartArray[i]).padLeft(10f).padTop(10f);
        }

        stage.addActor(debugTable);
    }

    public void updateLabelValues() {
        // Update health assets based on player's health
        for (int i = 0; i < heartArray.length; i++) {
            if (i < player.getHealth() / 20) {
                // Display full heart for each 20% health
                heartArray[i].setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("lives/heart_full.png"))));
            } else {
                // Display empty heart for remaining health
                heartArray[i].setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("lives/heart_empty.png"))));
            }
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}