package com.lsilberstein.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.lsilberstein.todoapp.data.TodoArrayAdapter;
import com.lsilberstein.todoapp.data.TodoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements EditDialog.EditDialogListener {
    public static SimpleDateFormat formater = new SimpleDateFormat("MM/dd");
    public static final String ITEM_KEY = "item";
    private static final int REQUEST_CODE = 1;

    private TodoArrayAdapter todoArrayAdapter;
    private ArrayList<TodoItem> todoItems;
    private ListView lvItems;
    private EditText etNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up initial data
        todoItems = new ArrayList<>(TodoItem.getItems());
        todoArrayAdapter = new TodoArrayAdapter(this, todoItems);

        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(todoArrayAdapter);
        etNewItem = (EditText) findViewById(R.id.etNewItem);

        // when an item is clicked we send an edit intent for the clicked item
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDialog(todoItems.get(position).remoteId.toString());
            }
        });
    }

    // Sends an intent to edit the item given by the specified id.
    private void showEditDialog(String id) {
        EditDialog edit = EditDialog.newInstance(id);
        edit.show(getFragmentManager(), "edit_dialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check that it was ok
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            // reload data from db
            refreshData();
        }
    }

    // Reloads the data from the database
    private void refreshData() {
        todoArrayAdapter.clear();
        todoItems = new ArrayList<>(TodoItem.getItems());
        todoArrayAdapter.addAll(todoItems);
        todoArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // click handler for the add button
    public void onAddItem(View view) {
        String shortName = etNewItem.getText().toString();
        TodoItem newItem = new TodoItem(shortName, null, 3, Calendar.getInstance());
        newItem.save();
        etNewItem.setText("");
        showEditDialog(newItem.remoteId.toString());
    }

    @Override
    public void onEditFinished() {
        refreshData();
    }
}
