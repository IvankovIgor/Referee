package com.technopark.ivankov.referee.match;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.pm.ActivityInfo;

import com.technopark.ivankov.referee.BuildConfig;
import com.technopark.ivankov.referee.Constants;
import com.technopark.ivankov.referee.R;
import com.technopark.ivankov.referee.client.Client;
import com.technopark.ivankov.referee.content.MatchList;
import com.technopark.ivankov.referee.content.PlayerList;
import com.technopark.ivankov.referee.content.TeamList;
import com.vk.sdk.VKAccessToken;

import java.util.List;

/**
 * An activity representing a list of Matches, which when touched,
 * lead to a {@link MatchActivity}.
 */
public class MatchListActivity extends AppCompatActivity implements Constants {

    private static final String TAG = MatchListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        int idVk = Integer.parseInt(VKAccessToken.tokenFromSharedPreferences(
                this, VK_ACCESS_TOKEN).userId);
        new Client(this).getMatches(idVk);

        View matchListRecyclerView = findViewById(R.id.match_list);
        assert matchListRecyclerView != null;
        setupRecyclerView((RecyclerView) matchListRecyclerView);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Created.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MatchList.MATCHES.clear();
        MatchList.MATCH_MAP.clear();
        PlayerList.PLAYER_MAP.clear();
        TeamList.TEAM_MAP.clear();
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Destroyed.");
        }
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
            holder.mTeamNamesView.setText(holder.mMatch.getTeam1().getName() + " - " +
                    holder.mMatch.getTeam2().getName());
//            holder.mTeam1LogoView.setImageResource(R.drawable.ball_icon);
//            holder.mTeam2LogoView.setImageResource(R.drawable.ball_icon);
            holder.mMatchInfoView.setText(holder.mMatch.toString());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(MatchListActivity.this, MatchActivity.class);
                intent.putExtra(MatchActivity.MATCH_ID, holder.mMatch.getIdMatch());
                startActivity(intent);
//                Context context = v.getContext();
//                Intent intent = new Intent(context, MatchDetailActivity.class);
//                intent.putExtra(MatchDetailFragment.MATCH_ID, holder.mMatch.getIdMatch());
//
//                context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final CardView mView;
            private final TextView mTeamNamesView;
//            private final ImageView mTeam1LogoView;
//            private final ImageView mTeam2LogoView;
            private final TextView mMatchInfoView;
            private MatchList.Match mMatch;

            private ViewHolder(View view) {
                super(view);
                mView = (CardView) view.findViewById(R.id.cv);
                mTeamNamesView = (TextView) view.findViewById(R.id.team_names);
//                mTeam1LogoView = (ImageView) view.findViewById(R.id.team1_logo);
//                mTeam2LogoView = (ImageView) view.findViewById(R.id.team2_logo);
                mMatchInfoView = (TextView) view.findViewById(R.id.match_info);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTeamNamesView.getText() + "'";
            }
        }
    }
}
