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
import serializableObjects.AskPlayersWaitingScreen;
import serializableObjects.BulletData;
import serializableObjects.CrystalToRemove;
import serializableObjects.GetMultiPlayerWorldNames;
import serializableObjects.GetSinglePlayerWorldNames;
import serializableObjects.OpponentData;
import serializableObjects.OpponentDataMap;
import serializableObjects.PlayerData;
import serializableObjects.PlayerLeavesTheWorld;
import serializableObjects.PlayerLeavesWaitingScreen;
import serializableObjects.RemoveMultiPlayerWorld;
import serializableObjects.RemoveSinglePlayerWorld;
import serializableObjects.RevivePlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameServer {

    private final Server server;
    private final Map<String, Session> worlds = new HashMap<>();
    private final Map<String, List<String>> worldIDtoListOfPlayerIDs = new HashMap<>();
    private final Map<String, Connection> playerIDtoConnection = new HashMap<>();
    private final Map<String, Map<Integer, Object>> playerDatas = new HashMap<>();
    private final Map<String, OpponentDataMap> opponentDatas = new HashMap<>();
    private final Map<String, Set<CrystalToRemove>> removedCrystalsByWorld = new HashMap<>();
    private final Map<String, Map<String, String>> playerIDToSinglePlayerWorldNames = new HashMap<>();
    // playerID, world name, world id
    private final Map<String, Map<String, String>> playerIDToMultiPlayerWorldNames = new HashMap<>();
    private final Map<String, Integer[]> worldIDToWaveData = new HashMap<>();

    /**
     * Constructor for GameServer. Initializes the KryoNet server and binds it to the specified ports.
     */
    public GameServer() {
        server = new Server(1000000, 1000000);

        //registering classes
        Kryo kryo = server.getKryo();
        kryo.register(OpponentData.class, 15);
        kryo.register(PlayerData.class);
        kryo.register(Integer.class);
        kryo.register(BulletData.class, 17);
        kryo.register(HashMap.class);
        kryo.register(OpponentDataMap.class);
        kryo.register(String.class);
        kryo.register(AddSinglePlayerWorld.class);
        kryo.register(AddMultiPlayerWorld.class);
        kryo.register(CrystalToRemove.class, 22);
        kryo.register(RevivePlayer.class);
        kryo.register(AskIfSessionIsFull.class);
        kryo.register(PlayerLeavesTheWorld.class);
        kryo.register(GetSinglePlayerWorldNames.class);
        kryo.register(RemoveSinglePlayerWorld.class);
        kryo.register(GetMultiPlayerWorldNames.class);
        kryo.register(RemoveMultiPlayerWorld.class);
        kryo.register(AskPlayersWaitingScreen.class);
        kryo.register(PlayerLeavesWaitingScreen.class);
        kryo.register(Integer[].class);

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

                    if (object instanceof PlayerLeavesWaitingScreen message) {
                        handlePlayerLeavesWaitingScreen(connection, message);
                    }

                    if (object instanceof AskPlayersWaitingScreen request) {
                        handleAskPlayersWaitingScreen(request);
                    }

                    if (object instanceof RemoveMultiPlayerWorld request) {
                        handleRemoveMultiPlayerWorld(request);
                    }

                    if (object instanceof GetMultiPlayerWorldNames request) {
                        handleGetMultiPlayerWorldNames(connection, request);
                    }

                    if (object instanceof RemoveSinglePlayerWorld request) {
                        handleRemoveSinglePlayerWorld(request);
                    }

                    if (object instanceof GetSinglePlayerWorldNames request) {
                        handleGetSinglePlayerWorldNames(connection, request);
                    }

                    if (object instanceof PlayerLeavesTheWorld message) {
                        handlePlayerLeavesTheWorld(connection, message);
                    }

                    if (object instanceof AskIfSessionIsFull request) {
                        handleAskIfSessionIsFull(connection, request);
                    }

                    if (object instanceof CrystalToRemove) {
                        handleRemovedCrystal(connection, (CrystalToRemove) object);
                    }

                    if (object instanceof RevivePlayer data) {
                        handleRevivePlayer(connection, data);
                    }

                    if (object instanceof AddSinglePlayerWorld) {
                        createSinglePlayerWorld(connection, (AddSinglePlayerWorld) object);
                    }

                    if (object instanceof AddMultiPlayerWorld) {
                        createMultiPlayerWorld(connection, (AddMultiPlayerWorld) object);
                    }

                    if (object instanceof PlayerData) {
                        updatePlayerData(connection, (PlayerData) object);
                    }
                    if (object instanceof BulletData) {
                        updateBulletData(connection, (BulletData) object);
                    }
                    if (object instanceof OpponentDataMap) {
                        updateOpponentData((OpponentDataMap) object);
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

                for (Map.Entry<String, Connection> entry : playerIDtoConnection.entrySet()) {
                    if (entry.getValue().equals(connection)) {
                        playerIDtoConnection.remove(entry.getKey());
                        break;
                    }
                }
            }
        });
    }

    private void handlePlayerLeavesWaitingScreen(Connection connection, PlayerLeavesWaitingScreen message) {
        String worldID = message.getWorldID();
        Session session = worlds.get(worldID);

        if (session != null && playerDatas.containsKey(worldID)) {
            session.removePlayer(connection);
            playerDatas.get(worldID).remove(connection.getID());
            message.setCurrentPlayers(session.getPlayers().size());
            message.setMaxPlayers(session.getMaxPlayers());
        }

        if (worldIDtoListOfPlayerIDs.containsKey(worldID)) {
            for (String id : worldIDtoListOfPlayerIDs.get(worldID)) {
                playerIDtoConnection.get(id).sendTCP(message);
            }
        }
    }


    private void handleAskPlayersWaitingScreen(AskPlayersWaitingScreen request) {
        String worldID = request.getWorldID();

        if (worlds.containsKey(worldID)) {
            Session session = worlds.get(worldID);
            request.setCurrentPlayers(session.getPlayers().size());
            request.setMaxPlayers(session.getMaxPlayers());
        }

        if (worldIDtoListOfPlayerIDs.containsKey(worldID)) {
            for (String id : worldIDtoListOfPlayerIDs.get(worldID)) {
                playerIDtoConnection.get(id).sendTCP(request);
            }
        }
    }

    /**
     * Removes a multiplayer world. If one player decides to remove a world, it is removed everywhere, so no one can
     * play in that world anymore
     *
     * @param message    that is received
     */
    private void handleRemoveMultiPlayerWorld(RemoveMultiPlayerWorld message) {
        String worldID = message.getWorldID();
        String worldName = message.getWorldName();

        worlds.remove(worldID);
        playerDatas.remove(worldID);
        opponentDatas.remove(worldID);
        removedCrystalsByWorld.remove(worldID);

        if (worldIDtoListOfPlayerIDs.containsKey(worldID)) {
            for (String pID : worldIDtoListOfPlayerIDs.get(worldID)) {
                if (playerIDToMultiPlayerWorldNames.containsKey(pID)) {
                    playerIDToMultiPlayerWorldNames.get(pID).remove(worldName);
                    worldIDToWaveData.remove(worldID);

                    if (playerIDtoConnection.containsKey(pID)) {
                        handleGetMultiPlayerWorldNames(playerIDtoConnection.get(pID), new GetMultiPlayerWorldNames(pID));
                    }
                }
            }
        }

        worldIDtoListOfPlayerIDs.remove(worldID);
    }

    /**
     * Removes a single player world
     *
     * @param message    that is received
     */
    private void handleRemoveSinglePlayerWorld(RemoveSinglePlayerWorld message) {
        String playerID = message.getPlayerID();
        String worldID = message.getWorldID();
        String worldName = message.getWorldName();

        worlds.remove(worldID);
        playerDatas.remove(worldID);
        opponentDatas.remove(worldID);
        removedCrystalsByWorld.remove(worldID);

        if (playerIDToSinglePlayerWorldNames.containsKey(playerID)) {
            playerIDToSinglePlayerWorldNames.get(playerID).remove(worldName);
            worldIDToWaveData.remove(worldID);
        }
    }

    /**
     * Sends the info with the singlePlayer worlds the player has
     *
     * @param connection The connection object representing the client connection.
     * @param request    that is received
     */
    private void handleGetSinglePlayerWorldNames(Connection connection, GetSinglePlayerWorldNames request) {
        request.setWorldNamesAndIDs(playerIDToSinglePlayerWorldNames.get(request.getPlayerID()));
        request.setWorldNameToWaveData(worldIDToWaveData);
        connection.sendTCP(request);
    }

    /**
     * Sends the info with the multiPlayer worlds the player has
     *
     * @param connection The connection object representing the client connection.
     * @param request    that is received
     */
    private void handleGetMultiPlayerWorldNames(Connection connection, GetMultiPlayerWorldNames request) {
        request.setWorldNamesAndIDs(playerIDToMultiPlayerWorldNames.get(request.getPlayerId()));
        request.setWorldNameToWaveData(worldIDToWaveData);
        connection.sendTCP(request);
    }

    /**
     * Handles revive data
     *
     * @param connection of the player
     * @param data       reviveData
     */
    public void handleRevivePlayer(Connection connection, RevivePlayer data) {
        String worldID = getWorldUUIDForConnection(connection);
        if (worldID != null) {
            for (Connection con : worlds.get(worldID).getPlayers()) {
                if (!con.equals(connection)) {
                    con.sendTCP(data);
                }
            }
        }
    }

    /**
     * Handles the situation when the player leaves the world
     *
     * @param connection connection
     * @param message    has the worldID in it
     */
    public void handlePlayerLeavesTheWorld(Connection connection, PlayerLeavesTheWorld message) {
        String worldID = message.getWorldID();
        int currentWave = message.getCurrentWave();
        int currentTimeInWave = message.getCurrentTime();

        worldIDToWaveData.put(worldID, new Integer[] {currentWave, currentTimeInWave});
        Session session = worlds.get(worldID);
        if (session == null || !playerDatas.containsKey(worldID)) return;

        session.removePlayer(connection);
        playerDatas.get(worldID).remove(connection.getID());
    }

    /**
     * Asks the server whether the player can join a world
     *
     * @param connection connection
     * @param request    that asks if the world is full
     */
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
            for (Connection con : worlds.get(worldUUID).getPlayers()) {
                if (!con.equals(connection)) {
                    con.sendTCP(crystal);
                }
            }
        }
    }

    /**
     * Creates a single-player world when requested by a client.
     *
     * @param connection The connection object representing the client connection.
     * @param e          The AddSinglePlayerWorld object indicating the request to create a single-player world.
     */
    private void createSinglePlayerWorld(Connection connection, AddSinglePlayerWorld e) {
        String worldUUID = e.getWorldUUID();
        String playerID = e.getPlayerID();

        playerIDToSinglePlayerWorldNames.put(playerID, e.getSinglePlayerWorlds());
        worldIDToWaveData.put(worldUUID, new Integer[]{ 1, 0});

        if (!worlds.containsKey(worldUUID)) {
            Session session = new Session(1);
            session.addPlayer(connection);

            worlds.put(worldUUID, session);
            playerDatas.put(worldUUID, new HashMap<>());
            opponentDatas.put(worldUUID, new OpponentDataMap(e.getWorldUUID()));
            removedCrystalsByWorld.put(worldUUID, new HashSet<>());
        } else {
            Session session = worlds.get(worldUUID);
            session.addPlayer(connection);
        }
    }

    /**
     * Creates a multiplayer world when requested by a client.
     *
     * @param connection The connection object representing the client connection.
     * @param e          The AddMultiPlayerWorld object indicating the request to create a multiplayer world.
     */
    private void createMultiPlayerWorld(Connection connection, AddMultiPlayerWorld e) {
        String worldUUID = e.getWorldUUID();
        String playerID = e.getPlayerID();


        System.out.println(playerID);

        playerIDToMultiPlayerWorldNames.put(playerID, e.getMultiplayerWorlds());
        worldIDToWaveData.put(worldUUID, new Integer[]{1, 0});

        if (!worlds.containsKey(worldUUID)) {
            int numberOfPlayers = Integer.parseInt(worldUUID.split(":")[1]);
            Session session = new Session(numberOfPlayers);
            session.addPlayer(connection);

            worlds.put(worldUUID, session);
            playerDatas.put(worldUUID, new HashMap<>());
            opponentDatas.put(worldUUID, new OpponentDataMap(e.getWorldUUID()));
            removedCrystalsByWorld.put(worldUUID, new HashSet<>());
            worldIDtoListOfPlayerIDs.put(worldUUID, new ArrayList<>(List.of(playerID)));
        } else {
            Session session = worlds.get(worldUUID);
            session.addPlayer(connection);
            worldIDtoListOfPlayerIDs.get(worldUUID).add(playerID);
        }


        playerIDtoConnection.put(playerID, connection);

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
        if (!worlds.containsKey(worldUUID)) return;

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
     * Updates opponent data received from clients and broadcasts it to all other clients in the same world.
     *
     * @param data The OpponentDataMap object representing the updated opponent data.
     */
    private void updateOpponentData(OpponentDataMap data) {
        String worldUUID = data.getWorldUUID();
        HashMap<String, OpponentData> map = data.getMap();

        OpponentDataMap opponentDataMap = opponentDatas.get(worldUUID);
        if (opponentDataMap == null) return;

        for (Map.Entry<String, OpponentData> entry : map.entrySet()) {
            opponentDataMap.put(entry.getKey(), entry.getValue());
        }

        for (Connection con : worlds.get(worldUUID).getPlayers()) {
            con.sendTCP(opponentDataMap);
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
