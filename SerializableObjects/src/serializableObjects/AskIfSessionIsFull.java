package serializableObjects;

public class AskIfSessionIsFull {
    private String worldID;
    private boolean isFull;

    public AskIfSessionIsFull() {
        this.worldID = "";
        this.isFull = false;
    }

    public AskIfSessionIsFull(String worldID) {
        this.worldID = worldID;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public boolean isFull() {
        return isFull;
    }

    public String getWorldID() {
        return worldID;
    }
}
