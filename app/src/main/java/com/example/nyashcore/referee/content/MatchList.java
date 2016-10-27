package com.example.nyashcore.referee.content;

import com.example.nyashcore.referee.MatchActivity;

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
    public static String currentMatchId;

    /**
     * A map of sample matches, by ID.
     */
    public static final Map<String, Match> MATCH_MAP = new HashMap<String, Match>();

    private static void addMatch(Match match) {
        MATCHES.add(match);
        MATCH_MAP.put(String.valueOf(match.idMatch), match);
    }

    public static Match getCurrentMatch() {
        return MATCH_MAP.get(currentMatchId);
    }

    public static void setCurrentMatchId(String currentMatchId) {
        MatchList.currentMatchId = currentMatchId;
    }

    /**
     * A match representing a piece of content.
     */
    public static class Match {
        private String idMatch;
        private int numOfMatch;
        private String content;
        private String details;
        private String federation;
        private String tournament;
        private String stage;
        private PlayerList.Team firstTeam;
        private PlayerList.Team secondTeam;
        private int timePeriod;
        private int countPeriods;
        private int firstScore;
        private int secondScore;
        private boolean started;
        private boolean finished;
        ActionList actionList;

        public Match(JSONObject jsonObject) {
            numOfMatch = MATCHES.size() + 1;
            firstScore = 0;
            secondScore = 0;
            started = false;
            finished = false;
            actionList = new ActionList();
            try {
                idMatch = jsonObject.getString("idMatch");
                JSONObject team1 = jsonObject.getJSONObject("team1");
                JSONObject team2 = jsonObject.getJSONObject("team2");
                firstTeam = new PlayerList.Team(team1);
                secondTeam = new PlayerList.Team(team2);
                content = team1.getString("name") + " - " + team2.getString("name");
                tournament = jsonObject.getString("tournament");
                stage = jsonObject.getString("stage");
                federation = jsonObject.getString("federation");
                timePeriod = jsonObject.getJSONObject("matchConfig").getInt("timePeriod");
                countPeriods = jsonObject.getJSONObject("matchConfig").getInt("countPeriods");
            } catch (JSONException e) {
                System.out.println(e);
            }
            details = "Federation: " + federation + "\nTournament: " + tournament + "\nStage: " + stage;
            MatchList.addMatch(this);
        }

        public String getIdMatch() {
            return idMatch;
        }

        public String getNumOfMatch() { return String.valueOf(numOfMatch); }

        public String getContent() {
            return content;
        }

        public String getDetails() {
            return details;
        }

        public String getTournament() {
            return tournament;
        }

        public PlayerList.Team getFirstTeam() {
            return firstTeam;
        }

        public PlayerList.Team getSecondTeam() {
            return secondTeam;
        }

        public long getTimePeriod() {
            return timePeriod * 60 * 1000L;
        }

        public int getCountPeriods() {
            return  countPeriods;
        }

        public int getFirstScore() {
            return firstScore;
        }

        public int getSecondScore() {
            return secondScore;
        }

        public void incrementScore(String idTeam) {
            if (firstTeam.getId().equals(idTeam)) {
                firstScore++;
            } else {
                secondScore++;
            }
            MatchActivity.refresh();
        }

        public void decrementScore(String idTeam) {
            if (firstTeam.getId().equals(idTeam)) {
                firstScore--;
            } else {
                secondScore--;
            }
            MatchActivity.refresh();
        }

        public void ownGoal(String idTeam) {
            if (secondTeam.getId().equals(idTeam)) {
                firstScore++;
            } else {
                secondScore++;
            }
            MatchActivity.refresh();
        }

        public void ownGoalDecrement(String idTeam) {
            if (secondTeam.getId().equals(idTeam)) {
                firstScore--;
            } else {
                secondScore--;
            }
            MatchActivity.refresh();
        }

        public boolean isStarted() {
            return started;
        }

        public void setStarted() {
            this.started = true;
        }

        public boolean isFinished() {
            return finished;
        }

        public void setFinished() {
            this.finished = true;
        }

        public ActionList getActionList() { return actionList; }

        @Override
        public String toString() {
            return content;
        }
    }
}
