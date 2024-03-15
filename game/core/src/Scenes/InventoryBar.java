package Scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Represents the inventory bar in the game.
 * It consists of a set of slots, each containing an item image.
 */
public class InventoryBar extends Actor {
    private static final int SLOT_COUNT = 8;
    private static final String SLOT_TEXTURE_PATH = "hotbar/slot.jpg";
    private static final String SLOT_HIGHLIGHT_TEXTURE_PATH = "hotbar/slot_highlight.jpg";
    private static final String ITEM_TEXTURE_PATH = "items/crystal.png";
    private static final float OFFSET = 2f;
    private static final float SLOT_PADDING_BOTTOM = 30f;
    private final Texture slotTexture;
    public final Table hotbarTable;
    private final Image[] slotImages;
    private final Image[] itemImages;
    private int highlightedSlotIndex;

    /**
     * Constructor for the InventoryBar class.
     * Initializes the inventory bar with default values.
     */
    public InventoryBar() {
        hotbarTable = new Table();
        slotTexture = new Texture(SLOT_TEXTURE_PATH);
        slotImages = new Image[SLOT_COUNT];
        itemImages = new Image[SLOT_COUNT];

        initializeHotbar();

        // Set the default highlighted slot to index 0
        highlightedSlotIndex = 0;
        slotImages[highlightedSlotIndex].setDrawable(new TextureRegionDrawable(new Texture(SLOT_HIGHLIGHT_TEXTURE_PATH)));

    }

    /**
     * Initializes the hotbar table with slot and item images.
     */
    private void initializeHotbar() {
        hotbarTable.setSize(slotTexture.getWidth() * 8, slotTexture.getHeight());

        Texture slotTexture = new Texture(SLOT_TEXTURE_PATH);
        Texture itemTexture = new Texture(ITEM_TEXTURE_PATH);

        for (int i = 0; i < SLOT_COUNT; i++) {
            slotImages[i] = new Image(slotTexture);
            itemImages[i] = new Image(itemTexture);

            float itemWidth = slotTexture.getWidth();
            float itemHeight = slotTexture.getHeight();

            itemImages[i].setSize(itemWidth, itemHeight);
            itemImages[i].setPosition(OFFSET, OFFSET); // We put the items at an offset, so it would be inside the slot properly

            // Overlay the images and add them to the table
            Group overlay = new Group();
            overlay.addActor(slotImages[i]);
            overlay.addActor(itemImages[i]);

            hotbarTable.add(overlay).size(itemWidth, itemHeight);
        }

        hotbarTable.padBottom(SLOT_PADDING_BOTTOM);
    }

    /**
     * Switches the highlighted slot in the inventory bar.
     *
     * @param newIndex The index of the slot to highlight.
     */
    public void switchHighlightedSlot(int newIndex) {
        if (newIndex >= 0 && newIndex < SLOT_COUNT && newIndex != highlightedSlotIndex) {
            slotImages[highlightedSlotIndex].setDrawable(new TextureRegionDrawable(new Texture(SLOT_TEXTURE_PATH)));
            slotImages[newIndex].setDrawable(new TextureRegionDrawable(new Texture(SLOT_HIGHLIGHT_TEXTURE_PATH)));
            highlightedSlotIndex = newIndex;
        }
    }

    /**
     * Draws the inventory bar on the screen.
     *
     * @param batch The batch to draw with.
     * @param parentAlpha The parent alpha value.
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        hotbarTable.draw(batch, parentAlpha);
    }

    /**
     * Performs actor logic for the inventory bar.
     *
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        hotbarTable.act(delta);
    }

    /**
     * Retrieves the index of the currently highlighted slot in the inventory bar.
     *
     * @return The index of the highlighted slot.
     */
    public int getHighlightedSlotIndex() {
        return highlightedSlotIndex;
    }

    /**
     * Disposes of resources used by the inventory bar.
     */
    public void dispose() {
        slotTexture.dispose();
    }
}
