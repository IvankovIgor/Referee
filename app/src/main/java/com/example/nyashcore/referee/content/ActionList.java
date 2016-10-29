package com.example.nyashcore.referee.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionList {

    public final static List<Action> ACTIONS = new ArrayList<>();
    public final static Map<Long, Action> ACTION_MAP = new HashMap<>();
    public enum EventType { START, FINISH, GOAL, OWN_GOAL, YELLOW_CARD, RED_CARD }

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
        }

        public long getIdAction() { return this.idAction; }

        public String getIdMatch() { return this.idMatch; }

        public String getIdTeam() { return this.idTeam; }

        public String getIdPlayer() { return this.idPlayer; }

        public int getMinute() { return this.minute; }

        public EventType getEvent() { return this.event; }

        public boolean equals(Action act) {
            return this.idAction == act.idAction;
        }
    }
}
