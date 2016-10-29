package com.example.nyashcore.referee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nyashcore.referee.content.MatchList;
import com.example.nyashcore.referee.content.PlayerList;
import com.example.nyashcore.referee.content.TeamList;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import java.util.List;

public class LoginFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener, OnRequestSocialPersonCompleteListener {
    public static SocialNetworkManager mSocialNetworkManager;
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
    private Button vk;
    private Button logout;
    private Button resetIP;
    private EditText editIP;
    private EditText editPort;
    private TextView curIP;
    private TextView curPort;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
//        ((LoginActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        vk = (Button) rootView.findViewById(R.id.vk);
        vk.setText("Login via vk");
        vk.setOnClickListener(loginClick);

        logout = (Button) rootView.findViewById(R.id.logout);
        logout.setOnClickListener(logoutClick);

        resetIP = (Button) rootView.findViewById(R.id.resetIP);
        resetIP.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    LoginActivity.serverIP = "ifootball.ml";
                    SharedPreferences.Editor editor = LoginActivity.sSettings.edit();
                    editor.putString(LoginActivity.APP_PREFERENCES_IP, LoginActivity.serverIP);
                    editor.apply();
                    curIP.setText(LoginActivity.serverIP);
                    LoginActivity.serverPort = "443";
                    editor.putString(LoginActivity.APP_PREFERENCES_PORT, LoginActivity.serverPort);
                    editor.apply();
                    curPort.setText(LoginActivity.serverPort);
                }
            }
        );

        curIP = (TextView) rootView.findViewById(R.id.curIP);
        curIP.setText(LoginActivity.serverIP);

        curPort = (TextView) rootView.findViewById(R.id.curPort);
        curPort.setText(LoginActivity.serverPort);

        editIP = (EditText) rootView.findViewById(R.id.editIP);
        editIP.setOnKeyListener(new View.OnKeyListener()
                {
                    public boolean onKey(View v, int keyCode, KeyEvent event)
                    {
                        if(event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                        {
                            LoginActivity.serverIP = editIP.getText().toString();
                            SharedPreferences.Editor editor = LoginActivity.sSettings.edit();
                            editor.putString(LoginActivity.APP_PREFERENCES_IP, LoginActivity.serverIP);
                            editor.apply();
                            curIP.setText(LoginActivity.serverIP);
                            return true;
                        }
                        return false;
                    }
                }
        );

        editPort = (EditText) rootView.findViewById(R.id.editPort);
        editPort.setOnKeyListener(new View.OnKeyListener()
                {
                    public boolean onKey(View v, int keyCode, KeyEvent event)
                    {
                        if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                (keyCode == KeyEvent.KEYCODE_ENTER))
                        {
                            LoginActivity.serverPort = editPort.getText().toString();
                            SharedPreferences.Editor editor = LoginActivity.sSettings.edit();
                            editor.putString(LoginActivity.APP_PREFERENCES_PORT, LoginActivity.serverPort);
                            editor.apply();
                            curPort.setText(LoginActivity.serverPort);
                            return true;
                        }
                        return false;
                    }
                }
        );

        //Get Keys for initiate SocialNetworks
        String VK_KEY = getActivity().getString(R.string.vk_app_id);

        //Chose permissions
        String[] vkScope = new String[] {
                VKScope.FRIENDS,
                VKScope.WALL,
                VKScope.PHOTOS,
                VKScope.NOHTTPS,
                VKScope.STATUS,
        };

        //Use manager to manage SocialNetworks
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(LoginActivity.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager VkSocialNetwork
            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
            mSocialNetworkManager.addSocialNetwork(vkNetwork);

            //Initiate every network from mSocialNetworkManager
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, LoginActivity.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if(!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }
        return rootView;
    }

    @Override
    public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        LoginActivity.userId = socialPerson.id;
        LoginActivity.userName = socialPerson.name;
        ((LoginActivity)getActivity()).getSupportActionBar().setTitle(LoginActivity.userName);
    }

    private void initSocialNetwork(SocialNetwork socialNetwork){
        if(socialNetwork.isConnected()){
            vk.setText("Show match list");
            MatchList.MATCHES.clear();
            MatchList.MATCH_MAP.clear();
            MatchListActivity.PLAYER_TEAM_MAP.clear();
            PlayerList.PLAYER_MAP.clear();
            TeamList.TEAM_MAP.clear();
            logout.setVisibility(View.VISIBLE);
            socialNetwork.requestCurrentPerson();
        }
    }
    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }

    //Login listener

    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int networkId = 0;
            networkId = VkSocialNetwork.ID;
            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if(!socialNetwork.isConnected()) {
                if(networkId != 0) {
                    socialNetwork.requestLogin();
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
                showMatchList(socialNetwork.getID());
            }
        }
    };

    private View.OnClickListener logoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            vk.setText("Login via vk");
            ((LoginActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
            logout.setVisibility(View.GONE);
            LoginActivity.userId = null;
            LoginActivity.userName = null;
            MatchList.MATCHES.clear();
            VKSdk.logout();
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.requestCurrentPerson();
//        showMatchList(networkId);
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private void showMatchList(int networkId){
        if (MatchList.MATCHES.isEmpty()) {
            HttpsClient.getMatches(LoginActivity.myId);
        }
        Intent intent = new Intent(getActivity(), MatchListActivity.class);
        startActivity(intent);
//        ProfileFragment profile = ProfileFragment.newInstance(networkId);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .addToBackStack("profile")
//                .replace(R.id.container, profile)
//                .commit();
////                .commitAllowingStateLoss();
    }
}