package edu.moravian.csci299.mocalendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.Objects;

/**
 * A fragment that acts as a popup window for picking a time. Any fragment that
 * uses this must implement the Callbacks interface defined here and set the
 * target fragment before showing it.
 *
 * HINTS: use the DatePickerFragment as inspiration for completing this one.
 */
public class  TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    /** The name of the argument for the start time (a boolean) */
    private static final String ARG_IS_START_TIME = "is_start_time";

    /** The name of the argument for the time (a Date object) */
    private static final String ARG_TIME = "time";

    interface Callbacks {
        void onTimeChanged(Date date);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = DateUtils.useDateOrNow((Date)getArguments().getSerializable(ARG_TIME));
        int[] time = DateUtils.getHourMinute(date);
        return new TimePickerDialog(requireContext(), this, time[0], time[1], true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        getArguments().getBoolean(ARG_IS_START_TIME);
        ((TimePickerFragment.Callbacks) Objects.requireNonNull(getTargetFragment())).onTimeChanged(DateUtils.getTime(hourOfDay,minute));
        //onTimeSelected(); might need this once method is created
    }
}
