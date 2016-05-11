package bouchottebenjamin.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button checkNetwork;
    private Button loadPage;
    private WebView webView;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNetwork = (Button) findViewById(R.id.checkNetwork);
        loadPage = (Button) findViewById(R.id.loadPage);
        webView = (WebView) findViewById(R.id.webView);

        // INSCRIPTION AU RECEIVER - NETWORK
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, intentFilter);

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        checkNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNetwork(webView);
            }
        });

        loadPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetwork(webView);
            }
        });

    }

    private void checkNetwork (WebView v) {
        if (networkInfo != null && networkInfo.isConnected()) {
            v.loadData("<html><body>" + networkInfo.getTypeName() + "</body></html>", "text/html", "UTF-8");
        } else {
            v.loadData("<html><body>Vous n'etes pas connecte au reseau</body></html>", "text/html", "UTF-8");
        }
    }

    private void loadNetwork (WebView v) {
        if (networkInfo != null && networkInfo.isConnected()) {
            String urlString = "http://www.bbouchotte.fr/cnam/android_network.php";
            new DownloadWebpageTask().execute(urlString);
        }
    }

    BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetwork(webView);
        }
    };

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder out = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.append(line);
                    }
                    return out.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            webView.loadDataWithBaseURL(null, s, "text/html", "UTF-8", null);
        }
    }
}
