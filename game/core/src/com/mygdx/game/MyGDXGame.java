package com.mygdx.game;

import ObjectsToSend.PlayerData;
import ObjectsToSend.RobotData;
import Screens.MenuScreen;
import Screens.PlayScreen;
import Sprites.OtherPlayer;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryo.util.*;

import java.io.IOException;
import java.util.*;

/**
 * The main class of the game, responsible for managing screens, rendering, and network communication.
 */
public class MyGDXGame extends Game {
    public SpriteBatch batch;
    public final static int V_WIDTH = 1920;
    public final static int V_HEIGHT = 1080;
    public final static float PPM = 100;

    public static Client client;

    private Object lastReceivedData;
    public static Map<Integer, OtherPlayer> playerDict = new HashMap<>();
    public static PlayScreen playScreen;
    private MenuScreen menu;

    /**
     * Initializes the game, creates essential objects, and sets up the network client.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        playScreen = new PlayScreen(this);
        menu = new MenuScreen(this);
        setScreen(menu);

        client = new Client();

        Kryo kryo = client.getKryo();
        kryo.register(RobotData.class, 15);
        kryo.register(PlayerData.class);
        kryo.register(Integer.class);
        kryo.register(HashMap.class);

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
                    if (object instanceof RobotData){
                        playScreen.robot.updatePosition((RobotData) object);
                    } else {
                        lastReceivedData = object;
                    }
                }
            }
        }));
    }

    /**
     * Renders the game, including updating the player data received from the server.
     */
    @Override
    public void render() {
        super.render();

        if (lastReceivedData != null) {
            if (lastReceivedData instanceof HashMap){
                HashMap data = ((HashMap)lastReceivedData);
                World world = playScreen.world;
                Set keys = data.keySet();
                ArrayList<Integer> allConnectionIDs = new ArrayList<>(keys);

                // Update existing players or create new ones
                for (Integer id : allConnectionIDs) {
                    if (id != client.getID()) {
                        PlayerData playerData = (PlayerData) data.get(id);
                        float otherPlayerPosX = playerData.getX();
                        float otherPlayerPosY = playerData.getY();
                        int frameIndex = playerData.getFrame();
                        boolean runningRight = playerData.isRunningRight();

                        // If playerDict contains id, then update the data, otherwise add it to playerDict
                        if (playerDict.containsKey(id)) {
                            OtherPlayer otherPlayer = playerDict.get(id);
                            otherPlayer.update(otherPlayerPosX, otherPlayerPosY, frameIndex, runningRight);
                        } else {
                            OtherPlayer otherPlayer = new OtherPlayer(world, playScreen, otherPlayerPosX, otherPlayerPosY);
                            playerDict.put(id, otherPlayer);
                            otherPlayer.update(otherPlayerPosX, otherPlayerPosY, frameIndex, runningRight);
                        }
                    }
                }

                // Remove disconnected players and destroy associated Box2D objects
                playerDict.keySet().removeIf(id -> {
                    if (!allConnectionIDs.contains(id)) {
                        // Player disconnected, destroy associated Box2D objects
                        OtherPlayer otherPlayer = playerDict.get(id);
                        world.destroyBody(otherPlayer.b2body);
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
