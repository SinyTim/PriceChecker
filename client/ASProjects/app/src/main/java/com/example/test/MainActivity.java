package com.example.test;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static String SERVER_URL = "http://sinytim.pythonanywhere.com/product";

    private EditText textAreaInputCode;
    private Button buttonFind;
    private TextView textViewShowInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        textAreaInputCode = findViewById(R.id.textAreaInputCode);
        buttonFind = findViewById(R.id.buttonFind);
        textViewShowInfo = findViewById(R.id.textViewShowInfo);

        addListeners();
    }

    private void addListeners(){
        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String codeStr = textAreaInputCode.getText().toString();
                long code = Long.parseLong(codeStr);
                InfoSend infoSend = new InfoSend(code);
                String infoStr = infoSend.JSONString();

                HTTPAsyncTask task = new HTTPAsyncTask();
                task.execute(SERVER_URL, infoStr);
            }
        });
    }
    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... urls) {
            try {
                return HttpPost(urls[0], urls[1]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            textViewShowInfo.setText(result);
        }

        private String HttpPost(String stringURL, String jsonString) throws IOException {

            URL url = new URL(stringURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            setPostRequestContent(connection, jsonString);
            connection.connect();
            InputStream stream = connection.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            InfoReceive infoReceive = new InfoReceive(reader.readLine());
            return  infoReceive.toString();
            //String str = connection.getResponseMessage();
            //int code = connection.getResponseCode();
        }

        private void setPostRequestContent(HttpURLConnection conn, String jsonString) throws IOException {

            OutputStream outputStream = conn.getOutputStream();
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter writer = new BufferedWriter(streamWriter);
            writer.write(jsonString);
            //Log.i(MainActivity.class.toString(), jsonString);
            writer.flush();
            writer.close();
            outputStream.close();
        }
    }


    // класс для получения запроса через URL
    //
    //new RequestSender().execute("http://sinytim.pythonanywhere.com/" + textAreaInputCode.getText().toString());
    //

    /*private class RequestSender extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(line, "{,}");
                    while (st.hasMoreTokens()){
                        buffer.append(st.nextElement());
                        buffer.append("\n");
                    }
                }
                //return buffer.toString();
            } catch (MalformedURLException e) {
                buffer.append("Malformed URL Exception");
                e.printStackTrace();
            } catch (IOException e) {
                buffer.append("File problem");
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            textViewShowInfo.setText(result);
        }
    }*/
}
