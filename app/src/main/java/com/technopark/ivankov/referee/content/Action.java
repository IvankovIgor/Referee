package com.technopark.ivankov.referee.content;

public class Action {
    private final int idAction;
    private final String idMatch;
    private final String idTeam;
    private final String idPlayer;
    private final int minute;
    private final EventType event;

    public Action(String idMatch, String idTeam, String idPlayer, int minute, EventType event) {
        this.idAction = MatchList.MATCH_MAP.get(idMatch).getActionList().size();
        this.idMatch = idMatch;
        this.idTeam = idTeam;
        this.idPlayer = idPlayer;
        this.minute = minute;
        this.event = event;
        MatchList.MATCH_MAP.get(idMatch).getActionList().add(this);
    }

    public enum EventType {
        MATCH_START(0),
        MATCH_END(1),
        TIME_START(2),
        TIME_END(3),
        GOAL(4),
        OWN_GOAL(5),
        YELLOW_CARD(6),
        RED_CARD(7);

        private final int index;

        EventType(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public int getIdAction() {
        return this.idAction;
    }

    public String getIdMatch() {
        return this.idMatch;
    }

    public String getIdTeam() {
        return this.idTeam;
    }

    public String getIdPlayer() {
        return this.idPlayer;
    }

    public int getMinute() {
        return this.minute;
    }

    public EventType getEvent() {
        return this.event;
    }

    @Override
    public String toString() {
        String result = String.valueOf(event);
        PlayerList.Player player = PlayerList.PLAYER_MAP.get(idPlayer);
        if (player != null) {
            result += " - " + player.getName();
        }
        return result;
    }
}
