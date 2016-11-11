package com.technopark.ivankov.referee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.technopark.ivankov.referee.login.LoginActivity;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

public class Referee extends android.app.Application implements Constants {

    public static SharedPreferences serverPreferences;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(@Nullable VKAccessToken oldToken, @Nullable VKAccessToken newToken) {
            if (newToken == null) {
                Toast.makeText(Referee.this, "AccessToken invalidated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Referee.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        serverPreferences = getSharedPreferences(SERVER_PREFERENCES, Context.MODE_PRIVATE);
        VKSdk.initialize(Referee.this);
    }
}