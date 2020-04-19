package com.tommyapps.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.tommyapps.shop.R.id.boughtCheckBox;
import static com.tommyapps.shop.R.id.productTextView;

public class ProductAdapter extends RecyclerView.Adapter {

    public ProductAdapter(ArrayList<Product> products) {
        this.products = products;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView productTextView;
        public final TextView priceTextView;
        public final CheckBox boughtCheckBox;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            productTextView = view.findViewById(R.id.productTextView);
            priceTextView = view.findViewById(R.id.priceTextView);
            boughtCheckBox = view.findViewById(R.id.boughtCheckBox);
        }
    }

    private ArrayList<Product> products;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Product product = products.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.productTextView.setText(product.getProduct());
        if (product.getPrice() != 0) {
            viewHolder.priceTextView.setText(String.format("%.2f", product.getPrice()));
        }

    }

    @Override
    public int getItemCount() {

        if (products != null) {
            return products.size();
        } else {
            return 0;
        }
    }

}



