package com.tommyapps.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> shoppingList = new ArrayList<String>();
    private TextView shoppingListEditText;
    private SharedPreferences sharedPreferences;
    private ListView shoppingListView;
    private ArrayAdapter<String> shoppingListArrayAdapter;
    private ArrayList<String> deleteShoppingListArray = new ArrayList<String>();
    private boolean checkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("TommyShoppingList");

        sharedPreferences = this.getSharedPreferences("com.tommyapps.shop", Context.MODE_PRIVATE);

        shoppingListView = (ListView) findViewById(R.id.shoppingListView);

        shoppingList = loadArray();

       /* shoppingList.add("Tomek");
        shoppingList.add("Kasia");
        shoppingList.add("Maciek");
        shoppingList.add("Alicja"); */


        shoppingListArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shoppingList);

        shoppingListView.setAdapter(shoppingListArrayAdapter);

        shoppingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (checkMode) {

                    CheckedTextView checkedTextView = (CheckedTextView) view;

                    if (checkedTextView.isChecked()) {
                        checkedTextView.setChecked(false);
                        deleteShoppingListArray.remove(shoppingList.get(i));
                    } else {
                        checkedTextView.setChecked(true);
                        deleteShoppingListArray.add(shoppingList.get(i));
                    }

                } else {

                    Toast.makeText(getApplicationContext(), shoppingList.get(i), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, ShoppingList.class);
                    intent.putExtra("shoppingList", shoppingList.get(i));
                    startActivity(intent);
                }

            }
        });

        shoppingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (checkMode) {

                    checkMode = false;
                    shoppingListArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, shoppingList);
                    shoppingListView.setAdapter(shoppingListArrayAdapter);

                } else {

                    deleteShoppingListArray.clear();
                    checkMode = true;
                    shoppingListArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_checked, shoppingList);
                    shoppingListView.setAdapter(shoppingListArrayAdapter);

                }

                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.shopping_list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.deleteAllSelectedShoppingLists:
                showDeleteShoppingListAlert();
                return true;
            default:
                return false;
        }
    }

    private void showDeleteShoppingListAlert() {

        if (deleteShoppingListArray.isEmpty()) {

            Toast.makeText(this, "Nie zaznaczno żadnego elementu. Aby to zrobić, przytrzymaj dłużej na danej liście zakupów", Toast.LENGTH_SHORT).show();

        } else {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Usuwanie listy zakupów")
                    .setMessage("Czy na pewno chcesz usunąć zaznaczone elementy?")
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteShoppingLists();
                            Toast.makeText(MainActivity.this, "Usunięto", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Nie", null)
                    .show();
        }

    }

    private void deleteShoppingLists() {

        Database db = new Database(this);
        db.deleteShoppingLists(deleteShoppingListArray);

        for (String list : deleteShoppingListArray) {
            shoppingList.remove(list);
        }

        sharedPreferences.edit().clear();
        saveArray();

        checkMode = false;
        shoppingListArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, shoppingList);
        shoppingListView.setAdapter(shoppingListArrayAdapter);

        shoppingListArrayAdapter.notifyDataSetChanged();
    }

    public void showAddShoppingListPopUp(View view) {

        final Button cancelShoppingListPopUpButton;
        final Button addShoppingListPopUpButton;
        final Dialog addShoppingListDialog = new Dialog(this);

        addShoppingListDialog.setContentView(R.layout.add_shopping_list);

        cancelShoppingListPopUpButton = (Button) addShoppingListDialog.findViewById(R.id.cancelShoppingListPopUpButton);
        addShoppingListPopUpButton = (Button) addShoppingListDialog.findViewById(R.id.addShoppingListPopUpButton);
        shoppingListEditText = (TextView) addShoppingListDialog.findViewById(R.id.shoppingListEditText);


        cancelShoppingListPopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShoppingListDialog.dismiss();
            }
        });

        addShoppingListPopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShoppingList();
            }
        });


        shoppingListEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    addShoppingList();
                }
                return false;
            }
        });

        addShoppingListDialog.show();
        Window window = addShoppingListDialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    }

    public void addShoppingList() {

        String shoppingListName = shoppingListEditText.getText().toString();

        if (shoppingListName.matches("")) {
            Toast.makeText(this, "Proszę wprowadzić nazwę listy zakupów", Toast.LENGTH_SHORT).show();
        } else {
            if (alreadyExists(shoppingListName)) {
                Toast.makeText(this, "Istnieje już lista o nazwie: " + shoppingListName, Toast.LENGTH_SHORT).show();
            } else {
                shoppingList.add(shoppingListName);
                saveArray();
                shoppingListArrayAdapter.notifyDataSetChanged();
                Intent intent = new Intent(this, ShoppingList.class);
                intent.putExtra("shoppingList", shoppingListName);
                startActivity(intent);

            }

        }

    }

    private boolean alreadyExists(String shoppingListName) {

        for (String shoppingListArrayName : shoppingList) {

            if (shoppingListArrayName.matches(shoppingListName)) {
                return true;
            }
        }

        return false;
    }

    private void saveArray() {

        try {
            sharedPreferences.edit().putString("shoppingList", ObjectSerializer.serialize(shoppingList)).apply();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> loadArray() {

        ArrayList<String> newShoppingList = new ArrayList<>();

        try {
            newShoppingList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("shoppingList", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newShoppingList;

    }

}
