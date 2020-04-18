package com.tommyapps.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private TextView productCounterTextView;
    private TextView boughtProductCounterTextView;
    private TextView totalPriceTextView;

    private ArrayList<Product> initProducts() {
        ArrayList<Product> list = new ArrayList<>();

        list.add(new Product("Ziemniaki", 3.39, true));
        list.add(new Product("Piwko", 2.59, false));
        list.add(new Product("Winko", 5, true));
        list.add(new Product("Chipsy", 2.1, false));
        list.add(new Product("Ziemniaksdafasdfasdfasdf asdf asdf asdf a sdf asdfi", 3.39, true));
        list.add(new Product("Piwko", 2.59, false));

        return list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Product> products = initProducts();
        this.productCounterTextView = (TextView) findViewById(R.id.productCounterTextView);
        this.totalPriceTextView = (TextView) findViewById(R.id.totalPriceTextView);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(mLayoutManager);

        adapter = new ProductAdapter(products);
        this.recyclerView.setAdapter(adapter);
        
        productCounterTextView.setText(String.valueOf(adapter.getItemCount()));
    }
}
