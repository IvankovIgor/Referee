package com.technopark.ivankov.referee.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.technopark.ivankov.referee.R;


public class LoginActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    public static final String APP_PREFERENCES = "server_settings";
    public static final String APP_PREFERENCES_IP = "IP";
    public static final String APP_PREFERENCES_PORT = "Port";
    public static SharedPreferences sSettings;
    public static Context context;
    public static String userName;
    public static String userId;
    public static String serverIP;
    public static String serverPort;
    public static String myId = "4189816";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        sSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(sSettings.contains(APP_PREFERENCES_IP)) {
            serverIP = sSettings.getString(APP_PREFERENCES_IP, "ifootball.ml");
        }
        if(sSettings.contains(APP_PREFERENCES_PORT)) {
            serverPort = sSettings.getString(APP_PREFERENCES_PORT, "443");
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
//        for(int i = 0; i<fingerprints.length;i++)
//            Log.i("myApp", "Fingerprint:"+fingerprints[i]);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        homeAsUpByBackStack();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackStackChanged() {
        homeAsUpByBackStack();
    }

    private void homeAsUpByBackStack() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}