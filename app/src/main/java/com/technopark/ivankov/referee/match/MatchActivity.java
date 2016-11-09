package com.technopark.ivankov.referee.match;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.technopark.ivankov.referee.content.Action;
import com.technopark.ivankov.referee.content.MatchList;
import com.technopark.ivankov.referee.content.PlayerList;
import com.technopark.ivankov.referee.client.Client;
import com.technopark.ivankov.referee.R;
import com.technopark.ivankov.referee.content.TeamList;

import java.util.List;

/**
 * An activity representing a chronometer and a list of Actions {@link Action}.
 */
public class MatchActivity extends AppCompatActivity {

    public static final String MATCH_ID = "com.technopark.ivankov.referee.match.MATCH_ID";

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
    private ActionListRecyclerViewAdapter actionListRecyclerViewAdapter;
    private Action.EventType chosenAction;
    private View recyclerViewTeam1;
    private View recyclerViewTeam2;
    private RecyclerView actionListRecyclerView;
    private LinearLayoutManager linearLayoutManager;

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

        timePeriod = getCurrentMatch().getMatchConfig().getTimePeriod() * 1000;
//        timePeriod = 3000L;
        countPeriods = getCurrentMatch().getMatchConfig().getCountPeriods();

        recyclerViewTeam1 = findViewById(R.id.player_list_team1);
        assert recyclerViewTeam1 != null;
        setupRecyclerView((RecyclerView) recyclerViewTeam1, getCurrentMatch().getTeam1());
        recyclerViewTeam1.setVisibility(View.GONE);

        recyclerViewTeam2 = findViewById(R.id.player_list_team2);
        assert recyclerViewTeam2 != null;
        setupRecyclerView((RecyclerView) recyclerViewTeam2, getCurrentMatch().getTeam2());
        recyclerViewTeam2.setVisibility(View.GONE);

        actionListRecyclerView = (RecyclerView) findViewById(R.id.action_list);
        assert actionListRecyclerView != null;
        actionListRecyclerViewAdapter = new ActionListRecyclerViewAdapter(currentMatch.getActionList());
        actionListRecyclerView.setAdapter(actionListRecyclerViewAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        actionListRecyclerView.setLayoutManager(linearLayoutManager);

        final Button btnStart = (Button) findViewById(R.id.btn_start);
        assert btnStart != null;
        btnStart.setOnClickListener(btnStartClick);

        final Button btnEndTime = (Button) findViewById(R.id.btn_reset);
        assert btnEndTime != null;
        btnEndTime.setOnClickListener(btnEndTimeClick);

        final Button btnGoal = (Button) findViewById(R.id.btn_goal);
        assert btnGoal != null;
        btnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act(Action.EventType.GOAL);
            }
        });

        final Button btnOwnGoal = (Button) findViewById(R.id.btn_own_goal);
        assert btnOwnGoal != null;
        btnOwnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act(Action.EventType.OWN_GOAL);
            }
        });

        final Button btnYellow = (Button) findViewById(R.id.btn_yellow);
        assert btnYellow != null;
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act(Action.EventType.YELLOW_CARD);
            }
        });

        final Button btnRed = (Button) findViewById(R.id.btn_red);
        assert btnRed != null;
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act(Action.EventType.RED_CARD);
            }
        });

        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        assert mChronometer != null;
        mChronometer.setOnChronometerTickListener(mChronometerTick);
        if (currentMatch.getMatchStatus() == MatchList.MatchStatus.FINISHED) {
            mChronometer.setVisibility(View.INVISIBLE);
        }

        additionalChronometer = (Chronometer) findViewById(R.id.additional_chronometer);
        additionalChronometer.setVisibility(View.INVISIBLE);

        chosenAction = null;
    }

    private void act(Action.EventType eventType) {
        if (!isStopped) {
            chosenAction = eventType;
            actionListRecyclerView.setVisibility(View.GONE);
            recyclerViewTeam1.setVisibility(View.VISIBLE);
            recyclerViewTeam2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        score.setText(currentMatch.getTeam1Score() + ":" + currentMatch.getTeam2Score());
        if (currentMatch.getMatchStatus() == MatchList.MatchStatus.FINISHED) {
            period.setText("Full time");
        }
    }

    @Override
    public void onBackPressed() {
        if (currentMatch.getMatchStatus() == MatchList.MatchStatus.STARTED) {
            openQuitDialog();
        } else {
            finish();
        }
    }

    private void addAction(View view, Action.EventType event, String idTeam, String idUser) {
        switch (event) {
            case MATCH_END:
            case MATCH_START:
            case TIME_END:
            case TIME_START:
                break;
            default:
                if (!(currentMatch.getMatchStatus() == MatchList.MatchStatus.STARTED)) {
                    Snackbar.make(view, "Not allowed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if (event == Action.EventType.GOAL) {
                    incrementScore(idTeam);
                } else if (event == Action.EventType.OWN_GOAL) {
                    if (currentMatch.getTeam1().getIdTeam().equals(idTeam)) {
                        incrementScore(currentMatch.getTeam2().getIdTeam());
                    } else {
                        incrementScore(currentMatch.getTeam1().getIdTeam());
                    }
                }
                break;
        }

        Client.postAction(new Action(currentMatch.getIdMatch(), idTeam,
                idUser, getTime(), event));
        actionListRecyclerViewAdapter.notifyItemInserted(0);
        linearLayoutManager.scrollToPosition(0);
    }

    public void incrementScore(String idTeam) {
        if (currentMatch.getTeam1().getIdTeam().equals(idTeam)) {
            currentMatch.setTeam1Score(currentMatch.getTeam1Score() + 1);
        } else {
            currentMatch.setTeam2Score(currentMatch.getTeam2Score() + 1);
        }
        score.setText(currentMatch.getTeam1Score() + ":" + currentMatch.getTeam2Score());
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(MatchActivity.this);
        quitDialog.setTitle("Матч будет сброшен, Вы уверены?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentMatch.setTeam1Score(0);
                currentMatch.setTeam2Score(0);
                currentMatch.getActionList().clear();
                currentMatch.getDeletedActionList().clear();
                currentMatch.setMatchStatus(MatchList.MatchStatus.NOT_STARTED);
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

    private void openItemDialog(final Action action, final int position){
        AlertDialog.Builder itemDialog = new AlertDialog.Builder(MatchActivity.this);
        itemDialog.setTitle("Удалить событие?");

        itemDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeAction(action, position);
            }
        });

        itemDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        itemDialog.show();
    }

    private void removeAction(Action action, int position) {
        switch (action.getIdEvent()) {
            case MATCH_END:
            case MATCH_START:
            case TIME_END:
            case TIME_START:
                return;
            case GOAL:
                decrementScore(action.getIdTeam());
                break;
            case OWN_GOAL:
                if (currentMatch.getTeam1().getIdTeam().equals(action.getIdTeam())) {
                    decrementScore(currentMatch.getTeam2().getIdTeam());
                } else {
                    decrementScore(currentMatch.getTeam1().getIdTeam());
                }
                break;
            default:
                break;
        }
        currentMatch.getDeletedActionList().add(action);
        currentMatch.getActionList().remove(action);
        actionListRecyclerViewAdapter.notifyItemRemoved(position);
    }

    private void decrementScore(String idTeam) {
        if (currentMatch.getTeam1().getIdTeam().equals(idTeam)) {
            currentMatch.setTeam1Score(currentMatch.getTeam1Score() - 1);
        } else {
            currentMatch.setTeam2Score(currentMatch.getTeam2Score() - 1);
        }
        score.setText(currentMatch.getTeam1Score() + ":" + currentMatch.getTeam2Score());
    }

    public class ActionListRecyclerViewAdapter
            extends RecyclerView.Adapter<ActionListRecyclerViewAdapter.ViewHolder> {

        private final List<Action> aValues;

        public ActionListRecyclerViewAdapter(List<Action> items) {
            aValues = items;
        }

        @Override
        public ActionListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.action_list_content, parent, false);
            return new ActionListRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ActionListRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.aAction = aValues.get(position);
            holder.aMinute.setText(String.valueOf(aValues.get(position).getMinute()));
            holder.aContentView.setText(aValues.get(position).toString());

            switch (holder.aAction.getIdEvent()) {
                case MATCH_END:
                case MATCH_START:
                case TIME_END:
                case TIME_START:
                    return;
            }
            holder.aView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openItemDialog(holder.aAction, holder.getAdapterPosition());
                }
            });
        }


        @Override
        public int getItemCount() {
            return aValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View aView;
            public final TextView aMinute;
            public final TextView aContentView;
            public Action aAction;

            public ViewHolder(View view) {
                super(view);
                aView = view;
                aMinute = (TextView) view.findViewById(R.id.id);
                aContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + aContentView.getText() + "'";
            }
        }
    }

    public int getTime() {
        String chronometerText = mChronometer.getText().toString();
        String array[] = chronometerText.split(":");
        return Integer.parseInt(array[1]);
    }

    private View.OnClickListener btnStartClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (currentPeriod < countPeriods + 1 && !(currentMatch.getMatchStatus() == MatchList.MatchStatus.FINISHED)) {
                if (currentMatch.getMatchStatus() == MatchList.MatchStatus.NOT_STARTED) {
                    currentMatch.setMatchStatus(MatchList.MatchStatus.STARTED);
                    addAction(view, Action.EventType.MATCH_START, null, null);
//                    actionListRecyclerViewAdapter.notifyItemInserted(0);
                }
                if (isStopped) {
                    additionalChronometer.setVisibility(View.INVISIBLE);
                    addAction(view, Action.EventType.TIME_START, null, null);
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
            if (currentPeriod < countPeriods + 1 && !(currentMatch.getMatchStatus() == MatchList.MatchStatus.FINISHED)) {
                if (!isStopped) {
                    if (isAdditional) {
                        addAction(view, Action.EventType.TIME_END, null, null);
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
                            addAction(view, Action.EventType.MATCH_END, null, null);
                            currentMatch.setMatchStatus(MatchList.MatchStatus.FINISHED);
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
        recyclerView.setAdapter(new PlayerListRecyclerViewAdapter(team.getPlayers(), team));
    }

    public class PlayerListRecyclerViewAdapter
            extends RecyclerView.Adapter<PlayerListRecyclerViewAdapter.ViewHolder> {

        private final List<PlayerList.Player> mValues;
        private final TeamList.Team mTeam;

        public PlayerListRecyclerViewAdapter(List<PlayerList.Player> playerList, TeamList.Team team) {
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
                    assert chosenAction != null;
                    addAction(v, chosenAction, mTeam.getIdTeam(), holder.mPlayer.getIdUser());
                    chosenAction = null;
                    actionListRecyclerView.setVisibility(View.VISIBLE);
                    recyclerViewTeam1.setVisibility(View.GONE);
                    recyclerViewTeam2.setVisibility(View.GONE);
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
