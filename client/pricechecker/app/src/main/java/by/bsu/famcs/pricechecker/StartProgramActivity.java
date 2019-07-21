package by.bsu.famcs.pricechecker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartProgramActivity extends AppCompatActivity {

    private Button buttonCleanBasket;
    private Button buttonResumeBasket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Start program Activity", "onCreate");
        setContentView(R.layout.activity_start_program);

        buttonCleanBasket = findViewById(R.id.buttonCleansBasket);
        buttonResumeBasket = findViewById(R.id.buttonResumeBasket);

        buttonCleanBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassesRef.basket = new Basket();
                StartProgramActivity.this.finish();
            }
        });

        buttonResumeBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(ClassesRef.getFilePath());
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String jsonString = bufferedReader.readLine();
                    Gson gson = new Gson();
                    ClassesRef.basket = gson.fromJson(jsonString, Basket.class);
                    StartProgramActivity.this.finish();
                } catch (FileNotFoundException e) {
                    ClassesRef.basket = new Basket();
                    e.printStackTrace();
                } catch (IOException e) {
                    ClassesRef.basket = new Basket();
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Start program Activity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Start program Activity", "onStop");
    }

    @Override
    public void finish() {
        super.finish();
        Log.i("Start program Activity", "finish");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Start program Activity", "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Start program Activity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Start program Activity", "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Start program Activity", "onRestart");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(ClassesRef.mainActivity != null){
            ClassesRef.mainActivity.finish();
        }
        if(ClassesRef.productInfoActivity != null){
            ClassesRef.productInfoActivity.finish();
        }
        if(ClassesRef.basketActivity != null){
            ClassesRef.basketActivity.finish();
        }
    }
}
