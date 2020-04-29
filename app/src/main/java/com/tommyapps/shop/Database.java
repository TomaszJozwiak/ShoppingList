package com.tommyapps.shop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ShopListDatabase.db";

    private static final String PRODUCTS_TABLE_NAME = "products";
    private static final String PRODUCTS_COLUMN_ID = "id";
    private static final String PRODUCTS_COLUMN_PRODUCT = "product";
    private static final String PRODUCTS_COLUMN_PRICE = "price";
    private static final String PRODUCTS_COLUMN_IS_BOUGHT = "isBought";
    private static final String PRODUCTS_COLUMN_SHOPPING_LIST = "shoppingList";

    public String shoppingListName = "Lista Kasi";


    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY, product VARCHAR, price VARCHAR, isBought INTEGER, shoppingList VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
       // db.execSQL("INSERT INTO products (product, price, isBought) VALUES ('Alicja', 2.69, 1)");
        //db.execSQL("DELETE FROM products");
       // this.onUpgrade(db, 0, 1);
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PRODUCTS_TABLE_NAME);
        return numRows;
    }

    public void insertProduct(String product, String price) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCTS_COLUMN_PRODUCT, product);
        contentValues.put(PRODUCTS_COLUMN_PRICE, price);
        contentValues.put(PRODUCTS_COLUMN_IS_BOUGHT, 0);
        contentValues.put(PRODUCTS_COLUMN_SHOPPING_LIST, shoppingListName);

        db.insert(PRODUCTS_TABLE_NAME, null, contentValues);

    }

    public void updateIsBought(int id, boolean isBought) {

        int isBoughtInt = 0;

        if (isBought) {
            isBoughtInt = 1;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCTS_COLUMN_IS_BOUGHT, isBoughtInt);
        db.update(PRODUCTS_TABLE_NAME, contentValues, PRODUCTS_COLUMN_ID + " = ?", new String[] {Integer.toString(id)} );

    }

    public ArrayList<Product> getProducts()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Product> products = new ArrayList<Product>();

        String query = "SELECT * FROM " + PRODUCTS_TABLE_NAME + " WHERE " + PRODUCTS_COLUMN_SHOPPING_LIST + " = '" + shoppingListName + "'";

        Cursor c = db.rawQuery(query, null);

        int productIndex = c.getColumnIndex(PRODUCTS_COLUMN_PRODUCT);
        int priceIndex = c.getColumnIndex(PRODUCTS_COLUMN_PRICE);
        int isBoughtIndex = c.getColumnIndex(PRODUCTS_COLUMN_IS_BOUGHT);
        int idIndex = c.getColumnIndex(PRODUCTS_COLUMN_ID);

        c.moveToFirst();

        while (!c.isAfterLast()) {

            boolean isBoughtBoolean;

            if (c.getInt(isBoughtIndex) == 1) {
                isBoughtBoolean = true;
            } else {
                isBoughtBoolean = false;
            }

            products.add(new Product(c.getString(productIndex), Double.valueOf(c.getString(priceIndex)), isBoughtBoolean, c.getInt(idIndex)));

            c.moveToNext();

        }

        return products;
    }

    public void deleteAllSelectedProducts(ArrayList<Integer> selectedProductsId) {

        SQLiteDatabase db = this.getWritableDatabase();

        for (Integer id : selectedProductsId) {

            db.delete(PRODUCTS_TABLE_NAME, PRODUCTS_COLUMN_ID + " = ?", new String[] {Integer.toString(id)} );

        }

    }

    public void setShoppingListTableName(String shoppingList) {
        this.shoppingListName = shoppingList;
    }
}
