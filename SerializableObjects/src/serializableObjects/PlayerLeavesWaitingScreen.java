package serializableObjects;

public class PlayerLeavesWaitingScreen {

    private int currentPlayers;
    private int maxPlayers;
    private String worldID;

    /**
     * Empty constructor
     */
    public PlayerLeavesWaitingScreen() {
        worldID = "";
        currentPlayers = 0;
        maxPlayers = 0;
    }

    /**
     * Constructor
     *
     * @param worldID
     */
    public PlayerLeavesWaitingScreen(String worldID) {
        this.worldID = worldID;
    }

    /**
     * Sets the current players value
     *
     * @param currentPlayers int
     */
    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    /**
     * Sets the max players value
     *
     * @param maxPlayers int
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Getter method
     *
     * @return int
     */
    public int getCurrentPlayers() {
        return currentPlayers;
    }

    /**
     * Getter method
     *
     * @return int
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Getter method
     *
     * @return worldID
     */
    public String getWorldID() {
        return worldID;
    }
}
