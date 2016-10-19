package com.example.nyashcore.referee;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nyashcore.referee.content.MatchList;

/**
 * A fragment representing a single Match detail screen.
 * This fragment is either contained in a {@link MatchListActivity}
 * in two-pane mode (on tablets) or a {@link MatchDetailActivity}
 * on handsets.
 */
public class MatchDetailFragment extends Fragment {
    /**
     * The fragment argument representing the match ID that this fragment
     * represents.
     */
    public static final String ARG_MATCH_ID = "match_id";

    /**
     * The match content this fragment is presenting.
     */
    private MatchList.Match mMatch;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MatchDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MATCH_ID)) {
            // Load the match content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mMatch = MatchList.MATCH_MAP.get(getArguments().getString(ARG_MATCH_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mMatch.getContent());
//                appBarLayout.setTitle(mMatch.getTournament());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.match_detail, container, false);

        // Show the match content as text in a TextView.
        if (mMatch != null) {
            ((TextView) rootView.findViewById(R.id.match_detail)).setText(mMatch.getDetails());
        }

        return rootView;
    }
}
