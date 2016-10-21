package com.example.nyashcore.referee.content;

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
public class ActionList {

    /**
     * An array of matches.
     */
    private final List<Action> ACTIONS = new ArrayList<Action>();

    /**
     * A map of sample matches, by ID.
     */
    private final Map<String, Action> ACTION_MAP = new HashMap<String, Action>();

    public void addAction(Action action) {
        ACTIONS.add(action);
        ACTION_MAP.put(action.id, action);
    }

    public List<Action> getACTIONS() { return ACTIONS; }

    /**
     * A match representing a piece of content.
     */
    public static class Action {
        public final String id;
        public final String content;
        public final String details;
        public String idTeam;

        public Action(String id, String content, String details, String idTeam) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.idTeam = idTeam;
        }

        public boolean equals(Action act) {
            if (this.id == act.id && this.content == act.content) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
