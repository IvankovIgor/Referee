package com.technopark.ivankov.referee.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchList {

    public static final List<Match> MATCHES = new ArrayList<>();
    public static final Map<String, Match> MATCH_MAP = new HashMap<>();

    public static class Match {
        private final String idMatch;
        private final String federation;
        private final String tournament;
        private final String stage;
        private final MatchConfig matchConfig;
        private final TeamList.Team team1;
        private final TeamList.Team team2;
        private int team1Score;
        private int team2Score;
        private boolean started;
        private boolean finished;
        private final List<Action> actionList;

        public Match(Match match) {
            idMatch = match.getIdMatch();
            federation = match.getFederation();
            tournament = match.getTournament();
            stage = match.getStage();
            matchConfig = new MatchConfig(match.getMatchConfig());
            if (TeamList.TEAM_MAP.containsKey(match.getTeam1().getIdTeam())) {
                team1 = TeamList.TEAM_MAP.get(match.getTeam1().getIdTeam());
            } else {
                team1 = new TeamList.Team(match.getTeam1());
            }
            if (TeamList.TEAM_MAP.containsKey(match.getTeam2().getIdTeam())) {
                team2 = TeamList.TEAM_MAP.get(match.getTeam2().getIdTeam());
            } else {
                team2 = new TeamList.Team(match.getTeam2());
            }
            actionList = new ArrayList<>();
            MATCHES.add(this);
            MATCH_MAP.put(idMatch, this);
        }

        public String getIdMatch() {
            return this.idMatch;
        }

        public String getFederation() {
            return this.federation;
        }

        public String getTournament() {
            return this.tournament;
        }

        public String getStage() { return this.stage; }

        public MatchConfig getMatchConfig() {
            return this.matchConfig;
        }

        public TeamList.Team getTeam1() {
            return this.team1;
        }

        public TeamList.Team getTeam2() {
            return this.team2;
        }

        public int getTeam1Score() {
            return team1Score;
        }

        public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }

        public int getTeam2Score() {
            return team2Score;
        }

        public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }

        public boolean isStarted() {
            return started;
        }

        public void setStarted(boolean started) {
            this.started = started;
        }

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) { this.finished = finished; }

        public List<Action> getActionList() { return actionList; }

        @Override
        public String toString() {
            String result = idMatch + "\n";
            result += federation + "\n";
            result += tournament + "\n";
            result += stage + "\n";
            result += matchConfig.countPeriods + "\n";
            result += matchConfig.timePeriod + "\n";
            result += team1.getName() + "\n";
            result += team2.getName() + "\n";

            return result;
        }
    }

    public static class MatchConfig {
        private final int timePeriod;
        private final int countPeriods;

        MatchConfig(MatchConfig matchConfig) {
            this.timePeriod = matchConfig.getTimePeriod();
            this.countPeriods = matchConfig.getCountPeriods();
        }

        public int getTimePeriod() {
            return this.timePeriod;
        }

        public int getCountPeriods() {
            return this.countPeriods;
        }
    }
}
