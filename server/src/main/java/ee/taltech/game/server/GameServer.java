package main.java.ee.taltech.game.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import main.java.ee.taltech.game.session.Session;
import serializableObjects.AddMultiPlayerWorld;
import serializableObjects.AddSinglePlayerWorld;
import serializableObjects.AskIfSessionIsFull;
import serializableObjects.BulletData;
import serializableObjects.CrystalToRemove;
import serializableObjects.PlayerData;
import serializableObjects.PlayerLeavesTheWorld;
import serializableObjects.RevivePlayer;
import serializableObjects.RobotData;
import serializableObjects.RobotDataMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameServer {

    private final Server server;
    private final Map<String, Session> worlds = new HashMap<>();
    private final Map<String, Map<Integer, Object>> playerDatas = new HashMap<>();
    private final Map<String, RobotDataMap> robotDatas = new HashMap<>();
    private final Map<String, Set<CrystalToRemove>> removedCrystalsByWorld = new HashMap<>();

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
        kryo.register(CrystalToRemove.class, 22);
        kryo.register(RevivePlayer.class);
        kryo.register(AskIfSessionIsFull.class);
        kryo.register(PlayerLeavesTheWorld.class);

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

                    if (object instanceof PlayerLeavesTheWorld message) {
                        handlePlayerLeavesTheWorld(connection, message);
                    }

                    if (object instanceof AskIfSessionIsFull request) {
                        handleAskIfSessionIsFull(connection, request);
                    }

                    if (object instanceof CrystalToRemove) {
                        handleRemovedCrystal(connection, (CrystalToRemove) object);
                    }

                    if (object instanceof RevivePlayer) {
                        server.sendToAllExceptTCP(connection.getID(), object);
                    }

                    // new singlePlayer world is created
                    if (object instanceof AddSinglePlayerWorld) {
                        createSinglePlayerWorld(connection, (AddSinglePlayerWorld) object);
                    }

                    // new multiPlayer world is created
                    if (object instanceof AddMultiPlayerWorld) {
                        createMultiPlayerWorld(connection, (AddMultiPlayerWorld) object);
                    }

                    if (object instanceof PlayerData) {
                        updatePlayerData(connection, (PlayerData) object);
                    }
                    if (object instanceof BulletData) {
                        updateBulletData(connection, (BulletData) object);
                    }
                    if (object instanceof RobotDataMap) {
                        updateRobotData((RobotDataMap) object);
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

                String worldUUID = removeDisconnectedPlayer(connection);

                if (!worldUUID.equals("crazy")) {
                    if (worlds.get(worldUUID).isEmpty()) {
                        worlds.remove(worldUUID);
                        playerDatas.remove(worldUUID);
                        robotDatas.remove(worldUUID);
                        removedCrystalsByWorld.remove(worldUUID);
                    }
                }
            }
        });
    }

    public void handlePlayerLeavesTheWorld(Connection connection, PlayerLeavesTheWorld message) {
        String worldID = message.getWorldID();
        Session session = worlds.get(worldID);

        session.removePlayer(connection);
        playerDatas.get(worldID).remove(connection.getID());
    }

    public void handleAskIfSessionIsFull(Connection connection, AskIfSessionIsFull request) {
        String worldID = request.getWorldID();
        System.out.println("Sending a reply");

        if (!worlds.containsKey(worldID)) {
            request.setFull(false);
        } else {
            Session session = worlds.get(worldID);
            request.setFull(session.isFull());
        }
        connection.sendTCP(request);
    }

    /**
     * Handles removing crystals when requested by a client and broadcasts the removal to all other clients in the same world.
     *
     * @param connection The connection object representing the client connection.
     * @param crystal    The CrystalToRemove object indicating the crystal to be removed.
     */
    private void handleRemovedCrystal(Connection connection, CrystalToRemove crystal) {
        String worldUUID = getWorldUUIDForConnection(connection);
        if (worldUUID != null) {
            removedCrystalsByWorld.get(worldUUID).add(crystal);
            server.sendToAllExceptTCP(connection.getID(), crystal);
        }
    }

    /**
     * Creates a single-player world when requested by a client.
     *
     * @param connection The connection object representing the client connection.
     * @param e          The AddSinglePlayerWorld object indicating the request to create a single-player world.
     */
    private void createSinglePlayerWorld(Connection connection, AddSinglePlayerWorld e) {
        Session session = new Session(1);
        session.addPlayer(connection);

        worlds.put(e.getWorldUUID(), session);
        playerDatas.put(e.getWorldUUID(), new HashMap<>());
        robotDatas.put(e.getWorldUUID(), new RobotDataMap(e.getWorldUUID()));
        removedCrystalsByWorld.put(e.getWorldUUID(), new HashSet<>());
    }

    /**
     * Creates a multiplayer world when requested by a client.
     *
     * @param connection The connection object representing the client connection.
     * @param e          The AddMultiPlayerWorld object indicating the request to create a multiplayer world.
     */
    private void createMultiPlayerWorld(Connection connection, AddMultiPlayerWorld e) {
        String worldUUID = e.getWorldUUID();
        System.out.println("creating multiplayer world with uuid: " + worldUUID);
        if (!worlds.containsKey(worldUUID)) {
            int numberOfPlayers = Integer.parseInt(worldUUID.split(":")[1]);
            Session session = new Session(numberOfPlayers);
            session.addPlayer(connection);

            worlds.put(worldUUID, session);
            playerDatas.put(worldUUID, new HashMap<>());
            robotDatas.put(worldUUID, new RobotDataMap(e.getWorldUUID()));
            removedCrystalsByWorld.put(worldUUID, new HashSet<>());
        } else {
            Session session = worlds.get(worldUUID);
            if (session.isFull()) {
                // sending one packet is not enough
                // bcs we already send a lot of packets
                // if we just send one, it might get discarded
                // or lost in the process. That is why we are
                // sending 60. It is an arbitrary number

                for (int i = 0; i < 60; i++) {
                    connection.sendTCP("LOL");
                }
            } else {
                session.addPlayer(connection);
            }
        }

        sendRemovedCrystalsToNewConnection(connection);
    }

    /**
     * Updates player data received from clients and broadcasts it to all other clients in the same world.
     *
     * @param connection The connection object representing the client connection.
     * @param data       The PlayerData object representing the updated player data.
     */
    private void updatePlayerData(Connection connection, PlayerData data) {
        String worldUUID = data.getWorldUUID();
        if (worlds.get(worldUUID).getPlayers().contains(connection)) {

            Map<Integer, Object> map = playerDatas.get(worldUUID);
            map.put(connection.getID(), data);

            for (Connection con : worlds.get(worldUUID).getPlayers()) {
                con.sendTCP(map);
            }
        }
    }

    /**
     * Updates bullet data received from clients and broadcasts it to all other clients in the same world.
     *
     * @param connection The connection object representing the client connection.
     * @param data       The BulletData object representing the updated bullet data.
     */
    private void updateBulletData(Connection connection, BulletData data) {
        String worldUUID = data.getWorldUUID();

        for (Connection con : worlds.get(worldUUID).getPlayers()) {
            if (con.getID() != connection.getID()) {
                con.sendTCP(data);
            }
        }
    }

    /**
     * Updates robot data received from clients and broadcasts it to all other clients in the same world.
     *
     * @param data The RobotDataMap object representing the updated robot data.
     */
    private void updateRobotData(RobotDataMap data) {
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

    /**
     * Retrieves the world UUID associated with a given connection.
     *
     * @param connection The connection object representing the client connection.
     * @return The world UUID associated with the connection, or null if not found.
     */
    private String getWorldUUIDForConnection(Connection connection) {
        for (Map.Entry<String, Session> entry : worlds.entrySet()) {
            Session session = entry.getValue();
            if (session.getPlayers().contains(connection)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Removes a disconnected player from the server.
     *
     * @param connection The connection object representing the disconnected client.
     * @return The UUID of the world from which the player was removed.
     */
    private String removeDisconnectedPlayer(Connection connection) {
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
        return worldUUID;
    }

    /**
     * Sends removed crystals to a newly connected client.
     *
     * @param connection The connection object representing the newly connected client.
     */
    private void sendRemovedCrystalsToNewConnection(Connection connection) {
        String worldUUID = getWorldUUIDForConnection(connection);
        if (worldUUID != null) {
            Set<CrystalToRemove> removedCrystals = removedCrystalsByWorld.getOrDefault(worldUUID, new HashSet<>());
            for (CrystalToRemove crystal : removedCrystals) {
                connection.sendTCP(crystal);
            }
        }
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
