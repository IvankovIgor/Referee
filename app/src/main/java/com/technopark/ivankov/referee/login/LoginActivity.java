package com.technopark.ivankov.referee.login;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.technopark.ivankov.referee.BuildConfig;
import com.technopark.ivankov.referee.Constants;
import com.technopark.ivankov.referee.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.util.VKUtil;


public class LoginActivity extends AppCompatActivity implements Constants {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private Context context;
    private boolean isResumed = false;
    static final String[] vkScope = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                if (isResumed) {
                    switch (res) {
                        case LoggedOut:
                            showLogin();
                            break;
                        case LoggedIn:
                            showLogout();
                            break;
                        case Pending:
                            break;
                        case Unknown:
                            break;
                    }
                }
            }

            @Override
            public void onError(VKError error) {

            }
        });

        if (BuildConfig.DEBUG) {
            String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
            for (String fingerprint : fingerprints) Log.i(TAG, "Fingerprint:" + fingerprint);
            Log.i(TAG, "Created.");
        }
    }

    void showLogout() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LogoutFragment())
                .commitAllowingStateLoss();
    }

    void showLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (VKSdk.isLoggedIn()) {
            showLogout();
        } else {
            showLogin();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                res.saveTokenToSharedPreferences(context, VK_ACCESS_TOKEN);
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}