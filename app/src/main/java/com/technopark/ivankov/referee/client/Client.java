package com.technopark.ivankov.referee.client;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.technopark.ivankov.referee.BuildConfig;
import com.technopark.ivankov.referee.R;
import com.technopark.ivankov.referee.content.Action;
import com.technopark.ivankov.referee.content.MatchList;
import com.technopark.ivankov.referee.login.LoginActivity;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static final String TAG = Client.class.getSimpleName();

    public static void getMatches(int idVk) {
        Call<List<MatchList.Match>> call;
        JsonObject param = new JsonObject();
        param.addProperty("idVk", idVk);
        call = createAPIService().getMatches(param);

        Response<List<MatchList.Match>> response;
        try {
            response = call.execute();
            try {
                if (BuildConfig.USE_LOG) {
                    checkRequestContent(call.request());
                    checkResponseContent(response);
                }
                List<MatchList.Match> list = response.body();
                for (int i = 0; i < list.size(); i++) {
                    new MatchList.Match(list.get(i));
                }
            } catch (JsonParseException | NullPointerException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void postAction(final Action action) {
        if (BuildConfig.USE_LOG) {
            checkAction(action);
        }

        Call<ResponseBody> call = createAPIService().postAction(action);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (BuildConfig.USE_LOG) {
                    try {
                        checkRequestContent(call.request());
                        checkResponseContent(response);
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private static APIService createAPIService() {

        String prefix = LoginActivity.serverPort.equals("443") ? "https://" : "http://";
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(prefix + LoginActivity.serverIP + ":" + LoginActivity.serverPort + "/");
        OkHttpClient client;

        SSLSocketFactory sslSocketFactory = getSSLConfig(LoginActivity.context).getSocketFactory();
        X509TrustManager trustManager = Platform.get().trustManager(sslSocketFactory);
        client = new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager)
                .build();

        Retrofit retrofit = builder.client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(APIService.class);
    }

    private static void checkAction(Action action) {
        Log.i(TAG, "idMatch: " + action.getIdMatch());
        Log.i(TAG, "idTeam: " + action.getIdTeam());
        Log.i(TAG, "teamName: " + action.getTeamName());
        Log.i(TAG, "idPlayer: " + action.getIdPlayer());
        Log.i(TAG, "playerName: " + action.getPlayerName());
        Log.i(TAG, "idAction: " + action.getIdAction());
        Log.i(TAG, "idEvent: " + action.getIdEvent());
        Log.i(TAG, "minute: " + action.getMinute());
    }

    private static void checkRequestContent(Request request) {
        try {
            Headers requestHeaders = request.headers();
            Log.i(TAG, "Headers: " + requestHeaders.toString());
            HttpUrl requestUrl = request.url();
            Log.i(TAG, "Url: " + requestUrl.toString());
            RequestBody requestBody = request.body();
            Log.i(TAG, "RequestBody: " + requestBody.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static void checkResponseContent(Response response) {
        try {
            Headers responseHeaders = response.headers();
            Log.i(TAG, "Headers: " + responseHeaders.toString());
            Integer responseCode = response.code();
            Log.i(TAG, "ResponseCode: " + responseCode.toString());
            Object responseBody = response.body();
            Log.i(TAG, "ResponseBody: " + responseBody.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static SSLContext getSSLConfig(Context context) {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        Certificate ca = null;
        try {
            try (InputStream cert = context.getResources().openRawResource(R.raw.intermediate)) {
                assert cf != null;
                ca = cf.generateCertificate(cert);
            }
        } catch (IOException | CertificateException e) {
            e.printStackTrace();
        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore   = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            assert keyStore != null;
            keyStore.load(null, null);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
        try {
            keyStore.setCertificateEntry("ca", ca);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            assert tmf != null;
            tmf.init(keyStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            assert sslContext != null;
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }
}
