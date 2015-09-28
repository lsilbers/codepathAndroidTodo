package com.lsilberstein.todoapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.lsilberstein.todoapp.data.TodoItem;

import java.util.Calendar;

public class EditDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    public static final int PHOTO_CODE = 1046;
    private static final String IMAGE_EXTENSION = ".jpg";
    private static final String TAG = "edit_dialog";

    private TodoItem item;
    private EditText etShortName;
    private Spinner spPriority;
    private EditText etDetails;
    private Button btnSetDate;
    private EditDialogListener listener;
    private MainActivity mainActivity;

    // calling class should implement this interface to recieve notification of dialog actions
    public interface EditDialogListener {

        /**
         * Called when an action is taken that closes the dialog
         */
        public void onEditFinished();
    }

    // Empty constructor - use newInstance instead
    public EditDialog() {}

    /**
     * Use this method to create new EditDialogs
     * @param item - the item that is being edited
     * @return a new EditDialog to be anle to make changes to that item
     */
    public static EditDialog newInstance(TodoItem item) {
        Log.d(TAG, "created a new edit dialog with item id " + item.remoteId.toString());
        EditDialog frag = new EditDialog();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.ITEM_KEY, item);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        if (mainActivity instanceof EditDialogListener) {
            this.listener = (EditDialogListener) mainActivity;
        }
        return inflater.inflate(R.layout.fragment_edit, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title
        item = (TodoItem) getArguments().getSerializable(MainActivity.ITEM_KEY);
        prepareScreen(view);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /*
    sets the appropriate value for our text field and puts the cursor at the end of the current text
     */
    private void prepareScreen(View view) {
        prepareSpinner(view);
        prepareShortName(view);
        prepareDetails(view);
        prepareDate(view);
        view.findViewById(R.id.btnSaveEdit).setOnClickListener(this);
        view.findViewById(R.id.btnDelete).setOnClickListener(this);
        view.findViewById(R.id.btnSetPhoto).setOnClickListener(this);
    }

    // Prepares the date button with the appropriate date
    private void prepareDate(View view) {
        btnSetDate = (Button) view.findViewById(R.id.btnSetDate);
        btnSetDate.setText(MainActivity.formater.format(item.dueDate.getTime()));
        btnSetDate.setOnClickListener(this);
    }

    // populates the details text field
    private void prepareDetails(View view) {
        etDetails = (EditText) view.findViewById(R.id.etDetails);
        etDetails.setText(item.details);
        if (item.image != null) {
            setBackgroundImage();
        }
    }

    // populates the title name
    private void prepareShortName(View view) {
        String sn = item.shortName;
        etShortName = (EditText) view.findViewById(R.id.etShortName);
        etShortName.setText(sn);
        etShortName.setSelection(sn.length());
        etShortName.requestFocus();
    }

    // populates the priority spinner selection
    private void prepareSpinner(View view) {
        spPriority = (Spinner) view.findViewById(R.id.spPriority);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.priorities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(adapter);
        Integer p = item.priority;
        // default to P4 if no priority set
        spPriority.setSelection(p != null ? p : 3);
    }

    // OnClickListener Interface method - called on a view click event
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDelete:
                onDelete();
                break;
            case R.id.btnSaveEdit:
                onSave();
                break;
            case R.id.btnSetDate:
                onSetDate();
                break;
            case R.id.btnSetPhoto:
                onSetPhoto();
                break;
            default:
                break;
        }
    }

    // called when the save button is clicked
    public void onSave() {
        item.priority = spPriority.getSelectedItemPosition();
        item.shortName = etShortName.getText().toString();
        item.details = etDetails.getText().toString();
        item.save();
        listener.onEditFinished();
        dismiss();
    }

    // called when the delete button is clicked
    public void onDelete() {
        item.delete();
        listener.onEditFinished();
        dismiss();
    }

    // called when the date button is clicked
    public void onSetDate() {
        Calendar dueDate = item.dueDate;
        int year = dueDate.get(Calendar.YEAR);
        int month = dueDate.get(Calendar.MONTH);
        int day = dueDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(),this, year, month, day);
        datePicker.show();
    }

    // OnDateSetListener interface method - called by the date picker dialog
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar dueDate = item.dueDate;
        dueDate.set(year, monthOfYear, dayOfMonth);
        item.save();
        btnSetDate.setText(MainActivity.formater.format(item.dueDate.getTime()));
    }

    // called when the camera button is clicked
    private void onSetPhoto() {
        item.image = item.remoteId.toString() + IMAGE_EXTENSION;
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mainActivity.getPhotoFileUri(item.image)); // set the image file name
        // Start the image capture intent to take photo
        startActivityForResult(intent, PHOTO_CODE);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "successfully took a photo. Filename : " + item.image);
                setBackgroundImage();
            } else { // Result was a failure
                item.image = null;
                Log.d(TAG, "Failed to take a photo for item id " + item.remoteId.toString());
                Toast.makeText(mainActivity, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setBackgroundImage() {
        Uri imageUri = mainActivity.getPhotoFileUri(item.image);
        Log.d(TAG, "Got uri for image " + item.image + ". Uri: " + imageUri.getPath());
        // by this point we have the camera photo on disk
        Bitmap image = BitmapFactory.decodeFile(imageUri.getPath());
        etDetails.setBackground(new BitmapDrawable(image));
    }
}
