package com.example.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ScannerActivity extends AppCompatActivity {

    private SurfaceView svBarcode;
    private BarcodeDetector detector;
    private CameraSource cameraSource;
    private String scanResult;
    private static final int REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        svBarcode = findViewById(R.id.surfaceBarcodeScanner);
        detector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.EAN_13).build();
        detector.setProcessor(new Detector.Processor<Barcode>(){
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

                    Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
                    intent.putExtra("ScanResult", scanResult); /* Sending text to next activity to display */
                    startActivity(intent);
                }
            }
        });

        cameraSource = new CameraSource.Builder(getApplicationContext(), detector).setRequestedPreviewSize(1024,768).setRequestedFps(25f).setAutoFocusEnabled(true).build();
        svBarcode.getHolder().addCallback(new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {}

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(svBarcode.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    cameraSource.start(svBarcode.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(this, "Scanner won't work without permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //initStuff();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.release();
        cameraSource.stop();
        cameraSource.release();
    }
}