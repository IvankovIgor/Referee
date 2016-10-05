package com.example.nyashcore.referee;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Chronometer;
import android.os.SystemClock;


import com.example.nyashcore.referee.content.PlayerList;

import java.util.List;

/**
 * An activity representing a list of Players. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of players, which when touched,
 * lead to a {@link PlayerDetailActivity} representing
 * player details. On tablets, the activity presents the list of players and
 * player details side-by-side using two vertical panes.
 */
public class PlayerListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Chronometer mChronometer;
    long timeWhenStopped = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mChronometer = (Chronometer) findViewById(R.id.chronometer);
//        mChronometer.setTextSize(120);
//        mChronometer.setFormat("MM:SS");


        Button btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                mChronometer.start();
            }
        });

        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeWhenStopped = mChronometer.getBase() - SystemClock.elapsedRealtime();
                mChronometer.stop();
            }
        });

        Button btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;
            }
        });

        View recyclerView = findViewById(R.id.player_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView, 1);

        View recyclerView2 = findViewById(R.id.player_list2);
        assert recyclerView2 != null;
        setupRecyclerView((RecyclerView) recyclerView2, 2);

        if (findViewById(R.id.player_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, int team) {
        if(team == 1) {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(PlayerList.PLAYERS));
        } else {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(PlayerList.PLAYERS2));
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<PlayerList.Player> mValues;

        public SimpleItemRecyclerViewAdapter(List<PlayerList.Player> players) {
            mValues = players;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.player_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mPlayer = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(PlayerDetailFragment.ARG_PLAYER_ID, holder.mPlayer.id);
                        PlayerDetailFragment fragment = new PlayerDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.player_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, PlayerDetailActivity.class);
                        intent.putExtra(PlayerDetailFragment.ARG_PLAYER_ID, holder.mPlayer.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public PlayerList.Player mPlayer;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
