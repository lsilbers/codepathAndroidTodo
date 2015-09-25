package com.lsilberstein.todoapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
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

public class MainActivity extends AppCompatActivity implements EditDialog.EditDialogListener, DetailsFragment.OnFragmentInteractionListener {
    private static final String DETAILS_FRAGMENT = "DETAILS_FRAGMENT";
    public static SimpleDateFormat formater = new SimpleDateFormat("MM/dd");
    public static final String ITEM_KEY = "item";
    private static final int REQUEST_CODE = 1;

    private View selectedItem;
    private TodoArrayAdapter todoArrayAdapter;
    private ArrayList<TodoItem> todoItems;
    private ListView lvItems;
    private EditText etNewItem;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.color, typedValue, true);
        color = typedValue.data;

        // set up initial data
        todoItems = new ArrayList<>(TodoItem.getItems());
        todoArrayAdapter = new TodoArrayAdapter(this, todoItems);

        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(todoArrayAdapter);
        etNewItem = (EditText) findViewById(R.id.etNewItem);

        // when an item is clicked we display the details fragment and highlight the item
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedItem != null) {
                    selectedItem.setBackgroundColor(color);
                }
                selectedItem = view;
                view.setBackgroundColor(Color.GRAY);
                // Begin the transaction
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frmDetails, DetailsFragment.newInstance(todoItems.get(position).details),DETAILS_FRAGMENT);
                ft.commit();
            }
        });

        // when an item is long clicked we send an edit intent for the clicked item
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDialog(todoItems.get(position));
                return true;
            }
        });
    }

    // Sends an intent to edit the item given by the specified id.
    private void showEditDialog(TodoItem item) {
        EditDialog edit = EditDialog.newInstance(item);
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
        showEditDialog(newItem);
    }

    @Override
    public void onEditFinished() {
        refreshData();
        clearSelection();
    }

    @Override
    public void onDetailsFragmentInteraction(int action) {
        // called on click of the close icon
        if (action == DetailsFragment.DETAILS_CLOSE_ACTION) {
            clearSelection();
        }
    }

    private void clearSelection() {
        // unselect the item
        if (selectedItem != null) {
            selectedItem.setBackgroundColor(color);
            selectedItem = null;
            // destroy the fragment
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(getFragmentManager().findFragmentByTag(DETAILS_FRAGMENT));
            ft.commit();
        }
    }
}
