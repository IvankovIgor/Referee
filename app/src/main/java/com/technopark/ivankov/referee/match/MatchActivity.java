package com.technopark.ivankov.referee.match;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.technopark.ivankov.referee.content.Action;
import com.technopark.ivankov.referee.client.Client;
import com.technopark.ivankov.referee.R;
import com.technopark.ivankov.referee.content.MatchList;
import com.technopark.ivankov.referee.content.PlayerList;
import com.technopark.ivankov.referee.content.TeamList;

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

    public static final String MATCH_ID = "com.technopark.ivankov.referee.match.MATCH_ID";
    public static final String TEAM_ID = "com.technopark.ivankov.referee.match.TEAM_ID";
    public static final String PLAYER_ID = "com.technopark.ivankov.referee.match.PLAYER_ID";
    public static final String MINUTE = "com.technopark.ivankov.referee.match.MINUTE";

    private String currentMatchId;
    private MatchList.Match currentMatch;
    private Chronometer mChronometer;
    private Chronometer additionalChronometer;
    private Vibrator vibrator;
    private long timeWhenStopped = 0L;
    private long timeWhenAdditionalStopped = 0L;
    private boolean isAdditional = false;
    private boolean isStopped = true;
    private TextView score;
    private TextView period;
    private long timePeriod;
    private int countPeriods;
    private int currentPeriod = 1;
    public static List<Action> actionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        currentMatchId = getIntent().getStringExtra(MATCH_ID);
        currentMatch = MatchList.MATCH_MAP.get(currentMatchId);
        actionList = getCurrentMatch().getActionList();

        Context context = getApplicationContext();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        score = (TextView) findViewById(R.id.score);
        score.setText(getCurrentMatch().getTeam1Score() + ":" + getCurrentMatch().getTeam2Score());

        period = (TextView) findViewById(R.id.period);

        TextView firstTeamName = (TextView) findViewById(R.id.team1);
        assert firstTeamName != null;
        firstTeamName.setText(getCurrentMatch().getTeam1().getName());

        TextView secondTeamName = (TextView) findViewById(R.id.team2);
        assert secondTeamName != null;
        secondTeamName.setText(getCurrentMatch().getTeam2().getName());

        timePeriod = getCurrentMatch().getMatchConfig().getTimePeriod() * 60 * 1000;
        timePeriod = 3000L;
        countPeriods = getCurrentMatch().getMatchConfig().getCountPeriods();

        final Button actions = (Button) findViewById(R.id.actions);
        assert actions != null;
        actions.setOnClickListener(actionsClick);

        final Button btnStart = (Button) findViewById(R.id.btn_start);
        assert btnStart != null;
        btnStart.setOnClickListener(btnStartClick);

        final Button btnEndTime = (Button) findViewById(R.id.btn_reset);
        assert btnEndTime != null;
        btnEndTime.setOnClickListener(btnEndTimeClick);

        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        assert mChronometer != null;
        mChronometer.setOnChronometerTickListener(mChronometerTick);
        if (currentMatch.isFinished()) {
            mChronometer.setVisibility(View.INVISIBLE);
        }

        additionalChronometer = (Chronometer) findViewById(R.id.additional_chronometer);
        additionalChronometer.setVisibility(View.INVISIBLE);

        View recyclerViewTeam1 = findViewById(R.id.player_list_team1);
        assert recyclerViewTeam1 != null;
        setupRecyclerView((RecyclerView) recyclerViewTeam1, getCurrentMatch().getTeam1());

        View recyclerViewTeam2 = findViewById(R.id.player_list_team2);
        assert recyclerViewTeam2 != null;
        setupRecyclerView((RecyclerView) recyclerViewTeam2, getCurrentMatch().getTeam2());
    }

    @Override
    public void onStart() {
        super.onStart();
        score.setText(currentMatch.getTeam1Score() + ":" + currentMatch.getTeam2Score());
        if (currentMatch.isFinished()) {
            period.setText("Full time");
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        if (!currentMatch.isFinished() && currentMatch.isStarted()) {
            openQuitDialog();
        } else {
            finish();
        }
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                MatchActivity.this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentMatch.setFinished(false);
                currentMatch.setStarted(false);
                currentMatch.setTeam1Score(0);
                currentMatch.setTeam2Score(0);
                currentMatch.getActionList().clear();
                finish();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }

    public int getTime() {
        String chronoText = mChronometer.getText().toString();
        String array[] = chronoText.split(":");
        return Integer.parseInt(array[1]);
    }

    private View.OnClickListener actionsClick =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MatchActivity.this, ActionListActivity.class);
            intent.putExtra(MATCH_ID, currentMatchId);
            startActivity(intent);
        }
    };

    private View.OnClickListener btnStartClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (currentPeriod < countPeriods + 1 && !currentMatch.isFinished()) {
                if (!getCurrentMatch().isStarted()) {
                    getCurrentMatch().setStarted(true);
                    Client.postAction(new Action(getCurrentMatchId(), null, null, getTime(), Action.EventType.MATCH_START));
                }
                if (isStopped) {
                    additionalChronometer.setVisibility(View.INVISIBLE);
                    Client.postAction(new Action(getCurrentMatchId(), null, null, getTime(), Action.EventType.TIME_START));
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
    };

    private View.OnClickListener btnEndTimeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (currentPeriod < countPeriods + 1 && !currentMatch.isFinished()) {
                if (!isStopped) {
                    if (isAdditional) {
                        Client.postAction(new Action(getCurrentMatchId(), null, null, getTime(), Action.EventType.TIME_END));
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
                            Client.postAction(new Action(getCurrentMatchId(), null, null, getTime(), Action.EventType.MATCH_END));
                            getCurrentMatch().setFinished(true);
                            period.setText("Full time");
//                            additionalChronometer.setBase(SystemClock.elapsedRealtime());
//                            additionalChronometer.setVisibility(View.INVISIBLE);
                            Snackbar.make(view, "Full time", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        Snackbar.make(view, "Too early", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(view, "Match is stopped", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            } else {
                Snackbar.make(view, "Full time", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    };

    private Chronometer.OnChronometerTickListener mChronometerTick = new Chronometer.OnChronometerTickListener(){
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            long elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
            if (elapsedMillis > timePeriod * (currentPeriod)) {
                isAdditional = true;
                additionalChronometer.setVisibility(View.VISIBLE);
                timeWhenAdditionalStopped = 0;

                mChronometer.stop();
                mChronometer.setBase(SystemClock.elapsedRealtime() - timePeriod * (currentPeriod));

                additionalChronometer.setBase(SystemClock.elapsedRealtime() - timeWhenAdditionalStopped);
                additionalChronometer.start();

                int vibrateTime = 500;
                vibrator.vibrate(vibrateTime);
            }
        }
    };

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, TeamList.Team team) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(team.getPlayers(), team));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<PlayerList.Player> mValues;
        private final TeamList.Team mTeam;

        public SimpleItemRecyclerViewAdapter(List<PlayerList.Player> playerList, TeamList.Team team) {
            mValues = playerList;
            mTeam = team;
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
            holder.mIdView.setText(String.valueOf(mTeam.getNumberMap().get(mValues.get(position).getIdUser())));
            holder.mContentView.setText(mValues.get(position).getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, PlayerDetailActivity.class);
                intent.putExtra(MATCH_ID, currentMatchId);
                intent.putExtra(PLAYER_ID, holder.mPlayer.getIdUser());
                intent.putExtra(TEAM_ID, currentMatch.getTeam1().getPlayers()
                                .contains(holder.mPlayer) ? currentMatch.getTeam1().getIdTeam() :
                                currentMatch.getTeam2().getIdTeam());
                intent.putExtra(MINUTE, getTime());

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

    public String getCurrentMatchId() { return currentMatchId; }

    public void setCurrentMatchId(String currentMatchId) {  this.currentMatchId = currentMatchId; }

    public MatchList.Match getCurrentMatch() { return currentMatch; }

    public void setCurrentMatch(MatchList.Match currentMatch) { this.currentMatch = currentMatch; }

}
