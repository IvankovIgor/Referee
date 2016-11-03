package com.technopark.ivankov.referee.content;

import java.util.HashMap;
import java.util.Map;

public class PlayerList {

    public static final Map<String, Player> PLAYER_MAP = new HashMap<>();

    public static class Player {
        private final String idUser;
        private final String name;
        private final String image;
        private final Integer number;

        Player(Player player) {
            idUser = player.getIdUser();
            name = player.getName();
            image = player.getImage();
            number = player.getNumber();
            PLAYER_MAP.put(idUser, this);
        }

        public String getIdUser() { return idUser; }

        public String getName() { return name; }

        public String getImage() { return image; }

        public Integer getNumber() { return number; }
    }
}
