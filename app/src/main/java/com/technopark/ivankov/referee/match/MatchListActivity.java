package com.technopark.ivankov.referee.match;

import android.content.Context;
import android.content.Intent;
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
import com.technopark.ivankov.referee.client.Client;
import com.technopark.ivankov.referee.content.MatchList;
import com.technopark.ivankov.referee.login.LoginActivity;

import java.util.List;

/**
 * An activity representing a list of Matches, which when touched,
 * lead to a {@link MatchDetailActivity} representing match details.
 */
public class MatchListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        Client.getMatches(LoginActivity.idVk);

        View matchListRecyclerView = findViewById(R.id.match_list);
        assert matchListRecyclerView != null;
        setupRecyclerView((RecyclerView) matchListRecyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new MatchListRecyclerViewAdapter(MatchList.MATCHES));
    }

    private class MatchListRecyclerViewAdapter
            extends RecyclerView.Adapter<MatchListRecyclerViewAdapter.ViewHolder> {

        private final List<MatchList.Match> mValues;

        private MatchListRecyclerViewAdapter(List<MatchList.Match> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.match_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mMatch = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getTeam1().getName() + " - " + mValues.get(position).getTeam2().getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MatchDetailActivity.class);
                intent.putExtra(MatchDetailFragment.MATCH_ID, holder.mMatch.getIdMatch());

                context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final TextView mContentView;
            private MatchList.Match mMatch;

            private ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
