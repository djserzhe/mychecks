package ru.serzhe.mychecks.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {

    private Date currentValue;

    public void setCurrentValue(Date dt) {
        currentValue = dt;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        c.setTime(currentValue);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
        datePickerDialog.getDatePicker().setTag(getTag());

        return datePickerDialog;
    }

}
