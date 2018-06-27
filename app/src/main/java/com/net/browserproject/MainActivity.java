package com.net.browserproject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    EditText address;
    ProgressDialog progress;

    TextView responseTxt;
    TextView requestTxt;
    TextView htmlTxt;

    Button get;
    Button clear;
    Button webview;
    public static String URL;

    Button Post;
    Button samplePost;


    String req = "";
    String resp = "";
    String LOG_TAG = "error =  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        textView = findViewById(R.id.test);

        progress = new ProgressDialog(this);
        progress.setMessage("Loading ...");
        address = findViewById(R.id.address);

        Post = findViewById(R.id.doPost);
        samplePost = findViewById(R.id.doSamplePost);

        webview = findViewById(R.id.doWebview);
        get = findViewById(R.id.doGet);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MakeNetworkCall().execute("http://" + address.getText() + "/", "Get");
            }
        });

        clear = findViewById(R.id.doClear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTxt.setText("");
                responseTxt.setText("");
                htmlTxt.setText("");
            }
        });


        webview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL = "http://" + address.getText().toString() + "/";
                MainActivity.this.startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            }
        });


        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MakeNetworkCall().execute("https://" + address.getText() + "/", "Post");

//                new MakeNetworkCall().execute("https://jsonplaceholder.typicode.com/posts", "Post");
            }
        });


        samplePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MakeNetworkCall().execute("https://jsonplaceholder.typicode.com/posts", "Post");

            }
        });
    }


    InputStream ByPostMethod(String ServerURL) {

        InputStream DataInputStream = null;
        try {

            //Post parameters
            String PostParam = "first_name=sina&amp;last_name=mehdizade";

            //Preparing
            URL url = new URL(ServerURL);

            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
            //set timeout for reading InputStream
            cc.setReadTimeout(20000);
            // set timeout for connection
            cc.setConnectTimeout(20000);
            //set HTTP method to POST
            cc.setRequestMethod("POST");
            //set it to true as we are connecting for input
            cc.setDoInput(true);
            //opens the communication link
            cc.connect();

            //Writing data (bytes) to the data output stream
            DataOutputStream dos = new DataOutputStream(cc.getOutputStream());
            dos.writeBytes(PostParam);
            //flushes data output stream.
            dos.flush();
            dos.close();

            //Getting HTTP response code
            int response = cc.getResponseCode();
            resp = "HTTP/1.1 " + cc.getResponseCode() + " " + cc.getResponseMessage() + "\n==========================";
            req = cc.getRequestMethod() + " " + cc.getURL().getPath() + " HTTP/1.1\nHost: " + cc.getURL().getHost() + "\nContent-type: " + cc.getContentType() + "\n==========================";

            //if response code is 200 / OK then read Inputstream
            //HttpURLConnection.HTTP_OK is equal to 200
            if (response == HttpURLConnection.HTTP_OK) {
                DataInputStream = cc.getInputStream();
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in getting data", e);
        }
        return DataInputStream;

    }


    InputStream ByGetMethod(String ServerURL) {

        InputStream DataInputStream = null;
        try {

            URL url = new URL(ServerURL);
            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
            //timeout vase khundane inputstream
            cc.setReadTimeout(20000);
            // timeout vas connection
            cc.setConnectTimeout(20000);
            //get krdne method
            cc.setRequestMethod("GET");
            //vas inke get hast pas inpute vas hamin true mizarim
            cc.setDoInput(true);


            //http response code k 200 301 404 in chizas
            int response = cc.getResponseCode();
            resp = "HTTP/1.1 " + cc.getResponseCode() + " " + cc.getResponseMessage() + "\n==========================";
            req = cc.getRequestMethod() + " " + cc.getURL().getPath() + " HTTP/1.1\nHost: " + cc.getURL().getHost() + "\nContent-type: " + cc.getContentType() + "\n==========================";

            //ag code 200 bashe yani okaye pas tu in qesmat bayad html o peyda kone
            if (response == HttpURLConnection.HTTP_OK) {
                DataInputStream = cc.getInputStream();
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in Getting data", e);
        }
        return DataInputStream;

    }

    String ConvertStreamToString(InputStream stream) {

        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();

        String line = null;
        try {

            while ((line = reader.readLine()) != null) {
                response.append(line + "\n");
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in convert stream to string", e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in convert stream to string", e);
        } finally {

            try {
                stream.close();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error in convert stream to string", e);

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error in convert stream to string", e);
            }
        }
        return response.toString();


    }

    public void DisplayMessage(String a) {

        htmlTxt = findViewById(R.id.html);
        htmlTxt.setMovementMethod(new ScrollingMovementMethod());
        htmlTxt.setText(a);

        ///////////////

        requestTxt = findViewById(R.id.request);
        requestTxt.setMovementMethod(new ScrollingMovementMethod());
        requestTxt.append(req + "\n");

        ///////////

        responseTxt = findViewById(R.id.response);
        responseTxt.setMovementMethod(new ScrollingMovementMethod());
        responseTxt.append(resp + "\n");

    }

    private class MakeNetworkCall extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
            DisplayMessage("Please Wait ...");
        }

        @Override
        protected String doInBackground(String... arg) {

            InputStream is = null;
            String URL = arg[0];
            Log.d(LOG_TAG, "URL: " + URL);
            String res = "";


            if (arg[1].equals("Post")) {

                is = ByPostMethod(URL);

            } else {

                is = ByGetMethod(URL);
            }
            if (is != null) {
                res = ConvertStreamToString(is);
            } else {
                res = "Something went wrong";
            }
            return res;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            DisplayMessage(result);
            Log.d(LOG_TAG, "Result: " + result);
        }
    }


}
