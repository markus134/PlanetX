package main.java.ee.taltech.game.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import main.java.ee.taltech.game.session.Session;
import serializableObjects.AddSinglePlayerWorld;
import serializableObjects.BulletData;
import serializableObjects.PlayerData;
import serializableObjects.RobotData;
import serializableObjects.RobotDataMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServer {

    private Server server;
    private Map<String, Session> worlds = new HashMap<>();
    private Map<String, Map<Integer, Object>> playerDatas = new HashMap<>();
    private Map<String, RobotDataMap> robotDatas = new HashMap<>();

    /**
     * Constructor for GameServer. Initializes the KryoNet server and binds it to the specified ports.
     */
    public GameServer() {
        server = new Server(1000000, 1000000);

        //registering classes
        Kryo kryo = server.getKryo();
        kryo.register(RobotData.class, 15);
        kryo.register(PlayerData.class);
        kryo.register(Integer.class);
        kryo.register(BulletData.class, 17);
        kryo.register(HashMap.class);
        kryo.register(RobotDataMap.class);
        kryo.register(String.class);
        kryo.register(AddSinglePlayerWorld.class);

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
                    //System.out.println("Server received: " + object);

                    if (object instanceof AddSinglePlayerWorld e) {
                        Session session = new Session(1);
                        session.addPlayer(connection);

                        worlds.put(e.getWorldUUID(), session);
                        playerDatas.put(e.getWorldUUID(), new HashMap<>());
                        robotDatas.put(e.getWorldUUID(), new RobotDataMap(e.getWorldUUID()));
                    }

                    if (object instanceof PlayerData data) {
                        // updates player coordinates
                        String worldUUID = data.getWorldUUID();
                        PlayerData playerData = (PlayerData) object;

                        Map<Integer, Object> map = playerDatas.get(worldUUID);
                        map.put(connection.getID(), playerData);

                        for (Connection con : worlds.get(worldUUID).getPlayers()) {
                            con.sendTCP(map);
                        }
                    }
                    if (object instanceof BulletData) {
                        // update bullet data
                        String worldUUID = ((BulletData) object).getWorldUUID();

                        for (Connection con : worlds.get(worldUUID).getPlayers()) {
                            if (con.getID() != connection.getID()) {
                                con.sendTCP(object);
                            }
                        }
                    }
                    if (object instanceof RobotDataMap data) {
                        // update robot data
                        String worldUUID = data.getWorldUUID();
                        HashMap<String, RobotData> map = data.getMap();

                        RobotDataMap robotDataMap = robotDatas.get(worldUUID);
                        for (Map.Entry<String, RobotData> entry : map.entrySet()) {
                            robotDataMap.put(entry.getKey(), entry.getValue());
                        }

                        for (Connection con : worlds.get(worldUUID).getPlayers()) {
                            con.sendTCP(robotDataMap);
                        }
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

                String worldUUID = "crazy";
                for (Map.Entry<String, Session> entry : worlds.entrySet()) {
                    Session session = entry.getValue();
                    List<Connection> players = entry.getValue().getPlayers();
                    for (Connection player : players) {
                        if (player.equals(connection)) {
                            session.removePlayer(player);
                            worldUUID = entry.getKey();
                            break;
                        }
                    }
                }

                if (worlds.get(worldUUID).isEmpty()) {
                    worlds.remove(worldUUID);
                    playerDatas.remove(worldUUID);
                    robotDatas.remove(worldUUID);
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
