package main.java.ee.taltech.game.session;

import com.esotericsoftware.kryonet.Connection;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private final int maxPlayers;
    private final List<Connection> players;

    public Session(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public void addPlayer(Connection player) {
        if (!isFull()) {
            players.add(player);
        }
    }

    public List<Connection> getPlayers() {
        return players;
    }

    public void removePlayer(Connection player) {
        players.remove(player);
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }
}