package serializableObjects;

public class AskIfSessionIsFull {
    private String worldID;
    private boolean isFull;
    private int currentAmountOfPlayers;
    private int maxAmountOfPlayers;

    /**
     * Empty constructor
     */
    public AskIfSessionIsFull() {
        this.worldID = "";
        this.isFull = false;
        this.currentAmountOfPlayers = 0;
        this.maxAmountOfPlayers = 0;
    }

    /**
     * Constructor
     *
     * @param worldID string
     */
    public AskIfSessionIsFull(String worldID) {
        this.worldID = worldID;
    }

    /**
     * Sets the value
     *
     * @param full boolean
     */
    public void setFull(boolean full) {
        isFull = full;
    }

    /**
     * Checks if it is full
     *
     * @return boolean
     */
    public boolean isFull() {
        return isFull;
    }

    /**
     * Getter method
     *
     * @return the world id
     */
    public String getWorldID() {
        return worldID;
    }

    /**
     * Getter method
     *
     * @return the current amount of players
     */
    public int getCurrentAmountOfPlayers() {
        return currentAmountOfPlayers;
    }

    /**
     * Getter method
     *
     * @return the max amount of players
     */
    public int getMaxAmountOfPlayers() {
        return maxAmountOfPlayers;
    }

    /**
     * Sets the current amount of players
     *
     * @param currentAmountOfPlayers to set
     */
    public void setCurrentAmountOfPlayers(int currentAmountOfPlayers) {
        this.currentAmountOfPlayers = currentAmountOfPlayers;
    }

    /**
     * Sets the max amount of players
     *
     * @param maxAmountOfPlayers to set
     */
    public void setMaxAmountOfPlayers(int maxAmountOfPlayers) {
        this.maxAmountOfPlayers = maxAmountOfPlayers;
    }
}
