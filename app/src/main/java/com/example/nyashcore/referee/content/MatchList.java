package com.example.nyashcore.referee.content;

import com.example.nyashcore.referee.MatchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchList {

    public static final List<Match> MATCHES = new ArrayList<Match>();
    public static String currentMatchId;

    public static final Map<String, Match> MATCH_MAP = new HashMap<String, Match>();

    public static Match getCurrentMatch() {
        return MATCH_MAP.get(currentMatchId);
    }

    public static void setCurrentMatchId(String currentMatchId) {
        MatchList.currentMatchId = currentMatchId;
    }

    public static class Match {
        private String idMatch;
        private String federation;
        private String tournament;
        private String stage;
        private MatchConfig matchConfig;
        private PlayerTeamList.Team team1;
        private PlayerTeamList.Team team2;
        private int team1Score;
        private int team2Score;
        private boolean started;
        private boolean finished;
        ActionList actionList;

        public Match(Match match) {
            actionList = new ActionList();
            idMatch = match.getIdMatch();
            federation = match.getFederation();
            tournament = match.getTournament();
            stage = match.getStage();
            matchConfig = new MatchConfig(match.getMatchConfig());
            if (PlayerTeamList.TEAM_MAP.containsKey(match.getTeam1().getIdTeam())) {
                team1 = PlayerTeamList.TEAM_MAP.get(match.getTeam1().getIdTeam());
            } else {
                team1 = new PlayerTeamList.Team(match.getTeam1());
            }
            if (PlayerTeamList.TEAM_MAP.containsKey(match.getTeam2().getIdTeam())) {
                team2 = PlayerTeamList.TEAM_MAP.get(match.getTeam2().getIdTeam());
            } else {
                team2 = new PlayerTeamList.Team(match.getTeam2());
            }
            MATCHES.add(this);
            MATCH_MAP.put(idMatch, this);
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

        public int getTeam1Score() {
            return team1Score;
        }

        public int getTeam2Score() {
            return team2Score;
        }

        public void incrementScore(String idTeam) {
            if (team1.getIdTeam().equals(idTeam)) {
                team1Score++;
            } else {
                team2Score++;
            }
            MatchActivity.refresh();
        }

        public void decrementScore(String idTeam) {
            if (team1.getIdTeam().equals(idTeam)) {
                team1Score--;
            } else {
                team2Score--;
            }
            MatchActivity.refresh();
        }

        public void ownGoal(String idTeam) {
            if (team2.getIdTeam().equals(idTeam)) {
                team1Score++;
            } else {
                team2Score++;
            }
            MatchActivity.refresh();
        }

        public void ownGoalDecrement(String idTeam) {
            if (team2.getIdTeam().equals(idTeam)) {
                team1Score--;
            } else {
                team2Score--;
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
