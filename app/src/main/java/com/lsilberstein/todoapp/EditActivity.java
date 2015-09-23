package com.lsilberstein.todoapp;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.lsilberstein.todoapp.data.TodoItem;

import java.util.Calendar;
import java.util.UUID;

public class EditActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{

    private TodoItem item;
    private EditText etShortName;
    private Spinner spPriority;
    private EditText etDetails;
    private Button btnSetDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // pull the info we pass from the main activity
        String id = getIntent().getStringExtra(MainActivity.ITEM_KEY);
        item = TodoItem.getItem(UUID.fromString(id));
        prepareScreen();
    }

    /*
    sets the appropriate value for our text field and puts the cursor at the end of the current text
     */
    private void prepareScreen() {
        prepareSpinner();
        prepareShortName();
        prepareDetails();
        prepareDate();
    }

    private void prepareDate() {
        btnSetDate = (Button) findViewById(R.id.btnSetDate);
        btnSetDate.setText(MainActivity.formater.format(item.dueDate.getTime()));
    }

    private void prepareDetails() {
        etDetails = (EditText) findViewById(R.id.etDetails);
        etDetails.setText(item.details);
    }

    private void prepareShortName() {
        String sn = item.shortName;
        etShortName = (EditText) findViewById(R.id.etShortName);
        etShortName.setText(sn);
        etShortName.setSelection(sn.length());
        etShortName.requestFocus();
    }

    private void prepareSpinner() {
        spPriority = (Spinner) findViewById(R.id.spPriority);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priorities_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spPriority.setAdapter(adapter);
        Integer p = item.priority;
        // default to P4 if no priority set
        spPriority.setSelection(p != null ? p : 3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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

    // Saves the changes to the item and returns the user to the main screen
    public void onSave(View view) {
        item.priority = spPriority.getSelectedItemPosition();
        item.shortName = etShortName.getText().toString();
        item.details = etDetails.getText().toString();
        item.save();
        backToMain();
    }

    // Delete this item from the database and returns to the main screen
    public void onDelete(View view) {
        item.delete();
        backToMain();
    }

    // Sends the completion intent back to the main screen
    private void backToMain() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar dueDate = item.dueDate;
        dueDate.set(year, monthOfYear, dayOfMonth);
        item.save();
        btnSetDate.setText(MainActivity.formater.format(item.dueDate.getTime()));
    }

    public void onSetDate(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.ITEM_KEY, item.dueDate);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
