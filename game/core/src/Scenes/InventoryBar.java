package Scenes;

import Items.Items;
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
    private static final String CRYSTAL_TEXTURE_PATH = "Items/crystal.png";
    private static final String BLASTER_TEXTURE_PATH = "Items/blaster.png";
    private static final String DRILL_TEXTURE_PATH = "Items/drill.png";
    private static final float OFFSET = 2f;
    private static final float SLOT_PADDING_BOTTOM = 30f;
    private final Texture slotTexture;
    public final Table hotbarTable;
    private final Image[] slotImages;
    private final Image[] itemImages;
    private Items[] items;
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
        items = new Items[SLOT_COUNT];

        initializeHotbar();

        // Set the default highlighted slot to index 0
        highlightedSlotIndex = 0;
        slotImages[highlightedSlotIndex].setDrawable(new TextureRegionDrawable(new Texture(SLOT_HIGHLIGHT_TEXTURE_PATH)));

    }

    /**
     * Initializes the hotbar table with slot and item images.
     */
    private void initializeHotbar() {
        hotbarTable.setSize(slotTexture.getWidth() * SLOT_COUNT, slotTexture.getHeight());

        Texture slotTexture = new Texture(SLOT_TEXTURE_PATH);

        for (int i = 0; i < SLOT_COUNT; i++) {
            slotImages[i] = new Image(slotTexture);
            itemImages[i] = new Image();

            float itemWidth = slotTexture.getWidth();
            float itemHeight = slotTexture.getHeight();

            itemImages[i].setSize(itemWidth, itemHeight);
            itemImages[i].setPosition(OFFSET, OFFSET);

            Group overlay = new Group();
            overlay.addActor(slotImages[i]);
            overlay.addActor(itemImages[i]);

            hotbarTable.add(overlay).size(itemWidth, itemHeight);

            // Set initial item to NONE
            items[i] = Items.NONE;
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
     * Retrieves the index of the currently highlighted slot in the inventory bar.
     *
     * @return The index of the highlighted slot.
     */
    public int getHighlightedSlotIndex() {
        return highlightedSlotIndex;
    }

    public void addItemToSlot(Items item, int slotIndex) {
        if (slotIndex >= 0 && slotIndex < SLOT_COUNT) {
            items[slotIndex] = item;
            Texture itemTexture = getItemTexture(item);
            itemImages[slotIndex].setDrawable(new TextureRegionDrawable(itemTexture));
        }
    }

    private Texture getItemTexture(Items item) {
        switch (item) {
            case BLASTER:
                return new Texture(BLASTER_TEXTURE_PATH);
            case DRILL:
                return new Texture(DRILL_TEXTURE_PATH);
            case CRYSTAL:
                return new Texture(CRYSTAL_TEXTURE_PATH);
            case NONE:
            default:
                return null; // Return null or a default texture for NONE case
        }
    }

    /**
     * Get highlighted item.
     * @return highlighted item
     */
    public Items getHighlightedItem() {
        return items[highlightedSlotIndex];
    }

    /**
     * Adds an item to the next available slot in the inventory bar.
     *
     * @param item The item to add.
     * @return True if the item was successfully added, false otherwise.
     */
    public boolean addItemToNextFreeSlot(Items item) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (items[i] == Items.NONE) {
                addItemToSlot(item, i);
                return true; // Item added successfully
            }
        }
        return false; // No free slot available
    }

    /**
     * Removes the item from the highlighted slot in the inventory bar.
     */
    public void removeHighlightedItem() {
        items[highlightedSlotIndex] = Items.NONE;
        itemImages[highlightedSlotIndex].setDrawable(null); // Remove the item image from the slot
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
     * Disposes of resources used by the inventory bar.
     */
    public void dispose() {
        slotTexture.dispose();
    }
}
