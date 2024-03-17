package main.java.ee.taltech.game.session;

import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private final int maxPlayers;
    private final List<Connection> players;

    /**
     * Constructor
     *
     * @param maxPlayers
     */
    public Session(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
    }

    /**
     * Checks if the session is full
     *
     * @return bool
     */
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    /**
     * Adds a connection to the list of players
     *
     * @param player
     */
    public void addPlayer(Connection player) {
        if (!isFull()) {
            players.add(player);
        }
    }

    /**
     * Returns the list of all connections in the session
     *
     * @return
     */
    public List<Connection> getPlayers() {
        return players;
    }

    /**
     * Removes a connection from the list
     *
     * @param player
     */
    public void removePlayer(Connection player) {
        players.remove(player);
    }

    /**
     * Checks if the session is empty
     *
     * @return
     */
    public boolean isEmpty() {
        return players.isEmpty();
    }
}