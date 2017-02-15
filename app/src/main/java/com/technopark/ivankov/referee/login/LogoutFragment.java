package com.technopark.ivankov.referee.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.technopark.ivankov.referee.BuildConfig;
import com.technopark.ivankov.referee.Constants;
import com.technopark.ivankov.referee.R;
import com.technopark.ivankov.referee.Referee;
import com.technopark.ivankov.referee.match.MatchListActivity;
import com.vk.sdk.VKSdk;

public class LogoutFragment extends android.support.v4.app.Fragment implements Constants {

    private static final String TAG = LogoutFragment.class.getSimpleName();

    private TextView curIP;
    private TextView curPort;
    private EditText editIP;
    private EditText editPort;

    public LogoutFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logout, container, false);

        final Button btnToMatchList = (Button) v.findViewById(R.id.to_match_list);
        btnToMatchList.setText(R.string.btn_to_match_list);
        btnToMatchList.setOnClickListener(btnToMatchListClick);

        final Button btnLogout = (Button) v.findViewById(R.id.logout);
        btnLogout.setText(R.string.btn_logout);
        btnLogout.setOnClickListener(btnLogoutClick);

        final Button btnSetDefaults = (Button) v.findViewById(R.id.btn_set_defaults);

        curIP = (TextView) v.findViewById(R.id.curIP);

        curPort = (TextView) v.findViewById(R.id.curPort);

        editIP = (EditText) v.findViewById(R.id.editIP);

        editPort = (EditText) v.findViewById(R.id.editPort);

        if (BuildConfig.DEBUG) {
            btnSetDefaults.setText(R.string.btn_set_defaults);
            btnSetDefaults.setOnClickListener(btnSetDefaultsClick);
            curIP.setText(Referee.serverPreferences.getString(SERVER_IP, "185.143.172.172"));
            curPort.setText(Referee.serverPreferences.getString(SERVER_PORT, "8080"));
            editIP.setOnKeyListener(editIPKeyEvent);
            editPort.setOnKeyListener(editPortKeyEvent);
        } else {
            btnSetDefaults.setVisibility(View.GONE);
            curIP.setVisibility(View.GONE);
            curPort.setVisibility(View.GONE);
            editIP.setVisibility(View.GONE);
            editPort.setVisibility(View.GONE);
        }

        return v;
    }

    private View.OnClickListener btnToMatchListClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), MatchListActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener btnLogoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            VKSdk.logout();
            if (!VKSdk.isLoggedIn()) {
                ((LoginActivity) getActivity()).showLogin();
            }
        }
    };

    private View.OnClickListener btnSetDefaultsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences.Editor editor = Referee.serverPreferences.edit();
            editor.putString(SERVER_IP, "185.143.172.172");
            editor.putString(SERVER_PORT, "8080");
            editor.apply();
            curIP.setText(R.string.default_ip);
            curPort.setText(R.string.default_port);
            editor.apply();
        }
    };

    private View.OnKeyListener editIPKeyEvent = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                SharedPreferences.Editor editor = Referee.serverPreferences.edit();
                editor.putString(SERVER_IP, editIP.getText().toString());
                editor.apply();
                curIP.setText(editIP.getText().toString());
                return true;
            }
            return false;
        }
    };

    private View.OnKeyListener editPortKeyEvent = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                SharedPreferences.Editor editor = Referee.serverPreferences.edit();
                editor.putString(SERVER_PORT, editPort.getText().toString());
                editor.apply();
                curPort.setText(editPort.getText().toString());
                return true;
            }
            return false;
        }
    };
}