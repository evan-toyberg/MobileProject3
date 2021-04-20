package edu.moravian.csci299.mocalendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.util.Date;
import java.util.Objects;

/**
 * A fragment that acts as a popup window for picking a time. Any fragment that
 * uses this must implement the Callbacks interface defined here and set the
 * target fragment before showing it.
 * <p>
 * HINTS: use the DatePickerFragment as inspiration for completing this one.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    /**
     * The name of the argument for the start time (a boolean)
     */
    private static final String ARG_IS_START_TIME = "is_start_time";

    /**
     * The name of the argument for the time (a Date object)
     */
    private static final String ARG_TIME = "time";

    interface Callbacks {
        void onTimeChanged(Boolean isStartTime, Date date);
    }

    public static TimePickerFragment newInstance(Boolean isStartTime, Date time) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);
        args.putBoolean(ARG_IS_START_TIME, isStartTime);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        Date time = DateUtils.useDateOrNow((Date) getArguments().get(ARG_TIME));
        int[] hourMinute = DateUtils.getHourMinute(time);
        return new TimePickerDialog(requireContext(), this, hourMinute[0], hourMinute[1], false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        ((TimePickerFragment.Callbacks) Objects.requireNonNull(getTargetFragment()))
                .onTimeChanged(getArguments().getBoolean(ARG_IS_START_TIME), DateUtils.getTime(hourOfDay, minute));

    }
}
