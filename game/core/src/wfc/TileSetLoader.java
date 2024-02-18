package wfc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Load tileset from file.
 * <p>
 * Responsible for loading valid Tiles and Images into a {@link java.util.HashMap} object.
 * Does not check if the tiles and images are valid.
 * </p>
 *
 * NOTE: Currently creates tiles and associates them with images manually. This is ugly :(
 */
public class TileSetLoader {
    private HashMap<int[][], String> optionMap = new HashMap<>();
    private ArrayList<int[][]> options;

    /**
     * Create {@link ArrayList} of options to send to {@link Cell}.
     * Create {@link HashMap} with option and its associated image.
     */
    public TileSetLoader() {
        options = new ArrayList<>(Arrays.asList(
                TILE_EMPTY, TILE_FULL,
                TILE_UP, TILE_RIGHT, TILE_DOWN, TILE_LEFT,
                TILE_CORNER_UL, TILE_CORNER_UR, TILE_CORNER_LR, TILE_CORNER_LL
        ));

        final String dir = "wfc_test_images/";

        optionMap.put(TILE_EMPTY, dir + "empty.png");
        optionMap.put(TILE_FULL, dir + "full.png");
        optionMap.put(TILE_UP, dir + "up.png");
        optionMap.put(TILE_RIGHT, dir + "right.png");
        optionMap.put(TILE_DOWN, dir + "down.png");
        optionMap.put(TILE_LEFT, dir + "left.png");
        optionMap.put(TILE_CORNER_UL, dir + "upper_left.png");
        optionMap.put(TILE_CORNER_UR, dir + "upper_right.png");
        optionMap.put(TILE_CORNER_LR, dir + "lower_right.png");
        optionMap.put(TILE_CORNER_LL, dir + "lower_left.png");
    }

    public HashMap<int[][], String> getOptionMap() {
        return optionMap;
    }

    public ArrayList<int[][]> getOptions() {
        return options;
    }

    private final int[][] TILE_EMPTY = new int[][]{
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
    };

    private final int[][] TILE_UP = new int[][]{
            {1, 1, 1},
            {1, 0, 0},
            {0, 0, 0},
            {0, 0, 1}
    };

    private final int[][] TILE_RIGHT = new int[][]{
            {0, 0, 1},
            {1, 1, 1},
            {1, 0, 0},
            {0, 0, 0}
    };

    private final int[][] TILE_DOWN = new int[][]{
            {0, 0, 0},
            {0, 0, 1},
            {1, 1, 1},
            {1, 0, 0}
    };

    private final int[][] TILE_LEFT = new int[][]{
            {1, 0, 0},
            {0, 0, 0},
            {0, 0, 1},
            {1, 1, 1}
    };

    private final int[][] TILE_FULL = new int[][]{
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
    };

    private final int[][] TILE_CORNER_UL = new int[][]{
            {1, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 1}
    };

    private final int[][] TILE_CORNER_UR = new int[][]{
            {0, 0, 1},
            {1, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
    };

    private final int[][] TILE_CORNER_LR = new int[][]{
            {0, 0, 0},
            {0, 0, 1},
            {1, 0, 0},
            {0, 0, 0}
    };

    private final int[][] TILE_CORNER_LL = new int[][]{
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 1},
            {1, 0, 0}
    };
}
