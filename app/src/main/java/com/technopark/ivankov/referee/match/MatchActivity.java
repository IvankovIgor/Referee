package com.technopark.ivankov.referee.match;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.technopark.ivankov.referee.action_list.ActionListActivity;
import com.technopark.ivankov.referee.https_client.HttpsClient;
import com.technopark.ivankov.referee.R;
import com.technopark.ivankov.referee.content.ActionList;
import com.technopark.ivankov.referee.content.MatchList;
import com.technopark.ivankov.referee.content.PlayerList;
import com.technopark.ivankov.referee.content.TeamList;

import java.util.ArrayList;
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
    public static List<ActionList.Action> actionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        if (!MatchList.getCurrentMatch().isStarted()) {
            actionList = new ArrayList<>();
        } else {
            actionList = MatchList.getCurrentMatch().getActions();
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
        firstTeamName.setText(MatchList.getCurrentMatch().getTeam1().getName());
        assert secondTeamName != null;
        secondTeamName.setText(MatchList.getCurrentMatch().getTeam2().getName());
        timePeriod = MatchList.getCurrentMatch().getMatchConfig().getTimePeriod() * 60 * 1000;
        timePeriod = 3000L;
        countPeriods = MatchList.getCurrentMatch().getMatchConfig().getCountPeriods();
        score.setText(MatchList.getCurrentMatch().getTeam1Score() + ":" + MatchList.getCurrentMatch().getTeam2Score());
        refresh();

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
                        HttpsClient.postAction(new ActionList.Action(MatchList.getCurrentMatchId(), null, null, MatchActivity.getTime(), ActionList.EventType.MATCH_START));
                    }
                    if (isStopped) {
                        HttpsClient.postAction(new ActionList.Action(MatchList.getCurrentMatchId(), null, null, MatchActivity.getTime(), ActionList.EventType.TIME_START));
                        isStopped = false;
                        if (!isAdditional) {
                            assert period != null;
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
                        HttpsClient.postAction(new ActionList.Action(MatchList.getCurrentMatchId(), null, null, MatchActivity.getTime(), ActionList.EventType.TIME_END));
                        assert period != null;
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
                            HttpsClient.postAction(new ActionList.Action(MatchList.getCurrentMatchId(), null, null, MatchActivity.getTime(), ActionList.EventType.MATCH_END));
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
        setupRecyclerView((RecyclerView) recyclerView, MatchList.getCurrentMatch().getTeam1());

        View recyclerView2 = findViewById(R.id.player_list2);
        assert recyclerView2 != null;
        setupRecyclerView((RecyclerView) recyclerView2, MatchList.getCurrentMatch().getTeam2());
    }

    public static void refresh() {
        score.setText(MatchList.getCurrentMatch().getTeam1Score() + ":" + MatchList.getCurrentMatch().getTeam2Score());
    }

    static int getTime() {
        String chronoText = mChronometer.getText().toString();
        String array[] = chronoText.split(":");
        return Integer.parseInt(array[1]);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, TeamList.Team team) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(team.getPlayers()));
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
            holder.mIdView.setText(Integer.toString(mValues.get(position).getNumber()));
            holder.mContentView.setText(mValues.get(position).getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, PlayerDetailActivity.class);
                intent.putExtra(PlayerDetailFragment.ARG_PLAYER_ID, holder.mPlayer.getIdUser());

                context.startActivity(intent);
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
