package by.bsu.famcs.pricechecker;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class BasketActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBasket;
    private Button buttonBasketBurger;
    private TextView textViewBasketTotalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_basket);
        ClassesRef.basketActivity = this;

        recyclerViewBasket = findViewById(R.id.recyclerViewBasket);
        textViewBasketTotalSum = findViewById(R.id.textViewBasketTotalSum);
        buttonBasketBurger = findViewById(R.id.buttonBasketBurger);

        recyclerViewBasket.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(ClassesRef.basket);
        recyclerViewBasket.setAdapter(recyclerViewAdapter);

        buttonBasketBurger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void changeTotalSum(String newSum){
        textViewBasketTotalSum.setText(newSum);
    }
}
