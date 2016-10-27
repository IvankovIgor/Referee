package com.example.nyashcore.referee;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

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
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.nyashcore.referee.LoginActivity.context;

public class HttpsClient {

    public static void getMatches() {

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://" + LoginActivity.serverIP + ":" + LoginActivity.serverPort + "/");
        OkHttpClient client = null;

        try {
            client = new OkHttpClient.Builder().sslSocketFactory(getSSLConfig(context).getSocketFactory())
                    .build();
        } catch (CertificateException
                | KeyStoreException
                | NoSuchAlgorithmException
                | IOException
                | KeyManagementException e) {
                    e.printStackTrace();
        }

        assert client != null;
        Retrofit retrofit = builder.client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);
        Call<List<MatchList.Match>> call = service.getMatchList(LoginActivity.userId);

        call.enqueue(new Callback<List<MatchList.Match>>() {
            @Override
            public void onResponse(Call<List<MatchList.Match>> call, Response<List<MatchList.Match>> response) {
                try {
                    List<MatchList.Match> list = response.body();
                    for (int i = 0; i < list.size(); i++) {
                        new MatchList.Match(list.get(i));
                    }
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<MatchList.Match>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
        try (InputStream cert = context.getResources().openRawResource(R.raw.intermediate)) {
            ca = cf.generateCertificate(cert);
        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }
}
