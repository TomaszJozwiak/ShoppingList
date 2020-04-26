package com.tommyapps.shop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ShopListDatabase.db";
    public static final String PRODUCTS_TABLE_NAME = "products";
    public static final String PRODUCTS_COLUMN_ID = "id";
    public static final String PRODUCTS_COLUMN_PRODUCT = "product";
    public static final String PRODUCTS_COLUMN_PRICE = "price";
    public static final String PRODUCTS_COLUMN_ISBOUGHT = "isBought";
    public static final String PRODUCTS_COLUMN_POSITION = "position";

    public ArrayList<Product> products = new ArrayList<Product>();


    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY, product VARCHAR, price VARCHAR, isBought INTEGER, position INTEGER)");
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
        //this.onUpgrade(db, 0, 1);
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PRODUCTS_TABLE_NAME);
        return numRows;
    }

    public void insertProduct(String product, String price) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCTS_COLUMN_PRODUCT, product);
        contentValues.put(PRODUCTS_COLUMN_PRICE, price);
        contentValues.put(PRODUCTS_COLUMN_ISBOUGHT, 0);
        contentValues.put(PRODUCTS_COLUMN_POSITION, this.numberOfRows());

        db.insert(PRODUCTS_TABLE_NAME, null, contentValues);

    }

    public void updateIsBought(int position, boolean isBought) {

        int isBoughtInt = 0;

        if (isBought) {
            isBoughtInt = 1;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCTS_COLUMN_ISBOUGHT, isBoughtInt);
        db.update(PRODUCTS_TABLE_NAME, contentValues, "position = ? ", new String[] { Integer.toString(position) } );

    }

    public ArrayList<Product> getProducts()
    {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM products", null);

        int productIndex = c.getColumnIndex(PRODUCTS_COLUMN_PRODUCT);
        int priceIndex = c.getColumnIndex(PRODUCTS_COLUMN_PRICE);
        int isBoughtIndex = c.getColumnIndex(PRODUCTS_COLUMN_ISBOUGHT);

        c.moveToFirst();


        while (!c.isAfterLast()) {

            boolean isBoughtBoolean;

            if (c.getInt(isBoughtIndex) == 1) {
                isBoughtBoolean = true;
            } else {
                isBoughtBoolean = false;
            }

            products.add(new Product(c.getString(productIndex), Double.valueOf(c.getString(priceIndex)), isBoughtBoolean));

            c.moveToNext();

        }

        return products;

    }
}
