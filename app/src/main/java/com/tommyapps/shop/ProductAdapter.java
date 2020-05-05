package com.tommyapps.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ProductAdapter extends RecyclerView.Adapter {

    private OnItemClickListener myOnItemClickListener;

    public ProductAdapter(ArrayList<Product> products, OnItemClickListener onItemClickListener) {
        this.products = products;
        this.myOnItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final View view;
        public final TextView productTextView;
        public final TextView priceTextView;
        public final CheckBox boughtCheckBox;
        OnItemClickListener onItemClickListener;


        public ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            this.view = view;
            this.onItemClickListener = onItemClickListener;
            productTextView = view.findViewById(R.id.productTextView);
            priceTextView = view.findViewById(R.id.priceTextView);
            boughtCheckBox = view.findViewById(R.id.boughtCheckBox);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }

    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private ArrayList<Product> products;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);

        return new ViewHolder(v, myOnItemClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Product product = products.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.productTextView.setText(product.getProduct());
        if (product.getPrice() != 0) {
            viewHolder.priceTextView.setText(String.format("%.2f", product.getPrice()));
        }

        if (product.isBought()) {
            viewHolder.boughtCheckBox.setChecked(true);
            viewHolder.productTextView.setAlpha(0.25f);
            viewHolder.priceTextView.setAlpha(0.25f);
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



