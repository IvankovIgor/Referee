package com.example.nyashcore.referee;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Button;

import com.example.nyashcore.referee.content.ActionList;
import com.example.nyashcore.referee.content.MatchList;
import com.example.nyashcore.referee.content.PlayerList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import static com.example.nyashcore.referee.LoginActivity.context;

/**
 * An activity representing a single Player detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * player details are presented side-by-side with a list of players
 * in a {@link MatchActivity}.
 */
public class PlayerDetailActivity extends AppCompatActivity {

    static int number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Button btnGoal = (Button) findViewById(R.id.btn_goal);
        btnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idTeam = addAction(view, "Goal");
                if (!idTeam.equals("false")) {
                    MatchList.getCurrentMatch().incrementScore(idTeam);
                }
            }
        });

        Button btnOwnGoal = (Button) findViewById(R.id.btn_own_goal);
        btnOwnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idTeam = addAction(view, "OwnGoal");
                if (!idTeam.equals("false")) {
                    MatchList.getCurrentMatch().ownGoal(idTeam);
                }
            }
        });

        Button btnYellow = (Button) findViewById(R.id.btn_yellow);
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAction(view, "YellowCard");
            }
        });

        Button btnRed = (Button) findViewById(R.id.btn_red);
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAction(view, "RedCard");
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(PlayerDetailFragment.ARG_PLAYER_ID,
                    getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID));
            PlayerDetailFragment fragment = new PlayerDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.player_detail_container, fragment)
                    .commit();
        }
    }

    private String addAction(View view, String action) {
        if (!MatchList.getCurrentMatch().isStarted() || MatchList.getCurrentMatch().isFinished()) {
            Snackbar.make(view, "Not allowed", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            return "false";
        }
        String idTeam = PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getIdTeam();
        Snackbar.make(view, action, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        MatchList.getCurrentMatch().getActionList().getACTIONS().add(new ActionList.Action(String.valueOf(MatchActivity.getTime())+"'", action + " - " +
                PlayerList.PLAYER_MAP.get(getIntent().getStringExtra(PlayerDetailFragment.ARG_PLAYER_ID)).getName(), action, idTeam));
        try {
            sendInfo(action);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return idTeam;
    }

    protected static void sendInfo(String message) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
        String matchId = MatchList.getCurrentMatch().getIdMatch();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
// From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = context.getResources().openRawResource(R.raw.intermediate);
        Certificate ca;
        ca = cf.generateCertificate(caInput);
        try {
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

// Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);
        try {
            URL url = new URL("https://" + LoginActivity.serverIP + ":" + LoginActivity.serverPort + "/api-referee/" + matchId + "/" + number + "/" + message + "/set-info");
//            URL url = new URL(path);
            HttpsURLConnection c = (HttpsURLConnection)url.openConnection();
            c.setSSLSocketFactory(context.getSocketFactory());
            c.setRequestMethod("GET");
            c.setReadTimeout(10000);
            c.connect();
            int responseCode = c.getResponseCode();
            System.out.println("Sending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
        } catch (IOException e) {
            System.out.println(e);
        }
        number++;
    }

//    protected static void sendInfo(String message) {
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("number", number);
//            jsonObject.put("data", message);
//            String matchId = MatchList.getCurrentMatch().getIdMatch();
//            try {
//                URL url = new URL("http://185.143.172.172:8080/api-referee/" + matchId + "/set-info");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");
//                OutputStream dStream = new BufferedOutputStream(connection.getOutputStream());
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dStream, "utf-8"));
//                writer.write(jsonObject.toString());
//                writer.flush();
//                writer.close();
//                int responseCode = connection.getResponseCode();
//                System.out.println("Sending 'POST' request to URL : " + url);
//                System.out.println("Post parameters : " + jsonObject);
//                System.out.println("Response Code : " + responseCode);
//            } catch (IOException e) {
//                System.out.println(e);
//            }
//        } catch (JSONException e) {
//            System.out.println(e);
//        }
//        number++;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
//            NavUtils.navigateUpTo(this, new Intent(this, MatchActivity.class));
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
