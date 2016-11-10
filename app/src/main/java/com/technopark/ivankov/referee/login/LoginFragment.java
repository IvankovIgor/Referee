package com.technopark.ivankov.referee.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.technopark.ivankov.referee.match.MatchListActivity;
import com.technopark.ivankov.referee.BuildConfig;
import com.technopark.ivankov.referee.R;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import java.util.List;

public class LoginFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener, OnRequestSocialPersonCompleteListener {

    public static SocialNetworkManager mSocialNetworkManager;
    private static final String TAG = LoginFragment.class.getSimpleName();
    /**
     * SocialNetwork Ids in ASNE:
     * 1 - Twitter
     * 2 - LinkedIn
     * 3 - Google Plus
     * 4 - Facebook
     * 5 - Vkontakte
     * 6 - Odnoklassniki
     * 7 - Instagram
     */
    private Button btnLogin;
    private Button btnToMatchList;
    private Button btnLogout;
    private TextView curIP;
    private TextView curPort;
    private EditText editIP;
    private EditText editPort;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        btnLogin = (Button) rootView.findViewById(R.id.login);
        btnLogin.setText(R.string.btn_login);
        btnLogin.setOnClickListener(btnLoginClick);

        btnToMatchList = (Button) rootView.findViewById(R.id.to_match_list);
        btnToMatchList.setText(R.string.btn_to_match_list);
        btnToMatchList.setOnClickListener(btnToMatchListClick);

        btnLogout = (Button) rootView.findViewById(R.id.logout);
        btnLogout.setText(R.string.btn_logout);
        btnLogout.setOnClickListener(btnLogoutClick);

        Button btnSetDefaults = (Button) rootView.findViewById(R.id.btn_set_defaults);
        btnSetDefaults.setText(R.string.btn_set_defaults);
        btnSetDefaults.setOnClickListener(btnSetDefaultsClick);

        curIP = (TextView) rootView.findViewById(R.id.curIP);
        curIP.setText(LoginActivity.serverIP);

        curPort = (TextView) rootView.findViewById(R.id.curPort);
        curPort.setText(LoginActivity.serverPort);

        editIP = (EditText) rootView.findViewById(R.id.editIP);
        editIP.setOnKeyListener(editIPKeyEvent);

        editPort = (EditText) rootView.findViewById(R.id.editPort);
        editPort.setOnKeyListener(editPortKeyEvent);

        //Get Keys for initiate SocialNetworks
        String VK_KEY = getActivity().getString(R.string.vk_app_id);

        //Chose permissions
        String[] vkScope = new String[]{
                VKScope.FRIENDS,
                VKScope.WALL,
                VKScope.PHOTOS,
                VKScope.NOHTTPS,
                VKScope.STATUS,
        };

        //Use manager to manage SocialNetworks
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager()
                .findFragmentByTag(LoginActivity.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager VkSocialNetwork
            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
            mSocialNetworkManager.addSocialNetwork(vkNetwork);

            //Initiate every network from mSocialNetworkManager
            getFragmentManager().beginTransaction()
                    .add(mSocialNetworkManager, LoginActivity.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup btnLogin only for initialized SocialNetworks
            if (!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
//                    initSocialNetwork(socialNetwork);
                }
            }
        }
        if (VKAccessToken.currentToken() != null) {
            LoginActivity.idVk = Integer.parseInt(VKAccessToken.currentToken().userId);
            loggedIn();
        } else {
            loggedOut();
        }
        return rootView;
    }

    @Override
    public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        if (BuildConfig.USE_LOG) {
            Log.i(TAG, "onRequestSocialPersonSuccess");
        }
        LoginActivity.idVk = Integer.parseInt(socialPerson.id);
        LoginActivity.userName = socialPerson.name;
        loggedIn();
//        ((LoginActivity) getActivity()).getSupportActionBar().setTitle(LoginActivity.userName);
    }

//    private void initSocialNetwork(SocialNetwork socialNetwork) {
//        if (socialNetwork.isConnected()) {
//            btnLogin.setText(R.string.btn_login_true);
//            btnLogin.setVisibility(View.GONE);
//            btnLogin.setEnabled(false);
//            btnLogout.setVisibility(View.VISIBLE);
//            btnLogout.setEnabled(true);
//            loggedOut();
//            socialNetwork.requestCurrentPerson();
//        }
//    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        if (BuildConfig.USE_LOG) {
            Log.i(TAG, "onSocialNetworkManagerInitialized");
        }
        //when init SocialNetworks - get and setup btnLogin only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
//            initSocialNetwork(socialNetwork);
        }
    }

    private void loggedIn() {
        btnLogin.setVisibility(View.GONE);
        btnLogin.setEnabled(false);
        btnToMatchList.setVisibility(View.VISIBLE);
        btnToMatchList.setEnabled(true);
        btnLogout.setVisibility(View.VISIBLE);
        btnLogout.setEnabled(true);
    }

    private void loggedOut() {
        btnLogin.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(true);
        btnToMatchList.setVisibility(View.GONE);
        btnToMatchList.setEnabled(false);
        btnLogout.setVisibility(View.GONE);
        btnLogout.setEnabled(false);
    }

    private View.OnClickListener btnLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(VkSocialNetwork.ID);
            if (!socialNetwork.isConnected()) {
                socialNetwork.requestLogin();
            }
        }
    };

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
            LoginActivity.userName = null;
            loggedOut();
            VKSdk.logout();
        }
    };

    private View.OnClickListener btnSetDefaultsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LoginActivity.serverIP = "ifootball.ml";
            SharedPreferences.Editor editor = LoginActivity.serverSettings.edit();
            editor.putString(LoginActivity.APP_PREFERENCES_IP, LoginActivity.serverIP);
            editor.apply();
            curIP.setText(LoginActivity.serverIP);
            LoginActivity.serverPort = "443";
            editor.putString(LoginActivity.APP_PREFERENCES_PORT, LoginActivity.serverPort);
            editor.apply();
            curPort.setText(LoginActivity.serverPort);
        }
    };

    private View.OnKeyListener editIPKeyEvent = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                LoginActivity.serverIP = editIP.getText().toString();
                SharedPreferences.Editor editor = LoginActivity.serverSettings.edit();
                editor.putString(LoginActivity.APP_PREFERENCES_IP, LoginActivity.serverIP);
                editor.apply();
                curIP.setText(LoginActivity.serverIP);
                return true;
            }
            return false;
        }
    };

    private View.OnKeyListener editPortKeyEvent = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                LoginActivity.serverPort = editPort.getText().toString();
                SharedPreferences.Editor editor = LoginActivity.serverSettings.edit();
                editor.putString(LoginActivity.APP_PREFERENCES_PORT, LoginActivity.serverPort);
                editor.apply();
                curPort.setText(LoginActivity.serverPort);
                return true;
            }
            return false;
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        if (BuildConfig.USE_LOG) {
            Log.i(TAG, "onLoginSuccess");
        }
        SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.requestCurrentPerson();
        loggedIn();
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }
}