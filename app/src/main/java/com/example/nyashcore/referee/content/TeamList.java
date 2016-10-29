package com.example.nyashcore.referee.content;

import com.example.nyashcore.referee.MatchListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.nyashcore.referee.content.PlayerList.PLAYER_MAP;

public class TeamList {

    public static final Map<String, Team> TEAM_MAP = new HashMap<>();

    public static class Team {
        private final String name;
        private final String logo;
        private final String idTeam;
        private final List<PlayerList.Player> players;

        Team(Team team) {
            name = team.getName();
            logo = team.getLogo();
            idTeam = team.getIdTeam();
            players = new ArrayList<>();
            for (PlayerList.Player player : team.getPlayers()) {
                if (PLAYER_MAP.containsKey(player.getIdUser())) {
                    players.add(PLAYER_MAP.get(player.getIdUser()));
                } else {
                    players.add(new PlayerList.Player(player));
                    MatchListActivity.PLAYER_TEAM_MAP.put(player.getIdUser(), idTeam);
                }
            }
            TEAM_MAP.put(idTeam, this);
        }

        String getIdTeam() {
            return idTeam;
        }

        public String getName() {
            return name;
        }

        public String getLogo() {
            return logo;
        }

        public List<PlayerList.Player> getPlayers() { return players; }
    }
}
