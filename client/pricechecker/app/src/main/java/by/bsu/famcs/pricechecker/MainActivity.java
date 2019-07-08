package by.bsu.famcs.pricechecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private static final int REQUEST_CODE_CAMERA_PERMISSION = 100;
    private SurfaceView surfaceViewBarcode;
    private BarcodeDetector detector;
    private CameraSource cameraSource;
    private String scanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClassesRef.mainActivity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, ProductInfoActivity.class);
        startActivity(intent);

        surfaceViewBarcode = findViewById(R.id.surfaceBarcodeScanner);
        detector = new BarcodeDetector.Builder(this).
                setBarcodeFormats(Barcode.EAN_13 | Barcode.EAN_8).build();
        cameraSource = new CameraSource.Builder(getApplicationContext(), detector).
                setRequestedPreviewSize(1024,768).setRequestedFps(25f).
                setAutoFocusEnabled(true).build();
        surfaceViewBarcode.getHolder().addCallback(makeSurfaceViewCallback2());
    }

    @Override
    protected void onResume() {
        super.onResume();
        restartDetector();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClassesRef.mainActivity = null;
        detector.release();
        cameraSource.stop();
        cameraSource.release();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_CAMERA_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    cameraSource.start(surfaceViewBarcode.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(this, "Scanner won't work without permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void restartDetector(){
        detector.release();
        detector.setProcessor(makeDetectorProcessor());
    }

    private Detector.Processor<Barcode> makeDetectorProcessor(){
        return new Detector.Processor<Barcode>(){
            @Override
            public void release() {}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes != null && barcodes.size() > 0){
                    detector.release();

                    ToneGenerator toneNotification = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    toneNotification.startTone(ToneGenerator.TONE_PROP_BEEP, 150);

                    scanResult = barcodes.valueAt(0).displayValue;
                    while (ClassesRef.productInfoActivity == null){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    ClassesRef.productInfoActivity.fillForm(scanResult);
                }
            }
        };
    }

    private SurfaceHolder.Callback2 makeSurfaceViewCallback2(){
        return new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {}

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(surfaceViewBarcode.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        };
    }


//    private static final String SERVER_URL = "http://sinytim.pythonanywhere.com/product";
//    private static final int REQUEST_SCAN_CODE = 1234;
//    //private static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 100;
//    private static final String SCAN_RESULT = "SCAN_RESULT";
//    //private LocationManager locationManager;
//    ///private LocationListener locationListener;
//    private FusedLocationProviderClient fusedLocationClient;
//
//    private Button buttonScan;
//    private TextView textViewShowInfo;
//    private double latitude;
//    private double longitude;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        buttonScan = findViewById(R.id.buttonScan);
//        textViewShowInfo = findViewById(R.id.textViewShowInfo);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                MainActivity.this.latitude = location.getLatitude();
//                MainActivity.this.longitude = location.getLongitude();
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {}
//
//            @Override
//            public void onProviderEnabled(String s) {
//                String sss  = "";
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {}
//        };
//        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, 1000, 1.0f,
//                    locationListener);
//        }else {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
//        }*/
//
//
//        addListeners();
//    }
//
//    /*@SuppressLint("MissingPermission")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == REQUEST_CODE_ACCESS_FINE_LOCATION){
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                locationManager.requestLocationUpdates(
//                        LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
//                        locationListener);
//            }else {
//                Toast.makeText(this, "Scanner won't work without permission", Toast.LENGTH_LONG).show();
//            }
//        }
//    }*/
//
//    private void addListeners() {
//
//        buttonScan.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onClick(View view) {
//
//                if (hasConnection(MainActivity.this)) {
//                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    Activity#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for Activity#requestPermissions for more details.
//                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//
//                        return;
//                    }
//                    Task<Location> l = fusedLocationClient.getLastLocation()
//                            .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
//                                @Override
//                                public void onSuccess(Location location) {
//                                    // Got last known location. In some rare situations this can be null.
//                                    if (location != null) {
//                                        latitude = location.getLatitude();
//                                        // Logic to handle location object
//                                    }
//                                }
//                            });
//                    Intent intentScan = new Intent(MainActivity.this, ScannerActivity.class);
//                    startActivityForResult(intentScan, REQUEST_SCAN_CODE);
//                }else {
//                    Toast.makeText(MainActivity.this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }
//    @SuppressLint("MissingPermission")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == 1){
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            }else {
//                Toast.makeText(this, "Scanner won't work without permission", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data == null) {return;}
//        if(requestCode == REQUEST_SCAN_CODE && resultCode == RESULT_OK) {
//
//
//            String scanResult = data.getStringExtra(SCAN_RESULT);
//            InfoSend infoSend = new InfoSend(scanResult);
//            String infoStr = infoSend.JSONString();
//
//            HTTPAsyncTask task = new HTTPAsyncTask();
//            task.execute(SERVER_URL, infoStr, scanResult);
//        }
//    }
//
//
//    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
//
//        private ProgressDialog progressDialog;
//
//        @Override
//        protected String doInBackground(String... urls) {
//            try {
//                return HttpPost(urls[0], urls[1]);
//            } catch (IOException e) {
//                return "Товар " + urls[2] + " не найден";
//                //return "Unable to retrieve web page. URL may be invalid.";
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(MainActivity.this);
//            progressDialog.setMessage("Please wait");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (progressDialog.isShowing()){
//                progressDialog.dismiss();
//            }
//            textViewShowInfo.setText(result);
//        }
//
//        private String HttpPost(String stringURL, String jsonString) throws IOException {
//
//            URL url = new URL(stringURL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//
//            setPostRequestContent(connection, jsonString);
//            connection.connect();
//
//            InputStream stream = connection.getInputStream();
//            InputStreamReader streamReader = new InputStreamReader(stream);
//            BufferedReader reader = new BufferedReader(streamReader);
//            InfoReceive infoReceive = new InfoReceive(reader.readLine());
//
//            return  infoReceive.toString();
//            //int code = connection.getResponseCode();
//        }
//
//        private void setPostRequestContent(HttpURLConnection conn, String jsonString) throws IOException {
//
//            OutputStream outputStream = conn.getOutputStream();
//            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, "UTF-8");
//            BufferedWriter writer = new BufferedWriter(streamWriter);
//            writer.write(jsonString);
//            writer.flush();
//            writer.close();
//            outputStream.close();
//        }
//    }
//
//    public static boolean hasConnection(final Context context)
//    {
//        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        if (wifiInfo != null && wifiInfo.isConnected()) {
//            return true;
//        }
//        wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if (wifiInfo != null && wifiInfo.isConnected()) {
//            return true;
//        }
//        wifiInfo = connectivityManager.getActiveNetworkInfo();
//        if (wifiInfo != null && wifiInfo.isConnected()) {
//            return true;
//        }
//        return false;
//    }
}
