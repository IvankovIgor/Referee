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
    public static final List<Action> ACTIONS = new ArrayList<Action>();

    /**
     * A map of sample matches, by ID.
     */
    public static final Map<String, Action> ACTION_MAP = new HashMap<String, Action>();

    private static final int COUNT = 0;

//    static {
//        // Add some matches.
//        for (int i = 1; i <= COUNT; i++) {
//            addAction(createAction(i));
//        }
//    }

    public static void addAction(Action action) {
        ACTIONS.add(action);
        ACTION_MAP.put(action.id, action);
    }

//    private static Action createAction(int position) {
//        return new Action(String.valueOf(position), "Ivan Ivanov - goal", makeDetails(position));
//    }

//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Action: ").append(position);
//        builder.append("\nTime: 15:30");
//        return builder.toString();
//    }

    /**
     * A match representing a piece of content.
     */
    public static class Action {
        public final String id;
        public final String content;
        public final String details;

        public Action(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
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
