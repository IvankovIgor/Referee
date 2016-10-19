package com.example.nyashcore.referee.content;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlayerList {

    /**
     * A map of players, by ID.
     */
    public static final Map<String, Player> PLAYER_MAP = new HashMap<String, Player>();

    /**
     * A player representing a piece of content.
     */
    public static class Player {
        private String id;
        private String name;
        private String image;
        private String number;
        private String idTeam;
        private String content;
        private String details;

        public Player(JSONObject jsonObject, String idTeam) {
            this.idTeam = idTeam;
            try {
                id = jsonObject.getString("idUser");
                name = jsonObject.getString("name");
                image = jsonObject.getString("image");
                number = jsonObject.getString("number");
            } catch (JSONException e) {
                System.out.println(e);
            }
            this.content = name;
            this.details = number + " " + name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public String getNumber() {
            return number;
        }

        public String getIdTeam() {
            return idTeam;
        }

        public String getContent() {
            return content;
        }

        public String getDetails() {
            return details;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Team {
        private String id;
        private String name;
        private String logo;
        private List<Player> PLAYERS = new ArrayList<Player>();

        public Team(JSONObject jsonObject) {
            try {
                id = jsonObject.getString("idTeam");
                name = jsonObject.getString("name");
                logo = jsonObject.getString("logo");
                JSONArray jsonArray = jsonObject.getJSONArray("players");
                for (int i = 0; i < jsonArray.length(); i++) {
                    addPlayer(jsonArray.getJSONObject(i), id);
                }
            } catch (JSONException e) {
                System.out.println(e);
            }
        }

        private void addPlayer(JSONObject jsonObject, String idTeam) {
            Player player = new Player(jsonObject, idTeam);
            this.PLAYERS.add(player);
            PLAYER_MAP.put(player.getId(), player);
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLogo() {
            return logo;
        }

        public List<Player> getPLAYERS() {
            return PLAYERS;
        }
    }
}
