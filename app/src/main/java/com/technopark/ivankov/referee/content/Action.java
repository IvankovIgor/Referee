package com.technopark.ivankov.referee.content;

public class Action {
    private final int idAction;
    private final String idMatch;
    private final String idTeam;
    private final String teamName;
    private final String idPlayer;
    private final String playerName;
    private final int minute;
    private final EventType idEvent;

    public Action(String idMatch, String idTeam, String idPlayer, int minute, EventType idEvent) {
        this.idAction = MatchList.MATCH_MAP.get(idMatch).getActionList().size() +
                        MatchList.MATCH_MAP.get(idMatch).getDeletedActionList().size();
        this.idMatch = idMatch;
        this.idTeam = idTeam;
        this.teamName = idTeam == null ? null : TeamList.TEAM_MAP.get(idTeam).getName();
        this.idPlayer = idPlayer;
        this.playerName = idPlayer == null ? null : PlayerList.PLAYER_MAP.get(idPlayer).getName();
        this.minute = minute;
        this.idEvent = idEvent;
        MatchList.MATCH_MAP.get(idMatch).getActionList().add(0, this);
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

    public String getTeamName() { return this.teamName; }

    public String getIdPlayer() {
        return this.idPlayer;
    }

    public String getPlayerName() { return this.playerName; }

    public int getMinute() {
        return this.minute;
    }

    public EventType getIdEvent() {
        return this.idEvent;
    }

    @Override
    public String toString() {
        String result = String.valueOf(idEvent);
        PlayerList.Player player = PlayerList.PLAYER_MAP.get(idPlayer);
        if (player != null) {
            result += " - " + player.getName();
        }
        return result;
    }
}
