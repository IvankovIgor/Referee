package com.technopark.ivankov.referee.content;

import java.util.HashMap;
import java.util.Map;

public class PlayerList {

    public static final Map<String, Player> PLAYER_MAP = new HashMap<>();

    public static class Player {
        private final String id;
        private final String name;
//        private final Integer number;

        Player(Player player) {
            id = player.getId();
            name = player.getName();
//            number = player.getNumber();
            PLAYER_MAP.put(id, this);
        }

        public String getId() { return id; }

        public String getName() { return name; }

//        public Integer getNumber() { return number; }
    }
}
