package Opponents.astar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Arrays;

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

                    // Added the offset 32 because it helped to prevent mobs getting stuck
                    int x = (int) Math.floor(Double.parseDouble(collision.getAttribute("x"))) - 32;
                    int y = (int) Math.floor(Double.parseDouble(collision.getAttribute("y"))) + 32;

                    collisions[y / 32][x / 32] = 1;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for collisions.
     *
     * @return collisions
     */
    public static int[][] getCollisions() {
        return Arrays.copyOf(collisions, collisions.length);
    }

}
