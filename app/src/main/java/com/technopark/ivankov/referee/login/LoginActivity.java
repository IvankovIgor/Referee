package com.technopark.ivankov.referee.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.technopark.ivankov.referee.Constants;
import com.technopark.ivankov.referee.R;
import com.vk.sdk.util.VKUtil;


public class LoginActivity extends AppCompatActivity implements Constants, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    public static SharedPreferences serverPreferences;
    public static SharedPreferences userSettings;
    public static String userName;
    public static String serverIP;
    public static String serverPort;
    public static int idVk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        serverPreferences = getSharedPreferences(Constants.SERVER_PREFERENCES, Context.MODE_PRIVATE);
        serverIP = serverPreferences.getString(Constants.SERVER_IP, "ifootball.ml");
        serverPort = serverPreferences.getString(Constants.SERVER_PORT, "443");

        userSettings = getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        for (String fingerprint : fingerprints) Log.i(TAG, "Fingerprint:" + fingerprint);
        Log.i(TAG, this.getPackageName());
        Log.i(TAG, this.getClass().getName());

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