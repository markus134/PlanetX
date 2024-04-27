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
     * @param worldID string
     */
    public AskIfSessionIsFull(String worldID) {
        this.worldID = worldID;
    }

    /**
     * Sets the value
     * @param full boolean
     */
    public void setFull(boolean full) {
        isFull = full;
    }

    /**
     * Checks if it is full
     * @return boolean
     */
    public boolean isFull() {
        return isFull;
    }

    /**
     * Getter method
     * @return the world id
     */
    public String getWorldID() {
        return worldID;
    }

    public int getCurrentAmountOfPlayers() {
        return currentAmountOfPlayers;
    }

    public int getMaxAmountOfPlayers() {
        return maxAmountOfPlayers;
    }

    public void setCurrentAmountOfPlayers(int currentAmountOfPlayers) {
        this.currentAmountOfPlayers = currentAmountOfPlayers;
    }

    public void setMaxAmountOfPlayers(int maxAmountOfPlayers) {
        this.maxAmountOfPlayers = maxAmountOfPlayers;
    }
}
