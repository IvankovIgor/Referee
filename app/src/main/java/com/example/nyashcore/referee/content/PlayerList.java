package com.example.nyashcore.referee.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlayerList {

    /**
     * An array of players.
     */
    public static final List<Player> PLAYERS = new ArrayList<Player>();
    public static final List<Player> PLAYERS2 = new ArrayList<Player>();

    /**
     * A map of players, by ID.
     */
    public static final Map<String, Player> PLAYER_MAP = new HashMap<String, Player>();

    private static final int COUNT = 50;

    static {
        // Add some players.
        for (int i = 1; i <= COUNT; i++) {
            addPlayer(createPlayer(i), i/25);
        }
    }

    private static void addPlayer(Player player, int i) {
        if(i == 0) {
            PLAYERS.add(player);
        } else {
            PLAYERS2.add(player);
        }
        PLAYER_MAP.put(player.id, player);
    }

    private static Player createPlayer(int position) {
        if(position/25 == 0) {
            return new Player(String.valueOf(position), "Ivan Ivanov " + position, makeDetails(position));
        } else {
            return new Player(String.valueOf(position), "John Johnson " + position, makeDetails(position));
        }
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about player: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A player representing a piece of content.
     */
    public static class Player {
        public final String id;
        public final String content;
        public final String details;

        public Player(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
