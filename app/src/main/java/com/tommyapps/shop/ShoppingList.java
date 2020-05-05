package com.tommyapps.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private EditText productEditText;
    private EditText priceEditText;
    private CheckBox enablePriceCheckBox;

    private TextView productTextView;
    private TextView priceTextView;
    private CheckBox boughtCheckBox;

    private ArrayList<Product> products = new ArrayList<>();

    private Database db;

    private double totalPrice = 0;
    private int boughtProductCounter = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.product_list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.deleteAllSelectedProducts:
                showDeleteAllProductsAlert();
                return true;
            default:
                return false;
        }

    }

    private void showDeleteAllProductsAlert() {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Usuwanie zaznaczonych produktów")
                .setMessage("Czy na pewno chcesz usunąć zaznaczone produkty?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAllSelectedProducts();
                        Toast.makeText(ShoppingList.this, "Usunięto", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Nie", null)
                .show();

    }

    private void deleteAllSelectedProducts() {

        ArrayList<Integer> selectedProductsPositions = new ArrayList<>();

        updateProductList();

        for (Product product: products) {
            if (product.isBought()) {
                selectedProductsPositions.add(product.getId());
            }
        }

        if (selectedProductsPositions.size() > 0) {
            db.deleteAllSelectedProducts(selectedProductsPositions);
            updateProductList();
        } else {
            Toast.makeText(ShoppingList.this, "Nie zaznaczono żadnych produktów do usunięcia", Toast.LENGTH_SHORT).show();
        }

    }

    public void showAddProductPopUp(View view) {

        final Button cancelPopUpButton;
        final Button addProductPopUpButton;
        final TextView priceTextViewAddProductPopup;

        addProductDialog.setContentView(R.layout.add_product);

        productEditText = addProductDialog.findViewById(R.id.shoppingListEditText);
        priceEditText = addProductDialog.findViewById(R.id.priceEditText);
        cancelPopUpButton = addProductDialog.findViewById(R.id.cancelShoppingListPopUpButton);
        addProductPopUpButton = addProductDialog.findViewById(R.id.addShoppingListPopUpButton);
        enablePriceCheckBox = addProductDialog.findViewById(R.id.enablePriceCheckBox);
        priceTextViewAddProductPopup = addProductDialog.findViewById(R.id.priceTextViewAddProductPopup);

        priceEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});

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
        if (window != null) {
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        }
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
                    totalPrice += Double.valueOf(price);
                    priceEditText.setText("");
                    Toast.makeText(ShoppingList.this, "Dodano produkt: " + productName, Toast.LENGTH_SHORT).show();
                    productEditText.setText("");
                    productEditText.requestFocus();
                    db.insertProduct(productName, price);
                    updateProductList();
                }

            } else {
                Toast.makeText(ShoppingList.this, "Dodano produkt: " + productName, Toast.LENGTH_SHORT).show();
                productEditText.setText("");
                productEditText.requestFocus();
                db.insertProduct(productName, price);
                updateProductList();
            }

            productCounterTextView.setText(String.valueOf(adapter.getItemCount()));
            totalPriceTextView.setText(String.format("%.2f", totalPrice));

        }
    }

    private void updateProductList() {

        products.clear();
        products.addAll(db.getProducts());

        adapter = new ProductAdapter(products, this);
        this.recyclerView.setAdapter(adapter);

        totalPrice = getTotalPriceFromProductList(products);
        boughtProductCounter = getBoughtItemCounterFromProductList(products);

        productCounterTextView.setText(String.valueOf(adapter.getItemCount()));
        totalPriceTextView.setText(String.format("%.2f", totalPrice));
        boughtProductCounterTextView.setText(String.valueOf(boughtProductCounter));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        db = new Database(this);

        Intent intent = getIntent();
        db.setShoppingListTableName(intent.getStringExtra("shoppingList"));
        setTitle(intent.getStringExtra("shoppingList"));

        this.productCounterTextView = findViewById(R.id.productCounterTextView);
        this.totalPriceTextView = findViewById(R.id.totalPriceTextView);
        this.boughtProductCounterTextView = findViewById(R.id.boughtProductCounterTextView);

        this.recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(mLayoutManager);

        addProductDialog = new Dialog(this);

        updateProductList();

    }

    @Override
    public void onItemClick(View view, int position) {

        productTextView = view.findViewById(R.id.productTextView);
        priceTextView = view.findViewById(R.id.priceTextView);
        boughtCheckBox = view.findViewById(R.id.boughtCheckBox);

        if (boughtCheckBox.isChecked()) {
            boughtCheckBox.setChecked(false);
            productTextView.setAlpha(1f);
            priceTextView.setAlpha(1f);
            boughtProductCounter--;
        } else {
            boughtCheckBox.setChecked(true);
            productTextView.setAlpha(0.25f);
            priceTextView.setAlpha(0.25f);
            boughtProductCounter++;
        }
        db.updateIsBought(products.get(position).getId(), boughtCheckBox.isChecked());
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