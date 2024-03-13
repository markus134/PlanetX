package main.java.ee.taltech.game.server.pathfinding;

import serializableObjects.PlayerData;
import serializableObjects.RobotData;

import java.util.Map;

public class PathfindingManager {

    /**
     * Finds the closest player to the given robot coordinates and sets the path or waypoints accordingly.
     *
     * @param robotData   The robot for which to find the closest player and set the path.
     * @param playersInfo The map containing player information.
     */
    public RobotData findClosestPlayerAndSetPath(RobotData robotData, Map<Integer, Object> playersInfo) {
        float shortestDistance = Float.MAX_VALUE;
        float closestX = 0;
        float closestY = 0;

        float robotX = robotData.getX();
        float robotY = robotData.getY();

        for (Object playerInfo : playersInfo.values()) {
            PlayerData info = (PlayerData) playerInfo;
            float playerX = info.getX();
            float playerY = info.getY();

            float deltaX = playerX - robotX;
            float deltaY = playerY - robotY;

            float actualDistance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (actualDistance < shortestDistance) {
                shortestDistance = actualDistance;
                closestX = playerX;
                closestY = playerY;

                System.out.println("New closest for robot " + robotData.getUuid() + " is " +
                        closestX + " " + closestY + " because its distance is " + actualDistance);
            }
        }

        // Now closestX and closestY contain the coordinates of the closest player.
        // Set the calculated path for the robot towards the closest player.
        return setPathForRobot(robotData, closestX, closestY, robotX, robotY);
    }

    // Set the path or waypoints for the given robot towards the target coordinates
    private RobotData setPathForRobot(RobotData robotData, float targetX, float targetY, float robotX, float robotY) {
        if (targetX > robotX) {
            robotData.setLinX(0.05f);
        } else {
            robotData.setLinX(-0.05f);
        }
        if (targetY > robotY) {
            robotData.setLinY(0.05f);
        } else {
            robotData.setLinY(-0.05f);
        }

        return robotData;
    }

}
