package ee.taltech.game.server;

import ee.taltech.game.server.ObjectsToSend.BulletData;
import ee.taltech.game.server.ObjectsToSend.PlayerData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GameServer {

    private Server server;
    private Map<Integer, Object> playerInstanceCoordinates = new HashMap<>();

    /**
     * Constructor for GameServer. Initializes the KryoNet server and binds it to the specified ports.
     */
    public GameServer() {
        server = new Server();

        Kryo kryo = server.getKryo();
        kryo.register(PlayerData.class);
        kryo.register(BulletData.class, 17);
        kryo.register(HashMap.class);

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
             * @param object The received object from the client.
             */
            public void received(Connection connection, Object object) {

                if (!(object instanceof FrameworkMessage.KeepAlive)) {
                    System.out.println("Server received: " + object);


                    if (object instanceof PlayerData){
                        playerInstanceCoordinates.put(connection.getID(), object);
                        server.sendToAllTCP(playerInstanceCoordinates);
                    }
                    if (object instanceof BulletData) {
                        server.sendToAllTCP(object);
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
