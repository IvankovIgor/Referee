package com.example.nyashcore.referee.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerTeamList {

    public static final Map<String, Player> PLAYER_MAP = new HashMap<>();
    public static final Map<String, Team> TEAM_MAP = new HashMap<>();
    public static final Map<String, String> PLAYER_TEAM_MAP = new HashMap<>();

    public static class Player {
        private String idUser;
        private String name;
        private String image;
        private String number;

        Player(Player player) {
            idUser = player.getIdUser();
            name = player.getName();
            image = player.getImage();
            number = player.getNumber();
        }

        public String getIdUser() { return idUser; }

        public void setIdUser(String idUser) { this.idUser = idUser; }

        public String getName() {
            return name;
        }

        public void setName(String name) { this.name = name; }

        public String getImage() {
            return image;
        }

        public void setImage(String image) { this.image = image; }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) { this.number = number; }
    }

    public static class Team {
        private String name;
        private String logo;
        private String idTeam;
        private List<Player> players;

        Team(Team team) {
            name = team.getName();
            logo = team.getLogo();
            idTeam = team.getIdTeam();
            players = new ArrayList<>();
            for (Player player : team.getPlayers()) {
                if (PLAYER_MAP.containsKey(player.getIdUser())) {
                    players.add(PLAYER_MAP.get(player.getIdUser()));
                } else {
                    Player newPlayer = new Player(player);
                    players.add(newPlayer);
                    PLAYER_MAP.put(player.getIdUser(), newPlayer);
                    PLAYER_TEAM_MAP.put(player.getIdUser(), idTeam);
                }
            }
            PlayerTeamList.TEAM_MAP.put(idTeam, this);
        }

        public String getIdTeam() {
            return idTeam;
        }

        public void setIdTeam(String idTeam) { this.idTeam = idTeam; }

        public String getName() {
            return name;
        }

        public void setName(String name) { this.name = name; }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) { this.logo = logo; }

        public List<Player> getPlayers() { return players; }

        public void setPlayers(List<Player> players) { this.players = players; }
    }
}
