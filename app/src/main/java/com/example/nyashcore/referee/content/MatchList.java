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
public class MatchList {

    /**
     * An array of matches.
     */
    public static final List<Match> MATCHES = new ArrayList<Match>();

    /**
     * A map of sample matches, by ID.
     */
    public static final Map<String, Match> MATCH_MAP = new HashMap<String, Match>();

    private static final int COUNT = 25;

    static {
        // Add some matches.
        for (int i = 1; i <= COUNT; i++) {
            addMatch(createMatch(i));
        }
    }

    private static void addMatch(Match match) {
        MATCHES.add(match);
        MATCH_MAP.put(match.id, match);
    }

    private static Match createMatch(int position) {
        return new Match(String.valueOf(position), "Spartak Moscow - Manchester United " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Match: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A match representing a piece of content.
     */
    public static class Match {
        public final String id;
        public final String content;
        public final String details;

        public Match(String id, String content, String details) {
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
