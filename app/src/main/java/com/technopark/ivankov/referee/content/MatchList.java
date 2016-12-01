package com.technopark.ivankov.referee.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchList {

    public static final List<Match> MATCHES = new ArrayList<>();
    public static final Map<String, Match> MATCH_MAP = new HashMap<>();

    public enum MatchStatus {
        NOT_STARTED, STARTED, BREAK, FINISHED
    }

    public static class Match {
        private final String idMatch;
        private final String federation;
        private final String tournament;
//        private final String stage;
        private final MatchConfig matchConfig;
        private final TeamList.Team team1;
        private final TeamList.Team team2;
        private int team1Score;
        private int team2Score;
        private final List<Action> actionList;
        private final List<Action> deletedActionList;
        private final List<String> sentOffPlayerList;
        private MatchStatus matchStatus;

        public Match(Match match) {
            idMatch = match.getIdMatch();
            federation = match.getFederation();
            tournament = match.getTournament();
//            stage = match.getStage();
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
            deletedActionList = new ArrayList<>();
            sentOffPlayerList = new ArrayList<>();
            matchStatus = MatchStatus.NOT_STARTED;
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

//        public String getStage() { return this.stage; }

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

        public List<Action> getActionList() { return actionList; }

        public List<Action> getDeletedActionList() { return deletedActionList; }

        public List<String> getSentOffPlayerList() { return sentOffPlayerList; }

        public MatchStatus getMatchStatus() {
            return matchStatus;
        }

        public void setMatchStatus(MatchStatus matchStatus) {
            this.matchStatus = matchStatus;
        }

        @Override
        public String toString() {
//            String result = idMatch + "\n";
            String result = "";
            result += "Федерация: " + federation + "\n";
            result += "Турнир: " + tournament + "\n";
//            result += "Стадия: " + stage + "\n";
            result += "Количество таймов: " + matchConfig.countPeriods + "\n";
            result += "Длительность таймов: " + matchConfig.timePeriod;
//            result += team1.getName() + "\n";
//            result += team2.getName() + "\n";

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
