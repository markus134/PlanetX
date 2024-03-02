package com.mygdx.game;

import Bullets.Bullet;
import Opponents.Robot;
import Screens.MenuScreen;
import Screens.PlayScreen;
import Sprites.OtherPlayer;
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
import serializableObjects.BulletData;
import serializableObjects.PlayerData;
import serializableObjects.RobotData;
import serializableObjects.RobotDataMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * The main class of the game, responsible for managing screens, rendering, and network communication.
 */
public class MyGDXGame extends Game {
    public SpriteBatch batch;
    public final static int V_WIDTH = 1920;
    public final static int V_HEIGHT = 1080;
    public final static float PPM = 100;
    public static Client client;
    public static Object lastReceivedData;
    private ArrayList<BulletData> lastReceivedBullets = new ArrayList<>();
    public static Map<Integer, Set<OtherPlayer>> playerDict = new HashMap<>();
    public static PlayScreen playScreen;
    private MenuScreen menu;
    public static final short BULLET_CATEGORY = 0x0001;
    public static final short PLAYER_CATEGORY = 0x0002;
    public static final short OTHER_PLAYER_CATEGORY = 0x0004;
    public static final short WORLD_CATEGORY = 0x0008;
    public static final short OPPONENT_CATEGORY = 0x0010;
    private static Music musicInTheMenu;


    /**
     * Initializes the playScreen and the client.
     */
    public void createScreenAndClient() {
        playScreen = new PlayScreen(this);

        client = new Client(1000000, 1000000);

        // registering classes
        Kryo kryo = client.getKryo();
        kryo.register(RobotData.class, 15);
        kryo.register(PlayerData.class);
        kryo.register(Integer.class);
        kryo.register(BulletData.class, 17);
        kryo.register(HashMap.class);
        kryo.register(RobotDataMap.class);
        kryo.register(String.class);

        client.start();
        client.sendTCP("Start");
        try {
            client.connect(5000, "localHost", 8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (!(object instanceof FrameworkMessage.KeepAlive)) {
                    if (object instanceof BulletData) {
                        // updating bullet data
                        System.out.println("Adding bullet data to list");
                        lastReceivedBullets.add((BulletData) object);

                    } else if (object instanceof RobotDataMap) {
                        // updating info about robots
                        RobotDataMap newRobotDataMap = (RobotDataMap) object;
                        newRobotDataMap.getMap().entrySet().removeIf(entry ->
                                PlayScreen.allDestroyedRobots.contains(entry.getKey())
                        );

                        PlayScreen.robotDataMap = newRobotDataMap;
                    } else if (object instanceof HashMap) {
                        HashMap<Integer, PlayerData> players = (HashMap) object;
                        players.entrySet().removeIf(entry ->
                                PlayScreen.allDestroyedPlayers.contains(entry.getValue().getUuid()));
                        lastReceivedData = players;

                    } else {
                        lastReceivedData = object;
                    }
                }
            }
        }));
    }

    /**
     * Initializes the game, creates music object and menu.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();

        musicInTheMenu = Gdx.audio.newMusic(Gdx.files.internal("Music/Ghostrifter-Official-Resurgence(chosic.com).mp3"));
        musicInTheMenu.setLooping(true);
        musicInTheMenu.play();

        menu = new MenuScreen(this, musicInTheMenu);

        setScreen(menu);
    }

    /**
     * Renders the game, including updating the player data received from the server.
     */
    @Override
    public void render() {
        super.render();
        //System.out.println(lastReceivedData);

        if (lastReceivedData != null) {
            // Made a special list for bullets as the packets were otherwise skipped (rendering was slower)
            for (BulletData data : lastReceivedBullets) {
                Bullet bullet = playScreen.bulletManager.obtainBullet(data.getX(), data.getY());
                bullet.body.setLinearVelocity(data.getLinVelX(), data.getLinVelY());
            }
            lastReceivedBullets.clear();

            if (lastReceivedData instanceof HashMap) {
                // updating info about players for the robots to move in the right direction
                Robot.playersInfo = ((HashMap) lastReceivedData);
                World world = playScreen.world;


                Set keys = ((HashMap) lastReceivedData).keySet();
                ArrayList<Integer> allConnectionIDs = new ArrayList<>(keys);

                // Update existing players or create new ones
                for (Integer id : allConnectionIDs) {
                    if (id != client.getID()) {
                        PlayerData playerData = (PlayerData) ((HashMap) lastReceivedData).get(id);
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
                            System.out.println("spawning new player " + playerId);
                            OtherPlayer otherPlayer = new OtherPlayer(world, playScreen, otherPlayerPosX, otherPlayerPosY, health, playerId, id);

                            Set<OtherPlayer> otherPlayers = playerDict.getOrDefault(id, new HashSet<>());
                            otherPlayers.add(otherPlayer);

                            playerDict.put(id, otherPlayers);
                            otherPlayer.update(otherPlayerPosX, otherPlayerPosY, frameIndex, runningRight);
                        }
                    }


                }

                // Remove disconnected players and destroy associated Box2D objects
                System.out.println(playerDict);
                System.out.println(allConnectionIDs);
                playerDict.keySet().removeIf(id -> {
                    if (!allConnectionIDs.contains(id)) {
                        // Player disconnected, destroy associated Box2D objects
                        for (OtherPlayer otherPlayer : playerDict.get(id)) {
                            world.destroyBody(otherPlayer.b2body);
                        }
                        return true; // Remove the player from the map
                    }
                    return false;
                });
            }
        }


    }

    /**
     * Disposes of resources and closes the network client when the game is closed.
     */
    @Override
    public void dispose() {
        client.close();
        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


