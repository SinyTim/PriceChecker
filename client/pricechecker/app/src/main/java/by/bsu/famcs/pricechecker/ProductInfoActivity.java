package by.bsu.famcs.pricechecker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class ProductInfoActivity extends AppCompatActivity {


    private static final String SCAN_RESULT = "SCAN_RESULT";
    private static final String SERVER_URL = "http://sinytim.pythonanywhere.com/product";

    private TextView textViewProductName;
    private TextView textViewPrice;
    private Button buttonCancel;
    private Spinner spinnerNumberProducts;
    private String barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_product_info);
        textViewProductName = findViewById(R.id.textViewProductName);
        textViewPrice = findViewById(R.id.textViewPrice);
        spinnerNumberProducts = findViewById(R.id.spinnerNumberProducts);
        buttonCancel = findViewById(R.id.buttonCancel);
        barcode = getIntent().getExtras().getString(SCAN_RESULT);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new Integer[]{1,2,3,4,5,6,7,8,9,10});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberProducts.setAdapter(adapter);

        addListeners();
        fillForm();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out);
    }

    private void addListeners() {
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductInfoActivity.this.finish();
            }
        });
    }

    private void fillForm(){
        InfoSend infoSend = new InfoSend(barcode);
        String infoStr = infoSend.JSONString();

        HTTPAsyncTask task = new HTTPAsyncTask();
        task.execute(SERVER_URL, infoStr, barcode);
    }
    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... urls) {
            try {
                return HttpPost(urls[0], urls[1]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ProductInfoActivity.this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if(result != null) {
                InfoReceive infoReceive = new InfoReceive(result);
                textViewPrice.setText(new DecimalFormat("#0.00").format(infoReceive.getPrice()));
                textViewProductName.setText(infoReceive.getName());
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductInfoActivity.this);
                builder.setTitle("Ошибка!")
                        .setMessage("Товар с кодом " + barcode + " не найден")
                        .setCancelable(false)
                        .setNegativeButton("Закрыть",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        ProductInfoActivity.this.finish();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
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

            return  reader.readLine();
        }

        private void setPostRequestContent(HttpURLConnection conn, String jsonString) throws IOException {

            OutputStream outputStream = conn.getOutputStream();
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter writer = new BufferedWriter(streamWriter);
            writer.write(jsonString);
            writer.flush();
            writer.close();
            outputStream.close();
        }
    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = connectivityManager.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
