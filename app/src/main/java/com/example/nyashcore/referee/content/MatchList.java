package com.example.nyashcore.referee.content;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private static final int COUNT = 0;

//    static {
//        // Add some matches.
//        for (int i = 1; i <= COUNT; i++) {
//            addMatch(createMatch(i));
//        }
//    }

    private static void addMatch(Match match) {
        MATCHES.add(match);
        MATCH_MAP.put(String.valueOf(match.id), match);
    }

//    private static Match createMatch(int position) {
//        return new Match(String.valueOf(position), "Spartak Moscow - Manchester United", makeDetails(position));
//    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Match: ").append(position);
        builder.append("\nDate: 12.10.2016");
        builder.append("\nJudge: Mark Clattenburg");
        return builder.toString();
    }

    /**
     * A match representing a piece of content.
     */
    public static class Match {
        private String id;
        private String content;
        private String details;
        private String federation;
        private String tournament;
        private String stage;
        private JSONObject matchConfig;
        private JSONObject team1;
        private JSONObject team2;

        public Match(JSONObject jsonObject) {
            try {
                id = jsonObject.getString("idMatch");
                team1 = jsonObject.getJSONObject("team1");
                team2 = jsonObject.getJSONObject("team2");
                content = team1.getString("name") + " - " + team2.getString("name");
                matchConfig = jsonObject.getJSONObject("matchConfig");
                tournament = jsonObject.getString("tournament");
                stage = jsonObject.getString("stage");
                federation = jsonObject.getString("federation");
            } catch (JSONException e) {
                System.out.println(e);
            }
            details = "Federation: " + federation + "\nTournament: " + tournament + "\nStage: " + stage;
            MatchList.addMatch(this);
        }

        public String getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

        public String getDetails() {
            return details;
        }

        public String getTournament() {
            return tournament;
        }

        public JSONObject getMatchConfig() {
            return matchConfig;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
