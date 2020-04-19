package com.tommyapps.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Dialog addProductDialog;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private TextView productCounterTextView;
    private TextView boughtProductCounterTextView;
    private TextView totalPriceTextView;
    private ArrayList<Product> products = new ArrayList<>();
    private EditText productEditText;
    private EditText priceEditText;
    private CheckBox enablePriceCheckBox;

    private double totalPrice = 0;
    private int boughtProductCounter;

   /* private ArrayList<Product> initProducts() {
        ArrayList<Product> list = new ArrayList<>();

        list.add(new Product("Ziemniaki", 3.39));
        list.add(new Product("Piwko", 2.59));
        list.add(new Product("Winko", 5));
        list.add(new Product("Chipsy", 2.1));
        list.add(new Product("Ziemniaksdafasdfasdfasdf asdf asdf asdf a sdf asdfi", 3.39));
        list.add(new Product("Piwko", 2.59));

        return list;
    } */

    public void showAddProductPopUp(View view) {

        final Button cancelPopUpButton;
        final Button addProductPopUpButton;
        final TextView priceTextViewAddProductPopup;

        addProductDialog.setContentView(R.layout.add_product);

        productEditText = (EditText) addProductDialog.findViewById(R.id.productEditText);
        priceEditText = (EditText) addProductDialog.findViewById(R.id.priceEditText);
        cancelPopUpButton = (Button) addProductDialog.findViewById(R.id.cancelPopUpButton);
        addProductPopUpButton = (Button) addProductDialog.findViewById(R.id.addProductPopUpButton);
        enablePriceCheckBox = (CheckBox) addProductDialog.findViewById(R.id.enablePriceCheckBox);
        priceTextViewAddProductPopup = (TextView) addProductDialog.findViewById(R.id.priceTextViewAddProductPopup);

        enablePriceCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (enablePriceCheckBox.isChecked()) {
                    priceEditText.setEnabled(true);
                    priceEditText.setAlpha(1f);
                    priceTextViewAddProductPopup.setAlpha(1f);
                } else {
                    priceEditText.setEnabled(false);
                    priceEditText.setAlpha(0.5f);
                    priceTextViewAddProductPopup.setAlpha(0.3f);
                }

            }
        });

        cancelPopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductDialog.dismiss();
            }
        });

        addProductPopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });


        productEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    addProduct();
                }
                return false;
            }
        });

        priceEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    addProduct();
                }
                return false;
            }
        });

        addProductDialog.show();
        Window window = addProductDialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    }


    public void addProduct() {

        if (productEditText.getText().toString().matches("")) {
            Toast.makeText(MainActivity.this, "Wpisz nazwę produktu", Toast.LENGTH_SHORT).show();
            productEditText.requestFocus();
        } else {
            if (enablePriceCheckBox.isChecked()) {

                if (priceEditText.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "Wpisz cenę", Toast.LENGTH_SHORT).show();
                    priceEditText.requestFocus();
                } else {
                    products.add(new Product(productEditText.getText().toString(), Double.valueOf(priceEditText.getText().toString())));
                    totalPrice += Double.valueOf(priceEditText.getText().toString());
                    priceEditText.setText("");
                    Toast.makeText(MainActivity.this, "Dodano produkt: " + productEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    productEditText.setText("");
                    productEditText.requestFocus();
                }

            } else {
                products.add(new Product(productEditText.getText().toString()));
                Toast.makeText(MainActivity.this, "Dodano produkt: " + productEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                productEditText.setText("");
                productEditText.requestFocus();
            }

            productCounterTextView.setText(String.valueOf(adapter.getItemCount()));
            totalPriceTextView.setText(String.format("%.2f", totalPrice));

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // products = initProducts();
        this.productCounterTextView = (TextView) findViewById(R.id.productCounterTextView);
        this.totalPriceTextView = (TextView) findViewById(R.id.totalPriceTextView);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(mLayoutManager);

        adapter = new ProductAdapter(products);
        this.recyclerView.setAdapter(adapter);

        productCounterTextView.setText(String.valueOf(adapter.getItemCount()));
        totalPriceTextView.setText(String.format("%.2f", totalPrice));

        addProductDialog = new Dialog(this);

    }
}
