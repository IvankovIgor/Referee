package com.technopark.ivankov.referee.content;

public class Action {
    private final int idAction;
    private final int idParentAction;
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
        this.idParentAction = this.idAction - 1;
        this.idMatch = idMatch;
        this.idTeam = idTeam;
        this.teamName = idTeam == null ? null : TeamList.TEAM_MAP.get(idTeam).getName();
        this.idPlayer = idPlayer;
        this.playerName = idPlayer == null ? null : PlayerList.PLAYER_MAP.get(idPlayer).getName();
        this.minute = minute;
        this.idEvent = idEvent;
        if (!(EventType.MIN.equals(idEvent))) {
            MatchList.MATCH_MAP.get(idMatch).getActionList().add(0, this);
        }
    }

    public Action(String idMatch, int idAction) {
        this.idAction = idAction;
        this.idParentAction = 0;
        this.idMatch = idMatch;
        this.idTeam = null;
        this.teamName = null;
        this.idPlayer = null;
        this.playerName = null;
        this.minute = 0;
        this.idEvent = null;
    }

    public enum EventType {
        MATCH_STARTED ("Начало матча"), MATCH_FINISHED ("Матч закончен"), TIME_STARTED ("Тайм начат"),
        TIME_FINISHED ("Тайм закончен"), GOAL ("Гол"), OWN_GOAL ("Автогол"),
        YELLOW_CARD ("Жёлтая карточка"), RED_CARD ("Красная карточка"), ASSIST ("Голевая передача"),
        MIN ("Минута");

        private final String event;

        EventType(String event) {
            this.event = event;
        }

        public String toString() {
            return this.event;
        }
    }

    public int getIdAction() {
        return this.idAction;
    }

    public int getIdParentAction() { return this.idParentAction; }

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
