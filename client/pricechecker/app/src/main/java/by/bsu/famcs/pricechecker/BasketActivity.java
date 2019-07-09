package by.bsu.famcs.pricechecker;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BasketActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBasket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_basket);

        recyclerViewBasket = findViewById(R.id.recyclerViewBasket);
        recyclerViewBasket.setLayoutManager(new LinearLayoutManager(this));
        List<ProductInfoData> list = new ArrayList<>();
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(list);
        recyclerViewBasket.setAdapter(recyclerViewAdapter);
        list.add(new ProductInfoData("123456789", "lalala", 12.24f, 2));
        //list.add("LALALA");
        recyclerViewAdapter.notifyItemInserted(list.size()-1);
    }
}
