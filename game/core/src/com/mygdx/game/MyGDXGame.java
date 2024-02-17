package com.mygdx.game;

import Screens.MenuScreen;
import Screens.PlayScreen;
import Sprites.OtherPlayer;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                    System.out.println("received: " + object);
                    lastReceivedData = object;
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
            String[] playersData = lastReceivedData.toString().split("/");
            World world = playScreen.world;
            ArrayList<Integer> allConnectionIDs = new ArrayList<>();

            // Collect IDs from the latest received data
            for (String playerData : playersData) {
                String[] parts = playerData.split(":");
                int id = Integer.parseInt(parts[0]);
                allConnectionIDs.add(id);
            }

            // Update existing players or create new ones
            for (String playerData : playersData) {
                String[] parts = playerData.split(":");
                int id = Integer.parseInt(parts[0]);

                if (id != client.getID()) {
                    String[] coordinates = parts[1].split(",");
                    float otherPlayerPosX = Float.parseFloat(coordinates[0]);
                    float otherPlayerPosY = Float.parseFloat(coordinates[1]);
                    int frameIndex = Integer.parseInt(coordinates[2]);
                    boolean runningRight = Boolean.parseBoolean(coordinates[3]);

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
