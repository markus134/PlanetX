package Opponents.astar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class TileMapReader {
    private static int[][] collisions;
    /**
     * Read tile map data from level/map.tmx and make a map from it.
     * 1 and 0 represent respectively the impassable and passable tiles.
     */
    public static void readTileMapData() {
        try {
            File file = new File("level/map.tmx");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            int mapWidth = Integer.parseInt(doc.getDocumentElement().getAttribute("width"));
            int mapHeight = Integer.parseInt(doc.getDocumentElement().getAttribute("height"));

            collisions = new int[mapHeight][mapWidth];

            NodeList collisionList = doc.getElementsByTagName("object");
            for (int i = 0; i < collisionList.getLength(); i++) {
                Element collision = (Element) collisionList.item(i);
                if (collision.getParentNode().getAttributes().getNamedItem("name").getNodeValue().equals("collisions")) {
                    int x = (int) Math.floor(Double.parseDouble(collision.getAttribute("x")));
                    int y = (int) Math.floor(Double.parseDouble(collision.getAttribute("y")));

                    collisions[y / 32][x / 32] = 1;

                    // Add collisions around the current collision block including a buffer of two blocks
                    for (int dy = -2; dy <= 2; dy++) {
                        for (int dx = -2; dx <= 2; dx++) {
                            int newX = x / 32 + dx;
                            int newY = y / 32 + dy;
                            if (newX >= 0 && newX < mapWidth && newY >= 0 && newY < mapHeight) {
                                if (collisions[newY][newX] != 1) {
                                    collisions[newY][newX] = 1; // or any other value to represent surrounding collisions
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper for printing the array.
     */
    public static void printCollisionArray(int[][] collisions) {
        for (int[] row : collisions) {
            for (int value : row) {
                if (value == 1) {
                    System.out.print("# ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Getter for collisions.
     *
     * @return collisions
     */
    public static int[][] getCollisions() {
        return collisions;
    }

}
