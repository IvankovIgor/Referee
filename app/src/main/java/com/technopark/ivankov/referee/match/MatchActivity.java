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

    private MatchList.Match currentMatch;
    private long timePeriod;
    private int countPeriods;
    private int currentPeriod;
    private Action.EventType chosenAction;
    private boolean isAdditional;
    private Chronometer mChronometer;
    private Chronometer additionalChronometer;
    private Vibrator vibrator;
    private TextView score;
    private TextView period;
    private View team1RecyclerView;
    private View team2RecyclerView;
    private RecyclerView actionListRecyclerView;
    private ActionListRecyclerViewAdapter actionListRecyclerViewAdapter;
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

        currentMatch = MatchList.MATCH_MAP.get(getIntent().getStringExtra(MATCH_ID));
        timePeriod = currentMatch.getMatchConfig().getTimePeriod() * 1000;
        timePeriod = 3000L;
        countPeriods = currentMatch.getMatchConfig().getCountPeriods();
        currentPeriod = 0;
        chosenAction = null;
        isAdditional = false;

        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        assert mChronometer != null;
        mChronometer.setOnChronometerTickListener(mChronometerTick);

        additionalChronometer = (Chronometer) findViewById(R.id.additional_chronometer);
        Context context = getApplicationContext();

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        score = (TextView) findViewById(R.id.score);
        period = (TextView) findViewById(R.id.period);

        team1RecyclerView = findViewById(R.id.team1_player_list);
        assert team1RecyclerView != null;
        setupPlayerListRecyclerView((RecyclerView) team1RecyclerView, currentMatch.getTeam1());
        team1RecyclerView.setVisibility(View.GONE);

        team2RecyclerView = findViewById(R.id.team2_player_list);
        assert team2RecyclerView != null;
        setupPlayerListRecyclerView((RecyclerView) team2RecyclerView, currentMatch.getTeam2());
        team2RecyclerView.setVisibility(View.GONE);

        actionListRecyclerView = (RecyclerView) findViewById(R.id.action_list);
        assert actionListRecyclerView != null;
        actionListRecyclerViewAdapter = new ActionListRecyclerViewAdapter(currentMatch.getActionList());
        actionListRecyclerView.setAdapter(actionListRecyclerViewAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        actionListRecyclerView.setLayoutManager(linearLayoutManager);

        final TextView team1Name = (TextView) findViewById(R.id.team1_name);
        assert team1Name != null;
        team1Name.setText(currentMatch.getTeam1().getName());

        final TextView team2Name = (TextView) findViewById(R.id.team2_name);
        assert team2Name != null;
        team2Name.setText(currentMatch.getTeam2().getName());

        final Button btnStartTime = (Button) findViewById(R.id.btn_start_time);
        assert btnStartTime != null;
        btnStartTime.setOnClickListener(btnStartTimeClick);

        final Button btnFinishTime = (Button) findViewById(R.id.btn_finish_time);
        assert btnFinishTime != null;
        btnFinishTime.setOnClickListener(btnFinishTimeClick);

        final Button btnGoal = (Button) findViewById(R.id.btn_goal);
        assert btnGoal != null;
        btnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act(view, Action.EventType.GOAL);
            }
        });

        final Button btnOwnGoal = (Button) findViewById(R.id.btn_own_goal);
        assert btnOwnGoal != null;
        btnOwnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act(view, Action.EventType.OWN_GOAL);
            }
        });

        final Button btnYellowCard = (Button) findViewById(R.id.btn_yellow_card);
        assert btnYellowCard != null;
        btnYellowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act(view, Action.EventType.YELLOW_CARD);
            }
        });

        final Button btnRedCard = (Button) findViewById(R.id.btn_red_card);
        assert btnRedCard != null;
        btnRedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act(view, Action.EventType.RED_CARD);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        score.setText(currentMatch.getTeam1Score() + ":" + currentMatch.getTeam2Score());
        if (currentMatch.getMatchStatus() == MatchList.MatchStatus.FINISHED) {
            period.setText(R.string.match_finished);
            mChronometer.setVisibility(View.GONE);
            additionalChronometer.setVisibility(View.GONE);
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

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(MatchActivity.this);
        quitDialog.setTitle(R.string.title_quit_dialog);

        quitDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
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

        quitDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }

    private void openItemDialog(final Action action, final int position){
        AlertDialog.Builder itemDialog = new AlertDialog.Builder(MatchActivity.this);
        itemDialog.setTitle(R.string.title_item_dialog);

        itemDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeAction(action, position);
            }
        });

        itemDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        itemDialog.show();
    }

    private void act(View view, Action.EventType eventType) {
        if (currentMatch.getMatchStatus() == MatchList.MatchStatus.STARTED) {
            chosenAction = eventType;
            actionListRecyclerView.setVisibility(View.GONE);
            team1RecyclerView.setVisibility(View.VISIBLE);
            team2RecyclerView.setVisibility(View.VISIBLE);
        } else {
            Snackbar.make(view, R.string.error_not_allowed, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void addAction(View view, Action.EventType event, String idTeam, String idUser) {
        switch (event) {
            case MATCH_FINISHED:
            case MATCH_STARTED:
            case TIME_FINISHED:
            case TIME_STARTED:
                break;
            default:
                if (!(currentMatch.getMatchStatus() == MatchList.MatchStatus.STARTED)) {
                    Snackbar.make(view, R.string.error_not_allowed, Snackbar.LENGTH_LONG)
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

    private void incrementScore(String idTeam) {
        if (currentMatch.getTeam1().getIdTeam().equals(idTeam)) {
            currentMatch.setTeam1Score(currentMatch.getTeam1Score() + 1);
        } else {
            currentMatch.setTeam2Score(currentMatch.getTeam2Score() + 1);
        }
        score.setText(currentMatch.getTeam1Score() + ":" + currentMatch.getTeam2Score());
    }

    private void removeAction(Action action, int position) {
        switch (action.getIdEvent()) {
            case MATCH_FINISHED:
            case MATCH_STARTED:
            case TIME_FINISHED:
            case TIME_STARTED:
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

    private int getTime() {
        String chronometerText = mChronometer.getText().toString();
        String array[] = chronometerText.split(":");
        return Integer.parseInt(array[1]);
    }

    private View.OnClickListener btnStartTimeClick;

    {
        btnStartTimeClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentMatch.getMatchStatus()) {
                    case FINISHED:
                        Snackbar.make(view, R.string.match_finished, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    case NOT_STARTED:
                        addAction(view, Action.EventType.MATCH_STARTED, null, null);
                    case BREAK:
                        addAction(view, Action.EventType.TIME_STARTED, null, null);
                        currentMatch.setMatchStatus(MatchList.MatchStatus.STARTED);
                        additionalChronometer.setBase(SystemClock.elapsedRealtime());
                        mChronometer.setBase(SystemClock.elapsedRealtime() - timePeriod * currentPeriod);
                        mChronometer.start();
                        currentPeriod++;
                        assert period != null;
                        period.setText(String.valueOf(currentPeriod));
                        break;
                    case STARTED:
                        Snackbar.make(view, R.string.error_already_started, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private View.OnClickListener btnFinishTimeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (currentMatch.getMatchStatus()) {
                case FINISHED:
                    Snackbar.make(view, R.string.match_finished, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                case NOT_STARTED:
                    Snackbar.make(view, R.string.error_not_started, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                case BREAK:
                    Snackbar.make(view, R.string.error_already_stopped, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                case STARTED:
                    if (!isAdditional) {
                        Snackbar.make(view, R.string.error_too_early, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    } else {
                        addAction(view, Action.EventType.TIME_FINISHED, null, null);
                        assert period != null;
                        period.setText(R.string.match_break);
                        currentMatch.setMatchStatus(MatchList.MatchStatus.BREAK);
                        isAdditional = false;
                        mChronometer.stop();
                        mChronometer.setBase(SystemClock.elapsedRealtime() - timePeriod * currentPeriod);
                        additionalChronometer.stop();
                        if (currentPeriod == countPeriods) {
                            addAction(view, Action.EventType.MATCH_FINISHED, null, null);
                            currentMatch.setMatchStatus(MatchList.MatchStatus.FINISHED);
                            period.setText(R.string.match_finished);
                        }
                    }
                default:
                    break;
            }
        }
    };

    private Chronometer.OnChronometerTickListener mChronometerTick = new Chronometer.OnChronometerTickListener(){
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            long elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
            if (elapsedMillis > timePeriod * (currentPeriod)) {
                isAdditional = true;

                mChronometer.stop();
                mChronometer.setBase(SystemClock.elapsedRealtime() - timePeriod * (currentPeriod));

                additionalChronometer.setBase(SystemClock.elapsedRealtime());
                additionalChronometer.start();

                vibrator.vibrate(500);
            }
        }
    };

    private class ActionListRecyclerViewAdapter
            extends RecyclerView.Adapter<ActionListRecyclerViewAdapter.ViewHolder> {

        private final List<Action> aValues;

        private ActionListRecyclerViewAdapter(List<Action> items) {
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
                case MATCH_FINISHED:
                case MATCH_STARTED:
                case TIME_FINISHED:
                case TIME_STARTED:
                    return;
            }
            holder.aView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(currentMatch.getMatchStatus() == MatchList.MatchStatus.FINISHED)) {
                        openItemDialog(holder.aAction, holder.getAdapterPosition());
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return aValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final View aView;
            private final TextView aMinute;
            private final TextView aContentView;
            private Action aAction;

            private ViewHolder(View view) {
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

    private void setupPlayerListRecyclerView(@NonNull RecyclerView recyclerView, TeamList.Team team) {
        recyclerView.setAdapter(new PlayerListRecyclerViewAdapter(team.getPlayers(), team));
    }

    private class PlayerListRecyclerViewAdapter
            extends RecyclerView.Adapter<PlayerListRecyclerViewAdapter.ViewHolder> {

        private final List<PlayerList.Player> mValues;
        private final TeamList.Team mTeam;

        private PlayerListRecyclerViewAdapter(List<PlayerList.Player> playerList, TeamList.Team team) {
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
                    team1RecyclerView.setVisibility(View.GONE);
                    team2RecyclerView.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final TextView mIdView;
            private final TextView mContentView;
            private PlayerList.Player mPlayer;

            private ViewHolder(View view) {
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
