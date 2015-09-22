package com.lsilberstein.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    private int position;
    private String item; // maybe this doesn't need to be a member variable
    private EditText etEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // pull the info we pass from the main activity
        item = getIntent().getStringExtra(MainActivity.ITEM_KEY);
        position = getIntent().getIntExtra(MainActivity.POSITION_KEY,-1); // -1 indicates an error
        prepareScreen();
    }

    /*
    sets the appropriate value for our text field and puts the cursor at the end of the current text
     */
    private void prepareScreen() {
        etEditText = (EditText) findViewById(R.id.etEditText);
        etEditText.setText(item);
        etEditText.setSelection(item.length());
        etEditText.requestFocus();
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
        Intent data = new Intent();
        data.putExtra(MainActivity.POSITION_KEY, position);
        data.putExtra(MainActivity.ITEM_KEY, etEditText.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }
}
