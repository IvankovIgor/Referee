package com.example.nyashcore.referee;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.pm.ActivityInfo;

import com.example.nyashcore.referee.content.ActionList;
import com.example.nyashcore.referee.content.MatchList;

import java.util.List;

public class ActionListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
//    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.action_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(MatchList.getCurrentMatch().getActionList().getACTIONS()));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ActionList.Action> aValues;

        public SimpleItemRecyclerViewAdapter(List<ActionList.Action> items) {
            aValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.action_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.aAction = aValues.get(position);
            holder.aIdView.setText(aValues.get(position).id);
            holder.aContentView.setText(aValues.get(position).content);

            holder.aView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int i = 0;
                    for(ActionList.Action act : MatchList.getCurrentMatch().getActionList().getACTIONS()) {
                        if (act.equals(holder.aAction)) {
                            break;
                        }
                        i++;
                    }
                    removeAt(i);
                    return false;
                }
            });
        }

        public void removeAt(int position) {
            String idTeam = MatchList.getCurrentMatch().getActionList().getACTIONS().get(position).idTeam;
            String details = MatchList.getCurrentMatch().getActionList().getACTIONS().get(position).details;
            if (details.equals("Goal")) {
                MatchList.getCurrentMatch().decrementScore(idTeam);
            } else if (details.equals("OwnGoal")) {
                MatchList.getCurrentMatch().ownGoalDecrement(idTeam);
            }
            MatchList.getCurrentMatch().getActionList().getACTIONS().remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public int getItemCount() {
            return aValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View aView;
            public final TextView aIdView;
            public final TextView aContentView;
            public ActionList.Action aAction;

            public ViewHolder(View view) {
                super(view);
                aView = view;
                aIdView = (TextView) view.findViewById(R.id.id);
                aContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + aContentView.getText() + "'";
            }
        }
    }
}
