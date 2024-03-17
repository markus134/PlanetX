package main.java.ee.taltech.game.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import main.java.ee.taltech.game.session.Session;
import serializableObjects.AddMultiPlayerWorld;
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
        kryo.register(AddMultiPlayerWorld.class);

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

                    // new singlePlayer world is created
                    if (object instanceof AddSinglePlayerWorld e) {
                        Session session = new Session(1);
                        session.addPlayer(connection);

                        worlds.put(e.getWorldUUID(), session);
                        playerDatas.put(e.getWorldUUID(), new HashMap<>());
                        robotDatas.put(e.getWorldUUID(), new RobotDataMap(e.getWorldUUID()));
                    }

                    // new multiPlayer world is created
                    if (object instanceof AddMultiPlayerWorld e) {
                        String worldUUID = e.getWorldUUID();

                        // checking if a world with this id already exists
                        // basically checking if a person is trying to join a world
                        // or if they are creating a new one
                        if (!worlds.containsKey(worldUUID)) {
                            int numberOfPlayers = Integer.parseInt(worldUUID.split(":")[1]);
                            Session session = new Session(numberOfPlayers);
                            session.addPlayer(connection);

                            worlds.put(worldUUID, session);
                            playerDatas.put(worldUUID, new HashMap<>());
                            robotDatas.put(worldUUID, new RobotDataMap(e.getWorldUUID()));
                        } else {
                            Session session = worlds.get(worldUUID);
                            if (session.isFull()) {

                                // sending one packet is not enough
                                // bcs we already send a lot of packets
                                // if we just send one, it might get discarded
                                // or lost in the process. That is why we are
                                // sending 60. It is an arbitrary number.
                                for (int i = 0; i < 60; i++) {
                                    connection.sendTCP("LOL");
                                }
                            } else {
                                session.addPlayer(connection);
                            }
                        }
                    }

                    if (object instanceof PlayerData data) {
                        // updates player coordinates
                        String worldUUID = data.getWorldUUID();
                        if (worlds.get(worldUUID).getPlayers().contains(connection)) {
                            PlayerData playerData = (PlayerData) object;

                            Map<Integer, Object> map = playerDatas.get(worldUUID);
                            map.put(connection.getID(), playerData);

                            for (Connection con : worlds.get(worldUUID).getPlayers()) {
                                con.sendTCP(map);
                            }
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

                // string "crazy" is needed bcs sometimes the player is connected to the server
                // but never added to the session. If we do not implement the extra check with the
                // string, it might cause unexpected behaviour.
                String worldUUID = "crazy";
                for (Map.Entry<String, Session> entry : worlds.entrySet()) {
                    Session session = entry.getValue();
                    List<Connection> players = entry.getValue().getPlayers();
                    for (Connection player : players) {
                        if (player.equals(connection)) {
                            session.removePlayer(player);
                            worldUUID = entry.getKey();

                            // removes the info about the disconnected player from the dictionary that
                            // is sent to all players in that session. This fixes the bug when a dead player
                            // stays on the screen, after they went back to the main menu.
                            playerDatas.get(worldUUID).remove(player.getID());
                            break;
                        }
                    }
                }

                // removing the data from the database if the session is empty
                if (!worldUUID.equals("crazy")) {
                    if (worlds.get(worldUUID).isEmpty()) {
                        worlds.remove(worldUUID);
                        playerDatas.remove(worldUUID);
                        robotDatas.remove(worldUUID);
                    }
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
