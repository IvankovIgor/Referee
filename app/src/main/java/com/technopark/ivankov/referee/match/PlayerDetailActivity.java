package com.technopark.ivankov.referee.match;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Button;

import com.technopark.ivankov.referee.content.Action;
import com.technopark.ivankov.referee.content.MatchList;
import com.technopark.ivankov.referee.content.PlayerList;
import com.technopark.ivankov.referee.content.TeamList;
import com.technopark.ivankov.referee.https_client.HttpsClient;
import com.technopark.ivankov.referee.R;

/**
 * An activity representing a single Player detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * player details are presented side-by-side with a list of players
 * in a {@link MatchActivity}.
 */
public class PlayerDetailActivity extends AppCompatActivity {

    private MatchList.Match mMatch;
    private TeamList.Team mTeam;
    private PlayerList.Player mPlayer;
    private Integer mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mMatch = MatchList.MATCH_MAP.get(getIntent().getStringExtra(MatchActivity.MATCH_ID));
        mTeam = TeamList.TEAM_MAP.get(getIntent().getStringExtra(MatchActivity.TEAM_ID));
        mPlayer = PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(MatchActivity.PLAYER_ID));
        mMinute = getIntent().getIntExtra(MatchActivity.MINUTE, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Button btnGoal = (Button) findViewById(R.id.btn_goal);
        assert btnGoal != null;
        btnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAction(view, Action.EventType.GOAL);
            }
        });

        Button btnOwnGoal = (Button) findViewById(R.id.btn_own_goal);
        assert btnOwnGoal != null;
        btnOwnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAction(view, Action.EventType.OWN_GOAL);
            }
        });

        Button btnYellow = (Button) findViewById(R.id.btn_yellow);
        assert btnYellow != null;
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAction(view, Action.EventType.YELLOW_CARD);
            }
        });

        Button btnRed = (Button) findViewById(R.id.btn_red);
        assert btnRed != null;
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAction(view, Action.EventType.RED_CARD);
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(MatchActivity.PLAYER_ID,
                    getIntent().getStringExtra(MatchActivity.PLAYER_ID));
            arguments.putString(MatchActivity.TEAM_ID,
                    getIntent().getStringExtra(MatchActivity.TEAM_ID));
            PlayerDetailFragment fragment = new PlayerDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.player_detail_container, fragment)
                    .commit();
        }
    }

    private void addAction(View view, Action.EventType event) {
        if (!mMatch.isStarted() || mMatch.isFinished()) {
            Snackbar.make(view, "Not allowed", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            return;
        }

        if (event == Action.EventType.GOAL) {
            incrementScore(mTeam);
        } else if (event == Action.EventType.OWN_GOAL) {
            if (mMatch.getTeam1().equals(mTeam)) {
                incrementScore(mMatch.getTeam2());
            } else {
                incrementScore(mMatch.getTeam1());
            }
        }

        Snackbar.make(view, String.valueOf(event), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        HttpsClient.postAction(new Action(mMatch.getIdMatch(), mTeam.getIdTeam(), mPlayer.getIdUser(), mMinute, event));
    }

    public void incrementScore(TeamList.Team team) {
        if (mMatch.getTeam1().equals(team)) {
            mMatch.setTeam1Score(mMatch.getTeam1Score() + 1);
        } else {
            mMatch.setTeam2Score(mMatch.getTeam2Score() + 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
//            NavUtils.navigateUpTo(this, new Intent(this, MatchActivity.class));
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
