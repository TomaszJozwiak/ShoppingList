package com.tommyapps.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShoppingList extends AppCompatActivity implements ProductAdapter.OnItemClickListener {

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

    private TextView productTextView;
    private TextView priceTextView;
    private CheckBox boughtCheckBox;

    private Database db;

    private double totalPrice = 0;
    private int boughtProductCounter = 0;

    private ArrayList<Product> initProducts() {
        ArrayList<Product> list = new ArrayList<>();

        list.add(new Product("Ziemniaki", 3.39));
        list.add(new Product("Piwko", 2.59));
        list.add(new Product("Winko", 5));
        list.add(new Product("Chipsy", 2.1));
        list.add(new Product("Ziemniaksdafasdfasdfasdf asdf asdf asdf a sdf asdfi", 3.39));
        list.add(new Product("Piwko", 2.59));

        return list;
    }

    public void showAddProductPopUp(View view) {

        final Button cancelPopUpButton;
        final Button addProductPopUpButton;
        final TextView priceTextViewAddProductPopup;

        addProductDialog.setContentView(R.layout.add_product);

        productEditText = (EditText) addProductDialog.findViewById(R.id.shoppingListEditText);
        priceEditText = (EditText) addProductDialog.findViewById(R.id.priceEditText);
        cancelPopUpButton = (Button) addProductDialog.findViewById(R.id.cancelShoppingListPopUpButton);
        addProductPopUpButton = (Button) addProductDialog.findViewById(R.id.addShoppingListPopUpButton);
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
            Toast.makeText(ShoppingList.this, "Wpisz nazwę produktu", Toast.LENGTH_SHORT).show();
            productEditText.requestFocus();
        } else {
            String productName = productEditText.getText().toString();
            String price = "0";
            if (enablePriceCheckBox.isChecked()) {

                if (priceEditText.getText().toString().matches("")) {
                    Toast.makeText(ShoppingList.this, "Wpisz cenę", Toast.LENGTH_SHORT).show();
                    priceEditText.requestFocus();
                } else {
                    price = priceEditText.getText().toString();
                    products.add(new Product(productName, Double.valueOf(price)));
                    totalPrice += Double.valueOf(price);
                    priceEditText.setText("");
                    Toast.makeText(ShoppingList.this, "Dodano produkt: " + productName, Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    productEditText.setText("");
                    productEditText.requestFocus();
                }

            } else {
                products.add(new Product(productName));
                // Toast.makeText(MainActivity.this, "Dodano produkt: " + productName, Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                productEditText.setText("");
                productEditText.requestFocus();
            }

            productCounterTextView.setText(String.valueOf(adapter.getItemCount()));
            totalPriceTextView.setText(String.format("%.2f", totalPrice));

            db.insertProduct(productName, price);
            int numberOfRows = db.numberOfRows();
            Toast.makeText(this, String.valueOf(numberOfRows), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        db = new Database(this);

        //products = initProducts();
        products = db.getProducts();
        totalPrice = getTotalPriceFromProductList(products);
        boughtProductCounter = getBoughtItemCounterFromProductList(products);
        this.productCounterTextView = (TextView) findViewById(R.id.productCounterTextView);
        this.totalPriceTextView = (TextView) findViewById(R.id.totalPriceTextView);
        this.boughtProductCounterTextView = (TextView) findViewById(R.id.boughtProductCounterTextView);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(mLayoutManager);

        adapter = new ProductAdapter(products, this);
        this.recyclerView.setAdapter(adapter);

        productCounterTextView.setText(String.valueOf(adapter.getItemCount()));
        totalPriceTextView.setText(String.format("%.2f", totalPrice));
        boughtProductCounterTextView.setText(String.valueOf(boughtProductCounter));

        addProductDialog = new Dialog(this);


        int numberOfRows = db.numberOfRows();
        Toast.makeText(this, String.valueOf(numberOfRows), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(View view, int position) {

        productTextView = view.findViewById(R.id.productTextView);
        priceTextView = view.findViewById(R.id.priceTextView);
        boughtCheckBox = view.findViewById(R.id.boughtCheckBox);

        if (boughtCheckBox.isChecked()) {
            boughtCheckBox.setChecked(false);
            db.updateIsBought(position, boughtCheckBox.isChecked());
            productTextView.setAlpha(1f);
            priceTextView.setAlpha(1f);
            boughtProductCounter--;
        } else {
            boughtCheckBox.setChecked(true);
            db.updateIsBought(position, boughtCheckBox.isChecked());
            productTextView.setAlpha(0.25f);
            priceTextView.setAlpha(0.25f);
            boughtProductCounter++;
        }
        boughtProductCounterTextView.setText(String.valueOf(boughtProductCounter));

    }

    private double getTotalPriceFromProductList(ArrayList<Product> products){

        double price = 0;

        for (Product productPrice : products) {
            price += Double.valueOf(productPrice.getPrice());
        }

        return price;
    }

    private int getBoughtItemCounterFromProductList(ArrayList<Product> products){

        int productCounter = 0;

        for (Product product : products) {
            if (product.isBought()) {
                productCounter++;
            }
        }

        return productCounter;
    }
}