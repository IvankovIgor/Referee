package com.technopark.ivankov.referee.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.technopark.ivankov.referee.content.PlayerList.PLAYER_MAP;

public class TeamList {

    public static final Map<String, Team> TEAM_MAP = new HashMap<>();

    public static class Team {
        private final String name;
        private final String idTeam;
        private final List<PlayerList.Player> players;
        private final Map<String, Integer> numberMap;

        Team(Team team) {
            name = team.getName();
            idTeam = team.getIdTeam();
            players = new ArrayList<>();
            numberMap = new HashMap<>();
            for (PlayerList.Player player : team.getPlayers()) {
                if (PLAYER_MAP.containsKey(player.getIdUser())) {
                    players.add(PLAYER_MAP.get(player.getIdUser()));
                } else {
                    players.add(new PlayerList.Player(player));
                }
//                numberMap.put(player.getIdUser(), player.getNumber());
                numberMap.put(player.getIdUser(), players.size());
            }
            TEAM_MAP.put(idTeam, this);
        }

        public String getIdTeam() {
            return idTeam;
        }

        public String getName() {
            return name;
        }

        public List<PlayerList.Player> getPlayers() { return players; }

        public Map<String, Integer> getNumberMap() { return numberMap; }
    }
}
