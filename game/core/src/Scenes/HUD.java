package Scenes;

import Items.Items;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.MyGDXGame;

/**
 * HUD class represents the Heads-Up Display in the game.
 * It manages the player's health display and the inventory bar.
 */
public class HUD implements Disposable {
    private static final String HEART_FULL_TEXTURE_PATH = "lives/heart_full.png";
    private static final String HEART_EMPTY_TEXTURE_PATH = "lives/heart_empty.png";
    private static final float HEART_PADDING_LEFT = 10f;
    private static final float HEART_PADDING_TOP = 10f;
    public Stage stage;
    private Table heartsTable;
    private Player player;
    private Image[] heartArray = new Image[5]; // For displaying health assets
    private InventoryBar inventoryBar;

    /**
     * Constructor for HUD class.
     * Initializes the HUD elements such as the stage, inventory bar, and health display.
     *
     * @param sb     The SpriteBatch to render the HUD.
     * @param player The player object to monitor for health changes.
     */
    public HUD(SpriteBatch sb, Player player) {
        this.player = player;

        stage = new Stage(new ExtendViewport(MyGDXGame.V_WIDTH, MyGDXGame.V_HEIGHT), sb);

        initializeInventoryBar();
        initializeHeartsTable();

        updateLabelValues();
    }

    /**
     * Initializes the inventory bar and adds it to the stage.
     * The inventory bar is positioned at the bottom center of the stage.
     */
    private void initializeInventoryBar() {
        inventoryBar = new InventoryBar();
        float inventoryBarX = (stage.getWidth() - inventoryBar.hotbarTable.getWidth()) / 2f;
        float inventoryBarY = 0; // Assuming you want it at the bottom
        inventoryBar.hotbarTable.setPosition(inventoryBarX, inventoryBarY);

        inventoryBar.addItemToSlot(Items.BLASTER, 0);
        inventoryBar.addItemToSlot(Items.DRILL, 1);
        stage.addActor(inventoryBar);
    }

    /**
     * Initializes the health display and adds it to the stage.
     * The health display is positioned at the top left of the stage.
     */
    private void initializeHeartsTable() {
        heartsTable = new Table();
        heartsTable.top().left();
        heartsTable.setFillParent(true);

        for (int i = 0; i < heartArray.length; i++) {
            Texture heartTexture = new Texture(Gdx.files.internal(HEART_FULL_TEXTURE_PATH));
            heartArray[i] = new Image(new TextureRegion(heartTexture));
            heartsTable.add(heartArray[i]).padLeft(HEART_PADDING_LEFT).padTop(HEART_PADDING_TOP);
        }

        stage.addActor(heartsTable);
    }

    /**
     * Updates the health display and inventory bar position.
     * This method should be called each frame to ensure proper rendering.
     */
    public void updateLabelValues() {
        updateHealthAssets();
        updateInventoryBarPosition();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    /**
     * Updates the health display based on the player's current health.
     * Each heart represents 20% of the player's health.
     */
    private void updateHealthAssets() {
        for (int i = 0; i < heartArray.length; i++) {
            String texturePath = (i < player.getHealth() / 20) ? HEART_FULL_TEXTURE_PATH : HEART_EMPTY_TEXTURE_PATH;
            Texture heartTexture = new Texture(Gdx.files.internal(texturePath));
            heartArray[i].setDrawable(new TextureRegionDrawable(new TextureRegion(heartTexture)));
        }
    }

    /**
     * Updates the position of the inventory bar to the bottom center of the stage.
     */
    private void updateInventoryBarPosition() {
        float inventoryBarX = (stage.getWidth() - inventoryBar.hotbarTable.getWidth()) / 2f;
        float inventoryBarY = 0;
        inventoryBar.hotbarTable.setPosition(inventoryBarX, inventoryBarY);
    }

    /**
     * Switches the highlighted slot in the inventory bar.
     *
     * @param index The index of the slot to highlight.
     */
    public void switchHighlightedSlot(int index) {
        inventoryBar.switchHighlightedSlot(index);
    }

    /**
     * Retrieves the index of the currently highlighted slot in the inventory bar.
     *
     * @return The index of the highlighted slot.
     */
    public int getHighlightedSlotIndex() {
        return inventoryBar.getHighlightedSlotIndex();
    }

    /**
     * Get highlighted item.
     * @return highlighted item
     */
    public Items getHighlightedItem() {
        return inventoryBar.getHighlightedItem();
    }

    /**
     * Add item to next free slot.
     * @param item
     * @return whether adding was successful
     */
    public boolean addItemToNextFreeSlot(Items item) {
        return inventoryBar.addItemToNextFreeSlot(item);
    }

    /**
     * Remove highlighted item.
     */
    public void removeHighlightedItem() {
        inventoryBar.removeHighlightedItem();
    }

    /**
     * Disposes of resources used by the HUD.
     * This method should be called when the HUD is no longer needed.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
