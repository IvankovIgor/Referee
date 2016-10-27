package com.example.nyashcore.referee.content;

import com.example.nyashcore.referee.MatchActivity;

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
        private String federation;
        private String tournament;
        private String stage;
        private MatchConfig matchConfig;
        private PlayerTeamList.Team team1;
        private PlayerTeamList.Team team2;
        private int firstScore;
        private int secondScore;
        private boolean started;
        private boolean finished;
        ActionList actionList;

        public Match(Match match) {
            numOfMatch = MATCHES.size() + 1;
            firstScore = 0;
            secondScore = 0;
            started = false;
            finished = false;
            actionList = new ActionList();
            idMatch = match.getIdMatch();
            federation = match.getFederation();
            tournament = match.getTournament();
            stage = match.getStage();
            matchConfig = new MatchConfig(match.getMatchConfig());
            team1 = new PlayerTeamList.Team(match.getTeam1());
            team2 = new PlayerTeamList.Team(match.getTeam2());
            MatchList.addMatch(this);
        }

        public String getIdMatch() {
            return this.idMatch;
        }

        public void setIdMatch(String idMatch) {
            this.idMatch = idMatch;
        }

        public String getFederation() {
            return this.federation;
        }

        public void setFederation(String federation) {
            this.federation = federation;
        }

        public String getTournament() {
            return this.tournament;
        }

        public void setTournament(String tournament) { this.tournament = tournament; }

        public String getStage() { return this.stage; }

        public void setStage(String stage) { this.stage = stage; }

        public MatchConfig getMatchConfig() {
            return this.matchConfig;
        }

        public void setMatchConfig(MatchConfig matchConfig) {
            this.matchConfig = matchConfig;
        }

        public PlayerTeamList.Team getTeam1() {
            return this.team1;
        }

        public void setTeam(PlayerTeamList.Team team1) {
            this.team1 = team1;
        }

        public PlayerTeamList.Team getTeam2() {
            return this.team2;
        }

        public void setTeam2(PlayerTeamList.Team team2) {
            this.team2 = team2;
        }

        public String getNumOfMatch() { return String.valueOf(numOfMatch); }

        public int getFirstScore() {
            return firstScore;
        }

        public int getSecondScore() {
            return secondScore;
        }

        public void incrementScore(String idTeam) {
            if (team1.getIdTeam().equals(idTeam)) {
                firstScore++;
            } else {
                secondScore++;
            }
            MatchActivity.refresh();
        }

        public void decrementScore(String idTeam) {
            if (team1.getIdTeam().equals(idTeam)) {
                firstScore--;
            } else {
                secondScore--;
            }
            MatchActivity.refresh();
        }

        public void ownGoal(String idTeam) {
            if (team2.getIdTeam().equals(idTeam)) {
                firstScore++;
            } else {
                secondScore++;
            }
            MatchActivity.refresh();
        }

        public void ownGoalDecrement(String idTeam) {
            if (team2.getIdTeam().equals(idTeam)) {
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
    }

    public static class MatchConfig {
        int timePeriod;
        int countPeriods;

        MatchConfig(MatchConfig matchConfig) {
            this.timePeriod = matchConfig.getTimePeriod();
            this.countPeriods = matchConfig.getCountPeriods();
        }

        public int getTimePeriod() {
            return this.timePeriod;
        }

        public void setTimePeriod(int timePeriod) {
            this.timePeriod = timePeriod;
        }

        public int getCountPeriods() {
            return this.countPeriods;
        }

        public void setCountPeriods(int countPeriods) {
            this.countPeriods = countPeriods;
        }

    }

}
