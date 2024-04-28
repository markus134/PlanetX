package com.mygdx.game;

import Bullets.Bullet;
import Opponents.Opponent;
import Screens.MenuScreen;
import Screens.MultiPlayerScreen;
import Screens.PlayScreen;
import Screens.SettingsScreen;
import Screens.SinglePlayerScreen;
import Screens.WaitingScreen;
import Sprites.OtherPlayer;
import Tools.UUIDFileManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import crystals.Crystal;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The main class of the game, responsible for managing screens, rendering, and network communication.
 */
public class MyGDXGame extends Game {
    // Constants
//     private static final String SERVER_ADDRESS = "193.40.255.19";
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_TCP_PORT = 8080;
    private static final int SERVER_UDP_PORT = 8081;

    // These are used for collisions
    public static final short BULLET_CATEGORY = 0x0001;
    public static final short PLAYER_CATEGORY = 0x0002;
    public static final short OTHER_PLAYER_CATEGORY = 0x0004;
    public static final short WORLD_CATEGORY = 0x0008;
    public static final short OPPONENT_CATEGORY = 0x0010;

    // Game attributes
    public SpriteBatch batch;
    public static final int V_WIDTH = 1920;
    public static final int V_HEIGHT = 1080;
    public static final float PPM = 100;
    public Client client;
    private MenuScreen menu;
    private final List<Object> receivedPackets = new ArrayList<>();
    public PlayScreen playScreen;
    public Map<Integer, Set<OtherPlayer>> playerDict = new HashMap<>();
    public HashMap<Integer, PlayerData> playerDataMap = new HashMap<>();
    public HashMap<String, OtherPlayer> playerHashMapByUuid = new HashMap<>();
    public AskIfSessionIsFull serverReply;
    public static HashMap<String, PlayScreen> worldUuidToScreen = new HashMap<>();
    public String playerUUID;
    private UUIDFileManager uuidFileManager;

    /**
     * Initializes the game, creates music object and menu.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();

        try {
            this.uuidFileManager = new UUIDFileManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initializeMenu();
        try {
            this.playerUUID = uuidFileManager.assignUUID();
            System.out.println(playerUUID);
        } catch (IOException e) {
            throw new RuntimeException("Error creating a file with the unique id");
        }
        System.out.println(playerUUID);
        getSinglePlayerWorlds();
        getMultiPlayerWorlds();
    }

    /**
     * Renders the game, including updating the player data received from the server.
     */
    @Override
    public void render() {
        super.render();

        processReceivedPackets();
    }

    /**
     * Creates the client and sets up the connection with the server
     */
    public void createClient() {
        client = new Client(1000000, 1000000); // If we don't set these sizes big enough, the game could crash
        registerClasses(client.getKryo());

        client.start();
        try {
            client.connect(5000, SERVER_ADDRESS, SERVER_TCP_PORT, SERVER_UDP_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setupClientListener();
    }

    /**
     * Creates the playScreen.
     */
    public void createScreen(String worldUUID, int numberOfPlayers, int currentWave, int currentTimeInWave) {
        if (worldUuidToScreen.containsKey(worldUUID)) {
            playScreen = worldUuidToScreen.get(worldUUID);

            if (numberOfPlayers == 0) {
                for (Map.Entry<String, Opponent> entry : playScreen.opponents.entrySet()) {
                    Opponent opponent = entry.getValue();
                    System.out.println("destroying body in createScreen " + opponent.getBody());
                    playScreen.world.destroyBody(opponent.getBody());
                }
            }
        } else {
            playScreen = new PlayScreen(this, worldUUID, menu, currentWave, currentTimeInWave);
        }

        playScreen.opponents.clear();
        playScreen.opponentDataMap.getMap().clear();
        playScreen.opponentIds.clear();
        playerDict.clear();
        playerDataMap.clear();
        playerHashMapByUuid.clear();
        receivedPackets.clear();


        if (numberOfPlayers == 1)
            client.sendTCP(new AddSinglePlayerWorld(worldUUID, playerUUID, SinglePlayerScreen.singlePlayerWorlds));
        if (numberOfPlayers == 0)
            client.sendTCP(new AddMultiPlayerWorld(worldUUID, playerUUID, MultiPlayerScreen.multiPlayerWorlds));
    }

    /**
     * Registers classes for serialization with Kryo.
     *
     * @param kryo The Kryo instance to register classes with.
     */
    private void registerClasses(Kryo kryo) {
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


    }

    /**
     * Initializes the menu screen.
     */
    private void initializeMenu() {
        Music musicInTheMenu = Gdx.audio.newMusic(Gdx.files.internal("Music/menu.mp3"));
        musicInTheMenu.setLooping(true);
        musicInTheMenu.setVolume(SettingsScreen.musicValue);
        musicInTheMenu.play();

        menu = new MenuScreen(this, musicInTheMenu);
        setScreen(menu);
        this.createClient();
    }

    /**
     * Sets up the listener for the client.
     */
    private void setupClientListener() {
        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (!(object instanceof FrameworkMessage.KeepAlive)) {
                    if (object instanceof GetSinglePlayerWorldNames) {
                        handleGetSinglePlayerWorldNames((GetSinglePlayerWorldNames) object);
                    } else if (object instanceof GetMultiPlayerWorldNames) {
                        handleGetMultiPlayerWorldNames((GetMultiPlayerWorldNames) object);
                    } else if (object instanceof AskIfSessionIsFull) {
                        serverReply = (AskIfSessionIsFull) object;
                    } else if (object instanceof AskPlayersWaitingScreen) {
                        handleAskPlayersWaitingScreen((AskPlayersWaitingScreen) object);
                    } else if (object instanceof PlayerLeavesWaitingScreen) {
                        handlePlayerLeavesWaitingScreen((PlayerLeavesWaitingScreen) object);
                    } else {
                        receivedPackets.add(object); // Store received packet in a list, this is because render is only called 60 times a second
                    }
                }
            }
        }));
    }

    /**
     * Processes received packets.
     */
    private void processReceivedPackets() {
        // Create a copy of receivedPackets to avoid concurrent modification
        ArrayList<Object> packetsCopy = new ArrayList<>(receivedPackets);

        for (Object object : packetsCopy) {
            if (object instanceof BulletData) {
                handleBulletData((BulletData) object);
            } else if (object instanceof OpponentDataMap) {
                handleOpponentData((OpponentDataMap) object);
            } else if (object instanceof CrystalToRemove) {
                handleCrystalData((CrystalToRemove) object);
            } else if (object instanceof RevivePlayer) {
                if (playScreen.player.getUuid().equals(((RevivePlayer) object).getUuid())) {
                    playScreen.player.revive();
                }
            }

            if (object instanceof HashMap) {
                playerDataMap = (HashMap) object;
                handlePlayerData();
            }
        }

        receivedPackets.clear();
    }

    /**
     * Handles the singlePlayer world names
     *
     * @param reply that is received
     */
    private void handleGetSinglePlayerWorldNames(GetSinglePlayerWorldNames reply) {
        System.out.println(reply.getWorldNamesAndIDs());
        System.out.println("data: " + reply.getWorldNameToWaveData());
        if (reply.getWorldNamesAndIDs() == null) {
            SinglePlayerScreen.singlePlayerWorlds = new HashMap<>();
            SinglePlayerScreen.worldNameToWaveData = new HashMap<>();
        } else {
            SinglePlayerScreen.singlePlayerWorlds = reply.getWorldNamesAndIDs();
            SinglePlayerScreen.worldNameToWaveData = reply.getWorldNameToWaveData();
        }
    }

    /**
     * Receives the multiPlayer world names
     *
     * @param reply that is received
     */
    private void handleGetMultiPlayerWorldNames(GetMultiPlayerWorldNames reply) {
        if (reply.getWorldNamesAndIDs() == null) {
            MultiPlayerScreen.multiPlayerWorlds = new HashMap<>();
            MultiPlayerScreen.worldNameToWaveData = new HashMap<>();
        } else {
            MultiPlayerScreen.multiPlayerWorlds = reply.getWorldNamesAndIDs();
            MultiPlayerScreen.worldNameToWaveData = reply.getWorldNameToWaveData();
        }
        menu.multiPlayerScreen.updateTableValuesAfterRemovingWorld();
    }

    /**
     * Handles bullet data received from the server.
     *
     * @param data The bullet data received.
     */
    private void handleBulletData(BulletData data) {
        Bullet bullet = playScreen.bulletManager.obtainBullet(data.getX(), data.getY());
        bullet.body.setLinearVelocity(data.getLinVelX(), data.getLinVelY());
    }

    /**
     * Handles opponent data received from the server.
     *
     * @param opponentDataMap The opponent data map received.
     */
    private void handleOpponentData(OpponentDataMap opponentDataMap) {
        opponentDataMap.getMap().entrySet().removeIf(entry ->
                playScreen.allDestroyedOpponents.contains(entry.getKey())
        );

        playScreen.opponentDataMap = opponentDataMap;
    }

    /**
     * Handles player data received from the server.
     */
    private void handlePlayerData() {
        // updating info about players for the robots to move in the right direction
        World world = playScreen.world;

        Set<Integer> keys = playerDataMap.keySet();
        ArrayList<Integer> allConnectionIDs = new ArrayList<>(keys);
        //System.out.println(allConnectionIDs);

        // Update existing players or create new ones
        for (Integer id : allConnectionIDs) {
            if (id != client.getID()) {
                PlayerData playerData = playerDataMap.get(id);
                updatePlayer(id, playerData, world);
            }
        }

        removeDisconnectedPlayers(allConnectionIDs, world);
    }

    private void handleCrystalData(CrystalToRemove crystal) {
        playScreen.crystals.remove(Crystal.getCrystalById(crystal.getId()));
    }

    /**
     * Updates player data received from the server.
     *
     * @param id         The ID of the player.
     * @param playerData The player data received.
     * @param world      The Box2D world.
     */
    private void updatePlayer(int id, PlayerData playerData, World world) {
        float otherPlayerPosX = playerData.getX();
        float otherPlayerPosY = playerData.getY();
        int frameIndex = playerData.getFrame();
        boolean runningRight = playerData.isRunningRight();
        int health = playerData.getHealth();
        String playerId = playerData.getUuid();

        // If playerDict contains id, then update the data, otherwise add it to playerDict
        if (playerDict.containsKey(id)) {
            for (OtherPlayer otherPlayer : playerDict.get(id)) {
                otherPlayer.update(otherPlayerPosX, otherPlayerPosY, frameIndex, runningRight);
            }
        } else {
            OtherPlayer otherPlayer = new OtherPlayer(world, playScreen, otherPlayerPosX, otherPlayerPosY, health, playerId, id);

            Set<OtherPlayer> otherPlayers = playerDict.getOrDefault(id, new HashSet<>());
            otherPlayers.add(otherPlayer);

            playerDict.put(id, otherPlayers);

            playerHashMapByUuid.put(playerId, otherPlayer);
            otherPlayer.update(otherPlayerPosX, otherPlayerPosY, frameIndex, runningRight);
        }
    }

    /**
     * Removes disconnected players.
     *
     * @param allConnectionIDs The IDs of all connected players.
     * @param world            The Box2D world.
     */
    private void removeDisconnectedPlayers(List<Integer> allConnectionIDs, World world) {
        playerDict.keySet().removeIf(id -> {
            if (!allConnectionIDs.contains(id)) {
                for (OtherPlayer otherPlayer : playerDict.get(id)) {
                    System.out.println("destroying body in removeDisconnectedPlayers " + otherPlayer.b2body);
                    world.destroyBody(otherPlayer.b2body);
                }
                return true;
            }
            return false;
        });
    }

    /**
     * Gets the singlePlayer worlds
     */
    private void getSinglePlayerWorlds() {
        client.sendTCP(new GetSinglePlayerWorldNames(playerUUID));
    }

    /**
     * Gets the multiplayer worlds
     */
    private void getMultiPlayerWorlds() {
        client.sendTCP(new GetMultiPlayerWorldNames(playerUUID));
    }

    /**
     * Updates the waiting screen values
     *
     * @param reply
     */
    private void handleAskPlayersWaitingScreen(AskPlayersWaitingScreen reply) {
        WaitingScreen.currentPlayers = reply.getCurrentPlayers();
        WaitingScreen.maxPlayers = reply.getMaxPlayers();
    }

    /**
     * Updates the waiting screen values
     *
     * @param reply
     */
    private void handlePlayerLeavesWaitingScreen(PlayerLeavesWaitingScreen reply) {
        WaitingScreen.currentPlayers = reply.getCurrentPlayers();
        WaitingScreen.maxPlayers = reply.getMaxPlayers();
    }

    public void updateMenu() {
        menu.multiPlayerScreen.updateDisplayTable();
        menu.singlePlayerScreen.updateDisplayTable();
    }

    /**
     * Disposes of resources and closes the network client when the game is closed.
     */
    @Override
    public void dispose() {
        try {
            uuidFileManager.releaseUUID(this.playerUUID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        client.sendTCP(new PlayerLeavesTheWorld(playScreen.worldUUID, playScreen.hud.getCurrentWave(), playScreen.hud.getCurrentTime()));
        client.close();
        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
