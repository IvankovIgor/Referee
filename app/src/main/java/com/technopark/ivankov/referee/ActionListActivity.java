package com.technopark.ivankov.referee;

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

import com.technopark.ivankov.referee.content.ActionList;
import com.technopark.ivankov.referee.content.MatchList;

import java.util.List;

public class ActionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.action_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(MatchList.getCurrentMatch().getActions()));
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
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.aAction = aValues.get(position);
            holder.aIdView.setText(String.valueOf(aValues.get(position).getMinute()));
            holder.aContentView.setText(aValues.get(position).toString());

            holder.aView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return removeAction(holder.aAction, position);
                }
            });
        }

        public boolean removeAction(ActionList.Action action, int position) {
            if (action.getEvent() == ActionList.EventType.GOAL) {
                MatchList.MATCH_MAP.get(action.getIdMatch()).decrementScore(action.getIdTeam());
            } else if (action.getEvent() == ActionList.EventType.OWN_GOAL) {
                MatchList.MATCH_MAP.get(action.getIdMatch()).ownGoalDecrement(action.getIdTeam());
            }
            MatchList.MATCH_MAP.get(action.getIdMatch()).getActions().remove(action);
            notifyItemRemoved(position);
            return true;
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
