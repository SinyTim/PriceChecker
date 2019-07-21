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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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

    private static final String SERVER_URL = "http://sinytim.pythonanywhere.com/product";
    private InfoReceive infoReceive;

    private TextView textViewProductName;
    private TextView textViewTotalSum;
    private TextView textViewPrice;
    private LinearLayout layoutFragment;
    private Button buttonCancel;
    private Button buttonBurger;
    private Button buttonAdd;
    private Spinner spinnerNumberProducts;
    private String barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Product Info Activity", "onCreate");
        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out);
        ClassesRef.productInfoActivity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_product_info);

        textViewProductName = findViewById(R.id.textViewProductName);
        textViewPrice = findViewById(R.id.textViewPrice);
        spinnerNumberProducts = findViewById(R.id.spinnerNumberProducts);
        textViewTotalSum = findViewById(R.id.textViewTotalSum);
        layoutFragment = findViewById(R.id.layoutFragment);
        buttonBurger = findViewById(R.id.buttonBurger);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonAdd = findViewById(R.id.buttonAdd);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new Integer[]{1,2,3,4,5,6,7,8,9,10});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberProducts.setAdapter(adapter);

        addListeners();
        if(ClassesRef.basket != null) {
            ClassesRef.basket.notifySums();
        }else{
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        Log.i("Product Info Activity", "finish");
        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Product Info Activity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Product Info Activity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Product Info Activity", "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Product Info Activity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Product Info Activity", "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Product Info Activity", "onRestart");
    }

    @Override
    public void onBackPressed() {

        clearFields();
        if(layoutFragment.getVisibility() == View.VISIBLE){
            ProductInfoActivity.this.layoutFragment.setVisibility(View.INVISIBLE);
            ClassesRef.mainActivity.restartDetector();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductInfoActivity.this);
            builder.setTitle("Закрыть приложение")
                    .setMessage("Вы уверены, что хотите закрыть приложение")
                    .setCancelable(false)
                    .setPositiveButton("Да",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            ProductInfoActivity.this.finish();
                            ClassesRef.mainActivity.finish();
                        }
                    })
                    .setNegativeButton("Нет",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void changeTotalSum(String newSum){
        textViewTotalSum.setText(newSum);
    }

    private void addListeners() {
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
                ProductInfoActivity.this.layoutFragment.setVisibility(View.INVISIBLE);
                ClassesRef.mainActivity.restartDetector();
            }
        });
        buttonBurger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductInfoActivity.this, BasketActivity.class);
                startActivity(intent);
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassesRef.basket.addProduct(new ProductInfoData(barcode, infoReceive.name,
                        infoReceive.price, spinnerNumberProducts.getSelectedItemPosition()+1));
                clearFields();
                ProductInfoActivity.this.layoutFragment.setVisibility(View.INVISIBLE);
                ClassesRef.basket.notifySums();
                ClassesRef.mainActivity.restartDetector();
            }
        });
    }

    private void clearFields(){
        this.barcode = null;
        this.spinnerNumberProducts.setSelection(0);
        this.textViewProductName.setText("");
        this.textViewPrice.setText("");
    }

    public void fillForm(String barcode){
        this.barcode = barcode;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                InfoSend infoSend = new InfoSend(ProductInfoActivity.this.barcode);
                String infoStr = infoSend.JSONString();

                HTTPAsyncTask task = new HTTPAsyncTask();
                task.execute(SERVER_URL, infoStr, ProductInfoActivity.this.barcode);
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
                ProductInfoActivity.this.layoutFragment.setVisibility(View.VISIBLE);
                infoReceive = new InfoReceive(result);
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
                                        ProductInfoActivity.this.layoutFragment.setVisibility(View.INVISIBLE);
                                        ClassesRef.mainActivity.restartDetector();
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
