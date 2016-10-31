package com.technopark.ivankov.referee.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionList {

    public final static List<Action> ACTIONS = new ArrayList<>();
    public final static Map<Long, Action> ACTION_MAP = new HashMap<>();

    public enum EventType {
        MATCH_START (0),
        MATCH_END (1),
        TIME_START (2),
        TIME_END (3),
        GOAL (4),
        OWN_GOAL (5),
        YELLOW_CARD (6),
        RED_CARD (7);

        private final int index;

        EventType(int index) {
            this.index = index;
        }

        public int getIndex() { return index;}
    }

    public static class Action {
        private final long idAction;
        private final String idMatch;
        private final String idTeam;
        private final String idPlayer;
        private final int minute;
        private final EventType event;

        public Action(String idMatch, String idTeam, String idPlayer, int minute, EventType event) {
            this.idAction = ACTIONS.size();
            this.idMatch = idMatch;
            this.idTeam = idTeam;
            this.idPlayer = idPlayer;
            this.minute = minute;
            this.event = event;
            ACTIONS.add(this);
            ACTION_MAP.put(idAction, this);
            MatchList.MATCH_MAP.get(idMatch).getActions().add(this);
        }

        public long getIdAction() { return this.idAction; }

        public String getIdMatch() { return this.idMatch; }

        public String getIdTeam() { return this.idTeam; }

        public String getIdPlayer() { return this.idPlayer; }

        public int getMinute() { return this.minute; }

        public EventType getEvent() { return this.event; }

        @Override
        public String toString() {
            String result = String.valueOf(event);
            PlayerList.Player player = PlayerList.PLAYER_MAP.get(idPlayer);
            if (player != null) {
                result +=  " - " + player.getName();
            }
            return result;
        }
    }
}
