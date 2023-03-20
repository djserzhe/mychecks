package ru.serzhe.mychecks.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    private Date currentValue;

    public void setCurrentValue(Date dt) {
        currentValue = dt;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        c.setTime(currentValue);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(), hour, minute,
                true);
    }

}
