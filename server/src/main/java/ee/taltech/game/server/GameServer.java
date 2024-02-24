package ee.taltech.game.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import ee.taltech.game.server.ObjectsToSend.BulletData;
import ee.taltech.game.server.ObjectsToSend.PlayerData;
import ee.taltech.game.server.ObjectsToSend.RobotData;
import ee.taltech.game.server.ObjectsToSend.RobotDataMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameServer {

    private Server server;
    private Map<Integer, Object> playerInstanceCoordinates = new HashMap<>();
    private RobotDataMap robotDataMap = new RobotDataMap();

    /**
     * Constructor for GameServer. Initializes the KryoNet server and binds it to the specified ports.
     */
    public GameServer() {
        server = new Server();

        Kryo kryo = server.getKryo();
        kryo.register(RobotData.class, 15);
        kryo.register(PlayerData.class);
        kryo.register(Integer.class);
        kryo.register(BulletData.class, 17);
        kryo.register(HashMap.class);
        kryo.register(RobotDataMap.class);
        kryo.register(String.class);

        server.start();
        try {
            server.bind(8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.addListener(new Listener() {
            /**
             * Handles received messages from clients. Updates player coordinates and broadcasts the updated state to all clients.
             *
             * @param connection The connection object representing the client connection.
             * @param object     The received object from the client.
             */
            public void received(Connection connection, Object object) {

                if (!(object instanceof FrameworkMessage.KeepAlive)) {
                    System.out.println("Server received: " + object);
                    if (object instanceof PlayerData) {
                        playerInstanceCoordinates.put(connection.getID(), object);
                        server.sendToAllTCP(playerInstanceCoordinates);
                    }
                    if (object instanceof BulletData) {
                        server.sendToAllTCP(object);
                    }
                    if (object instanceof RobotDataMap) {
                        HashMap<String, RobotData> map = ((RobotDataMap) object).getMap();
                        for (Map.Entry<String, RobotData> entry : map.entrySet()) {
                            robotDataMap.put(entry.getKey(), entry.getValue());
                        }
                        System.out.println(robotDataMap.getMap().size());
                        server.sendToAllTCP(robotDataMap);
                    }
                }
            }

            /**
             * Handles disconnection events. Removes the disconnected player's data from the server.
             *
             * @param connection The connection object representing the disconnected client.
             */
            @Override
            public void disconnected(Connection connection) {
                System.out.println("Player disconnected: " + connection.getID());
                playerInstanceCoordinates.remove(connection.getID());
                if (playerInstanceCoordinates.isEmpty()) {
                    robotDataMap.getMap().clear();
                    server.sendToAllTCP(robotDataMap);
                }
            }
        });
    }

    /**
     * The entry point of the server. Creates and starts an instance of the GameServer.
     *
     * @param args The command-line arguments (not used in this example).
     */
    public static void main(String[] args) {

        GameServer gameServer = new GameServer();

    }
}
