package com.technopark.ivankov.referee;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technopark.ivankov.referee.content.PlayerList;

/**
 * A fragment representing a single Player detail screen.
 * This fragment is either contained in a {@link MatchActivity}
 * in two-pane mode (on tablets) or a {@link PlayerDetailActivity}
 * on handsets.
 */
public class PlayerDetailFragment extends Fragment {
    /**
     * The fragment argument representing the player ID that this fragment
     * represents.
     */
    public static final String ARG_PLAYER_ID = "player_id";

    /**
     * The player content this fragment is presenting.
     */
    private PlayerList.Player mPlayer;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayerDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_PLAYER_ID)) {
            // Load the player content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mPlayer = PlayerList.PLAYER_MAP.get(getArguments().getString(ARG_PLAYER_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mPlayer.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_detail, container, false);

        // Show the player content as text in a TextView.
        if (mPlayer != null) {
            ((TextView) rootView.findViewById(R.id.player_detail)).setText(mPlayer.getNumber() + " " + mPlayer.getName());
        }

        return rootView;
    }
}
