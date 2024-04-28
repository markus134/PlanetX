package serializableObjects;

public class AskPlayersWaitingScreen {

    private int currentPlayers;
    private int maxPlayers;
    private String worldID;

    /**
     * Empty constructor
     */
    public AskPlayersWaitingScreen() {
        currentPlayers = 0;
        maxPlayers = 0;
        worldID = "";
    }

    /**
     * Constructor
     *
     * @param worldID
     */
    public AskPlayersWaitingScreen(String worldID) {
        this.worldID = worldID;
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

    /**
     * Getter method
     *
     * @return int
     */
    public int getCurrentPlayers() {
        return currentPlayers;
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
}
