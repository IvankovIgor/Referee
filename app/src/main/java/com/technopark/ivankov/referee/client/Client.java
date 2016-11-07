package com.technopark.ivankov.referee.client;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    public static void getMatches(int idVk) {
        Call<List<MatchList.Match>> call;
        JsonObject param = new JsonObject();
        param.addProperty("idVk", idVk);
        call = createAPIService().getMatches(param);

        Response<List<MatchList.Match>> response;
        try {
            response = call.execute();
            try {
                checkRequestContent(call.request());
                checkResponseContent(response);
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

//        call.enqueue(new Callback<List<MatchList.Match>>() {
//            @Override
//            public void onResponse(Call<List<MatchList.Match>> call, Response<List<MatchList.Match>> response) {
//                try {
//                    checkRequestContent(call.request());
//                    checkResponseContent(response);
//                    List<MatchList.Match> list = response.body();
//                    for (int i = 0; i < list.size(); i++) {
//                        new MatchList.Match(list.get(i));
//                    }
//                } catch (JsonParseException | NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<MatchList.Match>> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
    }

    public static void postAction(final Action action) {

        Call<ResponseBody> call = createAPIService().postAction(action);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    checkRequestContent(call.request());
                    checkResponseContent(response);
                } catch (JsonParseException e) {
                    e.printStackTrace();
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
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(prefix + LoginActivity.serverIP + ":" + LoginActivity.serverPort + "/");
        OkHttpClient client;

        SSLSocketFactory sslSocketFactory = getSSLConfig(LoginActivity.context).getSocketFactory();
        X509TrustManager trustManager = Platform.get().trustManager(sslSocketFactory);
//    if (LoginActivity.serverPort.equals("443")) {
        client = new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager)
                .build();
//    } else {
//        client = new OkHttpClient.Builder().build();
//    }

        Retrofit retrofit = builder.client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(APIService.class);
    }

    private static void checkRequestContent(Request request) {
        try {
            Headers requestHeaders = request.headers();
            LOGGER.info("Headers: " + requestHeaders.toString());
            HttpUrl requestUrl = request.url();
            LOGGER.info("HttpUrl: " + requestUrl.toString());
            RequestBody requestBody = request.body();
            LOGGER.info("RequestBody: " + requestBody.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static void checkResponseContent(Response response) {
        try {
            Headers responseHeaders = response.headers();
            LOGGER.info("Headers: " + responseHeaders.toString());
            Integer responseCode = response.code();
            LOGGER.info("ResponseCode: " + responseCode.toString());
            Object responseBody = response.body();
            LOGGER.info("ResponseBody: " + responseBody.toString());
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
