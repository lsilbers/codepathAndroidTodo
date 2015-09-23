package com.lsilberstein.todoapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    Calendar dueDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dueDate = (Calendar) getArguments().get(MainActivity.ITEM_KEY);
        // Use the current date as the default date in the picker
        int year = dueDate.get(Calendar.YEAR);
        int month = dueDate.get(Calendar.MONTH);
        int day = dueDate.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}
