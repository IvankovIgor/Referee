package com.example.nyashcore.referee;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Button;

import com.example.nyashcore.referee.content.ActionList;
import com.example.nyashcore.referee.content.MatchList;
import com.example.nyashcore.referee.content.PlayerList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An activity representing a single Player detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * player details are presented side-by-side with a list of players
 * in a {@link PlayerListActivity}.
 */
public class PlayerDetailActivity extends AppCompatActivity {

    static int number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Button btnGoal = (Button) findViewById(R.id.btn_goal);
        btnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idTeam = PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getIdTeam();
                MatchList.getCurrentMatch().incrementScore(idTeam);
                Snackbar.make(view, "Goal", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ActionList.addAction(new ActionList.Action(String.valueOf(PlayerListActivity.getTime())+"'", "Goal - " +
                        PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getName(), "goal", idTeam));
                sendInfo("Goal");
            }
        });

        Button btnOwnGoal = (Button) findViewById(R.id.btn_own_goal);
        btnOwnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idTeam = PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getIdTeam();
                MatchList.getCurrentMatch().ownGoal(idTeam);
                Snackbar.make(view, "Own goal", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ActionList.ACTIONS.add(new ActionList.Action(String.valueOf(PlayerListActivity.getTime())+"'", "Own goal - " +
                        PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getName(), "own goal", idTeam));
                sendInfo("Own goal");
            }
        });

        Button btnYellow = (Button) findViewById(R.id.btn_yellow);
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idTeam = PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getIdTeam();
                Snackbar.make(view, "Yellow card", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ActionList.ACTIONS.add(new ActionList.Action(String.valueOf(PlayerListActivity.getTime())+"'", "Yellow card - " +
                        PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getName(), "yellow card", idTeam));
                sendInfo("Yellow card");
            }
        });

        Button btnRed = (Button) findViewById(R.id.btn_red);
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idTeam = PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getIdTeam();
                Snackbar.make(view, "Red card", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ActionList.ACTIONS.add(new ActionList.Action(String.valueOf(PlayerListActivity.getTime())+"'", "Red card - " +
                        PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getName(), "red card", idTeam));
                sendInfo("Red Card");
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(PlayerDetailFragment.ARG_PLAYER_ID,
                    getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID));
            PlayerDetailFragment fragment = new PlayerDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.player_detail_container, fragment)
                    .commit();
        }
    }

    private void sendInfo(String message) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("number", number);
            jsonObject.put("data", message);
            try {
                URL url = new URL("http://185.143.172.172:8080/api-referee/XXXX/set-info");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                OutputStream dStream = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dStream, "utf-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                int responseCode = connection.getResponseCode();
//                System.out.println("Sending 'POST' request to URL : " + url);
//                System.out.println("Post parameters : " + jsonObject);
//                System.out.println("Response Code : " + responseCode);
            } catch (IOException e) {
                System.out.println(e);
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
        number++;
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
//            NavUtils.navigateUpTo(this, new Intent(this, PlayerListActivity.class));
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
