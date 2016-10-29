package com.example.nyashcore.referee;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.example.nyashcore.referee.content.ActionList;
import com.example.nyashcore.referee.content.MatchList;
import com.google.gson.JsonParseException;

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
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class HttpsClient {

    static void getMatches(String idUser) {

        Call<List<MatchList.Match>> call = createAPIService().getMatches(idUser);

        Response<List<MatchList.Match>> response;
        try {
            response = call.execute();
            try {
                List<MatchList.Match> list = response.body();
                for (int i = 0; i < list.size(); i++) {
                    new MatchList.Match(list.get(i));
                }
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        call.enqueue(new Callback<List<MatchList.Match>>() {
//            @Override
//            public void onResponse(Call<List<MatchList.Match>> call, Response<List<MatchList.Match>> response) {
//                try {
//                    List<MatchList.Match> list = response.body();
//                    for (int i = 0; i < list.size(); i++) {
//                        new MatchList.Match(list.get(i));
//                    }
//                } catch (JsonParseException e) {
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

    static boolean postAction(ActionList.Action action) {

        Call<ResponseBody> call = createAPIService().postAction(action.getIdMatch(), action.getIdAction(), action.getEvent());

        try {
            Response<ResponseBody> responseBody = call.execute();
            System.out.println(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static APIService createAPIService() {

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://" + LoginActivity.serverIP + ":" + LoginActivity.serverPort + "/");
        OkHttpClient client;

        client = new OkHttpClient.Builder().sslSocketFactory(getSSLConfig(LoginActivity.context).getSocketFactory())
                .build();

        Retrofit retrofit = builder.client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(APIService.class);
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
