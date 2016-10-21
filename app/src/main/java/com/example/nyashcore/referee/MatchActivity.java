package com.example.nyashcore.referee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import android.os.Vibrator;
import android.content.pm.ActivityInfo;

import com.example.nyashcore.referee.content.ActionList;
import com.example.nyashcore.referee.content.MatchList;
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
public class MatchActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    final private int vibrateTime = 500;
    private static Chronometer mChronometer;
    private static Chronometer additionalChronometer;
    private Vibrator vibrator;
    private long timeWhenStopped = 0L;
    private long timeWhenAdditionalStopped = 0L;
    private boolean isAdditional = false;
    private boolean isStopped = true;
    private static TextView score;
    private long timePeriod;
    private int countPeriods;
    private int currentPeriod = 1;
    public static ActionList actionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (!MatchList.getCurrentMatch().isStarted()) {
            actionList = new ActionList();
        } else {
            actionList = MatchList.getCurrentMatch().getActionList();
        }

        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        additionalChronometer = (Chronometer) findViewById(R.id.additional_chronometer);
        Context context = getApplicationContext();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        score = (TextView) findViewById(R.id.score);
        TextView firstTeamName = (TextView) findViewById(R.id.team1);
        TextView secondTeamName = (TextView) findViewById(R.id.team2);
        TextView actions = (TextView) findViewById(R.id.actions);
        final TextView period = (TextView) findViewById(R.id.period);
        assert firstTeamName != null;
        firstTeamName.setText(MatchList.getCurrentMatch().getFirstTeam().getName());
        assert secondTeamName != null;
        secondTeamName.setText(MatchList.getCurrentMatch().getSecondTeam().getName());
//        timePeriod = MatchList.getCurrentMatch().getTimePeriod();
        timePeriod = 3000L;
        countPeriods = MatchList.getCurrentMatch().getCountPeriods();
//        countPeriods = 3;
        score.setText(MatchList.getCurrentMatch().getFirstScore() + ":" + MatchList.getCurrentMatch().getSecondScore());
        refresh();
        System.out.println(MatchList.getCurrentMatch().getId());

        actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MatchActivity.this, ActionListActivity.class);
                startActivity(intent);
            }
        });

        final Button btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPeriod < countPeriods + 1) {
                    if (!MatchList.getCurrentMatch().isStarted()) {
                        MatchList.getCurrentMatch().setStarted();
                        PlayerDetailActivity.sendInfo("start");
                    }
                    if (isStopped) {
                        isStopped = false;
                        if (!isAdditional) {
                            period.setText("Period " + currentPeriod);
                            mChronometer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
                            mChronometer.start();
                        } else {
                            additionalChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenAdditionalStopped);
                            additionalChronometer.start();
                        }
                    }
                } else {
                    Snackbar.make(view, "Full time", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        Button btnEndTime = (Button) findViewById(R.id.btn_reset);
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPeriod < countPeriods + 1) {
                    if (!isStopped) {
                        period.setText("Break");
                        isStopped = true;
                        isAdditional = false;
                        timeWhenStopped = timePeriod * (currentPeriod);
                        timeWhenAdditionalStopped = 0;
                        mChronometer.stop();
                        mChronometer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
                        additionalChronometer.stop();
                        currentPeriod++;
                        if (currentPeriod > countPeriods) {
                            PlayerDetailActivity.sendInfo("finish");
                            MatchList.getCurrentMatch().setFinished();
                            period.setText("Full time");
                            additionalChronometer.setBase(SystemClock.elapsedRealtime());
                            Snackbar.make(view, "Full time", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                } else {
                    Snackbar.make(view, "Full time", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        mChronometer.setOnChronometerTickListener(
            new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
                if (elapsedMillis > timePeriod * (currentPeriod)) {
                    isAdditional = true;
                    timeWhenAdditionalStopped = 0;

                    mChronometer.stop();
                    mChronometer.setBase(SystemClock.elapsedRealtime() - timePeriod * (currentPeriod));

                    additionalChronometer.setBase(SystemClock.elapsedRealtime() - timeWhenAdditionalStopped);
                    additionalChronometer.start();

                    vibrator.vibrate(vibrateTime);
                }
            }}
        );

        View recyclerView = findViewById(R.id.player_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView, MatchList.getCurrentMatch().getFirstTeam());

        View recyclerView2 = findViewById(R.id.player_list2);
        assert recyclerView2 != null;
        setupRecyclerView((RecyclerView) recyclerView2, MatchList.getCurrentMatch().getSecondTeam());

        if (findViewById(R.id.player_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    public static void refresh() {
        score.setText(MatchList.getCurrentMatch().getFirstScore() + ":" + MatchList.getCurrentMatch().getSecondScore());
    }

    static long getTime() {
        String chronoText = mChronometer.getText().toString();
        String array[] = chronoText.split(":");
        return Integer.parseInt(array[1]);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, PlayerList.Team team) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(team.getPLAYERS()));
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
                    .inflate(R.layout.match_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mPlayer = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getNumber());
            holder.mContentView.setText(mValues.get(position).getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(PlayerDetailFragment.ARG_PLAYER_ID, holder.mPlayer.getId());
                        PlayerDetailFragment fragment = new PlayerDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.player_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, PlayerDetailActivity.class);
                        intent.putExtra(PlayerDetailFragment.ARG_PLAYER_ID, holder.mPlayer.getId());

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
