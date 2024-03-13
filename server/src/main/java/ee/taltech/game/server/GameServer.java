package main.java.ee.taltech.game.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import main.java.ee.taltech.game.server.pathfinding.PathfindingManager;
import serializableObjects.BulletData;
import serializableObjects.PlayerData;
import serializableObjects.RobotData;
import serializableObjects.RobotDataMap;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.physics.box2d.World;

public class GameServer {

    private Server server;
    private World world;
    private Map<Integer, Object> playerInstanceCoordinates = new HashMap<>();
    private RobotDataMap robotDataMap = new RobotDataMap();
    private PathfindingManager pathfindingManager;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);



    /**
     * Constructor for GameServer. Initializes the KryoNet server and binds it to the specified ports.
     */
    public GameServer() {
        server = new Server(1000000, 1000000);
        pathfindingManager = new PathfindingManager();
        world = new World(new Vector2(0, 0), true);

        // Schedule a task to update robot data and send it to all clients every 50 milliseconds (20 times per second)
        scheduler.scheduleAtFixedRate(this::updateAndSendRobotData, 0, 50, TimeUnit.MILLISECONDS);

        //registering classes
        Kryo kryo = server.getKryo();
        kryo.register(RobotData.class, 15);
        kryo.register(PlayerData.class);
        kryo.register(Integer.class);
        kryo.register(BulletData.class, 17);
        kryo.register(HashMap.class);
        kryo.register(RobotDataMap.class);
        kryo.register(String.class);

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
                    if (object instanceof PlayerData) {
                        // updates player coordinates
                        PlayerData playerData = (PlayerData) object;


                        playerInstanceCoordinates.put(connection.getID(), playerData);

                        // Perform pathfinding for robots when player data is received
                        for (RobotData robotData : robotDataMap.getMap().values()) {
                            RobotData newData = pathfindingManager.findClosestPlayerAndSetPath(robotData, playerInstanceCoordinates);
                            robotDataMap.put(robotData.getUuid(), newData);
                        }

                        System.out.println(playerData);
                        server.sendToAllTCP(robotDataMap);
                        server.sendToAllTCP(playerInstanceCoordinates);
                    }
                    if (object instanceof BulletData) {
                        // update bullet data
                        server.sendToAllExceptTCP(connection.getID(), object);
                    }
                    if (object instanceof RobotDataMap) {
                        // update robot data
                        HashMap<String, RobotData> map = ((RobotDataMap) object).getMap();
                        for (Map.Entry<String, RobotData> entry : map.entrySet()) {
                            robotDataMap.put(entry.getKey(), entry.getValue());
                        }
                        server.sendToAllTCP(robotDataMap);
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

                // if everyone disconnects, every robot is destroyed
                if (playerInstanceCoordinates.isEmpty()) {
                    robotDataMap.getMap().clear();
                    server.sendToAllTCP(robotDataMap);
                }
            }
        });
    }

    /**
     * Method to be scheduled for updating robot data and sending it to all clients.
     */
    private void updateAndSendRobotData() {
        for (RobotData robotData : robotDataMap.getMap().values()) {

            RobotData newData = pathfindingManager.findClosestPlayerAndSetPath(robotData, playerInstanceCoordinates);
            robotDataMap.put(robotData.getUuid(), newData);

        }

        server.sendToAllTCP(robotDataMap);
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
