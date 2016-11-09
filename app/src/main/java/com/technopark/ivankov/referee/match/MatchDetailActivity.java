package com.technopark.ivankov.referee.match;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Button;

import com.technopark.ivankov.referee.R;

/**
 * An activity representing a single Match detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * match details are presented side-by-side with a list of matches
 * in a {@link MatchListActivity}.
 */
public class MatchDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Button btnToMatch = (Button) findViewById(R.id.to_match);
        btnToMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MatchDetailActivity.this, MatchActivity.class);
                intent.putExtra(MatchActivity.MATCH_ID, getIntent().getStringExtra(MatchDetailFragment.MATCH_ID));
                startActivity(intent);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(MatchDetailFragment.MATCH_ID,
                    getIntent().getStringExtra(MatchDetailFragment.MATCH_ID));
            MatchDetailFragment fragment = new MatchDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.match_detail_container, fragment)
                    .commit();
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
            NavUtils.navigateUpTo(this, new Intent(this, MatchListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
