package ee.taltech.game.server;

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

    public GameServer() {
        server = new Server();
        server.start();
        try {
            server.bind(8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {

                if (!(object instanceof FrameworkMessage.KeepAlive)) {
                    System.out.println("Server received: " + object);
                    playerInstanceCoordinates.put(connection.getID(), object);

                    server.sendToAllTCP(
                            playerInstanceCoordinates.entrySet().stream()
                                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                                    .collect(Collectors.joining("/"))
                    );
                }
            }
        });
    }

    public static void main(String[] args) {

        GameServer gameServer = new GameServer();

    }
}
