package com.technopark.ivankov.referee.action_list;

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

import com.technopark.ivankov.referee.R;
import com.technopark.ivankov.referee.content.Action;
import com.technopark.ivankov.referee.content.MatchList;
import com.technopark.ivankov.referee.match.MatchActivity;

import java.util.List;

public class ActionListActivity extends AppCompatActivity {

    private MatchList.Match mMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mMatch = MatchList.MATCH_MAP.get(getIntent().getStringExtra(MatchActivity.MATCH_ID));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.action_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mMatch.getActionList()));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Action> aValues;

        public SimpleItemRecyclerViewAdapter(List<Action> items) {
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
            holder.aIdView.setText(String.valueOf(aValues.get(position).getMinute()));
            holder.aContentView.setText(aValues.get(position).toString());

            holder.aView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return removeAction(holder.aAction, holder.getAdapterPosition());
                }
            });
        }

        public boolean removeAction(Action action, int position) {
            if (action.getIdEvent() == Action.EventType.GOAL) {
                decrementScore(action.getIdTeam());
            } else if (action.getIdEvent() == Action.EventType.OWN_GOAL) {
                if (mMatch.getTeam1().getIdTeam().equals(action.getIdTeam())) {
                    decrementScore(mMatch.getTeam2().getIdTeam());
                } else {
                    decrementScore(mMatch.getTeam1().getIdTeam());
                }
            }
            mMatch.getActionList().remove(action);
            notifyItemRemoved(position);
            return true;
        }

        public void decrementScore(String idTeam) {
            if (mMatch.getTeam1().getIdTeam().equals(idTeam)) {
                mMatch.setTeam1Score(mMatch.getTeam1Score() - 1);
            } else {
                mMatch.setTeam2Score(mMatch.getTeam2Score() - 1);
            }
        }

        @Override
        public int getItemCount() {
            return aValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View aView;
            public final TextView aIdView;
            public final TextView aContentView;
            public Action aAction;

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
