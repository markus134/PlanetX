package wfc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Load rules for each tile from file.
 * <p>
 * Loads Tile rules, neighbors, images and weights into tile objects.
 * </p>
 * <p>
 * NOTE: Currently creates tiles and associates them with images manually.
 */
class TileSetLoader {
    public static LinkedHashMap<Integer, Tile> tileMap = new LinkedHashMap<>();

    /**
     * Load Tile data.
     * <p>
     * TODO: Take a file as input.
     */
    public TileSetLoader() {
        tileMap.put(0, createEmpty());
        tileMap.put(1, createFull());
        tileMap.put(2, createUp());
        tileMap.put(3, createRight());
        tileMap.put(4, createDown());
        tileMap.put(5, createLeft());
        tileMap.put(6, createUL());
        tileMap.put(7, createUR());
        tileMap.put(8, createLR());
        tileMap.put(9, createLL());
    }

    /**
     * The rest of the methods in this file create
     * a relationship between each side and its allowed neighbors.
     * <p>
     * Tile relationships are added clockwise from 0. (up, right, down, left).
     * <p>
     * Tile ID codes for now.
     * 0 - empty
     * 1 - full
     * 2 - up
     * 3 - right
     * 4 - down
     * 5 - left
     * 6 - upper left corner
     * 7 - upper right corner
     * 8 - lower right corner
     * 9 - lower left corner
     * <p>
     * TODO: These methods will be deprecated when file i/o is added.
     */
    private Tile createEmpty() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(0, 2, 6, 7)));
        map.put(1, new HashSet<>(Arrays.asList(0, 3, 7, 8)));
        map.put(2, new HashSet<>(Arrays.asList(0, 4, 8, 9)));
        map.put(3, new HashSet<>(Arrays.asList(0, 5, 7, 8)));

        return new Tile(map, "wfc_test_images/empty.png", 5);
    }

    private Tile createFull() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(1, 4)));
        map.put(1, new HashSet<>(Arrays.asList(1, 5)));
        map.put(2, new HashSet<>(Arrays.asList(1, 2)));
        map.put(3, new HashSet<>(Arrays.asList(1, 3)));

        return new Tile(map, "wfc_test_images/full.png", 1);
    }

    private Tile createUp() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(1, 4)));
        map.put(1, new HashSet<>(Arrays.asList(2, 6)));
        map.put(2, new HashSet<>(Arrays.asList(0, 4, 8, 9)));
        map.put(3, new HashSet<>(Arrays.asList(2, 7)));

        return new Tile(map, "wfc_test_images/up.png", 4);
    }

    private Tile createRight() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(3, 8)));
        map.put(1, new HashSet<>(Arrays.asList(1, 5)));
        map.put(2, new HashSet<>(Arrays.asList(3, 7)));
        map.put(3, new HashSet<>(Arrays.asList(0, 5, 6, 9)));

        return new Tile(map, "wfc_test_images/right.png", 4);
    }

    private Tile createDown() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(0, 2, 8, 9)));
        map.put(1, new HashSet<>(Arrays.asList(4, 9)));
        map.put(2, new HashSet<>(Arrays.asList(1, 2)));
        map.put(3, new HashSet<>(Arrays.asList(4, 8)));

        return new Tile(map, "wfc_test_images/down.png", 4);
    }

    private Tile createLeft() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(5, 9)));
        map.put(1, new HashSet<>(Arrays.asList(0, 3, 7, 8)));
        map.put(2, new HashSet<>(Arrays.asList(5, 6)));
        map.put(3, new HashSet<>(Arrays.asList(1, 3)));

        return new Tile(map, "wfc_test_images/left.png", 4);
    }

    private Tile createUL() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(5, 9)));
        map.put(1, new HashSet<>(Arrays.asList(3, 7, 8)));
        map.put(2, new HashSet<>(Arrays.asList(4, 8, 9)));
        map.put(3, new HashSet<>(Arrays.asList(2, 7)));

        return new Tile(map, "wfc_test_images/upper_left.png", 2);
    }

    private Tile createUR() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(3, 8)));
        map.put(1, new HashSet<>(Arrays.asList(2, 6)));
        map.put(2, new HashSet<>(Arrays.asList(4, 8, 9)));
        map.put(3, new HashSet<>(Arrays.asList(5, 6, 9)));

        return new Tile(map, "wfc_test_images/upper_right.png", 2);
    }

    private Tile createLR() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(2, 6, 7)));
        map.put(1, new HashSet<>(Arrays.asList(4, 9)));
        map.put(2, new HashSet<>(Arrays.asList(3, 7)));
        map.put(3, new HashSet<>(Arrays.asList(5, 6, 9)));

        return new Tile(map, "wfc_test_images/lower_right.png", 2);
    }

    private Tile createLL() {
        HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        map.put(0, new HashSet<>(Arrays.asList(2, 6, 7)));
        map.put(1, new HashSet<>(Arrays.asList(3, 7, 8)));
        map.put(2, new HashSet<>(Arrays.asList(5, 6)));
        map.put(3, new HashSet<>(Arrays.asList(4, 8)));

        return new Tile(map, "wfc_test_images/lower_left.png", 2);
    }
}
