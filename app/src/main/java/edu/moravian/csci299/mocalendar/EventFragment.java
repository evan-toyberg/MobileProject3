package edu.moravian.csci299.mocalendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.UUID;

/**
 * The fragment for a single event. It allows editing all of the details of the event, either with
 * text edit boxes (for the name and description) or popup windows (for the date, start time,
 * time and type). The event is not updated in the database until the user leaves this fragment.
 */
public class EventFragment extends Fragment implements TextWatcher, EventTypePickerFragment.Callbacks, DatePickerFragment.Callbacks, TimePickerFragment.Callbacks {

    // fragment initialization parameters
    private static final String ARG_EVENT_ID = "event_id";

    // dialog fragment tags
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_EVENT_TYPE = "DialogEventType";

    // dialog fragment codes
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_EVENT_TYPE = 2;

    // argument once loaded from database
    private Event event;
    private TextView dateView, endTimeView, startTimeView, tillView;
    private EditText description, eventNameView;

    private ImageView typeView;

    /**
     * Use this factory method to create a new instance of this fragment that
     * show the details for the given event.
     *
     * @param event the event to show information about
     * @return a new instance of fragment EventFragment
     */
    public static EventFragment newInstance(Event event) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, event.id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Upon creation load the data. Once the data is loaded, update the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        CalendarRepository.get().getEventById((UUID) getArguments()
                .getSerializable(ARG_EVENT_ID)).observe(this, event -> {
            this.event = event;
            updateUI();
        });


    }

    /**
     * Create the view from the layout, save references to all of the important
     * views within in, then hook up the listeners.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_event, container, false);


        description = base.findViewById(R.id.description);
        description.addTextChangedListener(this);

        typeView = base.findViewById(R.id.eventTypeIcon);
        typeView.setOnClickListener(v -> showEventTypePicker());


        tillView = base.findViewById(R.id.tillView);

        startTimeView = base.findViewById(R.id.startTime);
        startTimeView.setOnClickListener(v -> showTimePicker(true));


        endTimeView = base.findViewById(R.id.endTime);
        endTimeView.setOnClickListener(v -> showTimePicker(false));

        dateView = base.findViewById(R.id.dateView);
        dateView.setOnClickListener(v -> showDatePicker());
        eventNameView = base.findViewById(R.id.eventTypeName);
        eventNameView.addTextChangedListener(this);


        // Return the base view
        return base;
    }


    /**
     * Save the edits to the database when the fragment is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
        CalendarRepository.get().updateEvent(event);
    }

    /**
     * Updates the UI to match the event.
     */
    private void updateUI() {
        typeView.setImageResource(event.type.iconResourceId);
        eventNameView.setText(event.name);
        dateView.setText(DateUtils.toFullDateString(event.startTime));
        startTimeView.setText(DateUtils.toTimeString(event.startTime));
        if (event.type == EventType.ASSIGNMENT) {
            tillView.setVisibility(View.GONE);
        } else {
            endTimeView.setText(DateUtils.toTimeString(event.endTime));
        }


        description.setText(event.description);

    }

    private void showTimePicker(Boolean isStartTime) {
        TimePickerFragment picker;
        if (isStartTime) {
            picker = TimePickerFragment.newInstance(true, event.startTime);
        } else
            picker = TimePickerFragment.newInstance(false, event.endTime);

        picker.setTargetFragment(this, REQUEST_TIME);
        picker.show(requireFragmentManager(), DIALOG_TIME);
    }

    private void showDatePicker() {
        DatePickerFragment picker = DatePickerFragment.newInstance(event.startTime);
        picker.setTargetFragment(this, REQUEST_DATE);
        picker.show(requireFragmentManager(), DIALOG_DATE);
    }

    private void showEventTypePicker() {
        EventTypePickerFragment picker = EventTypePickerFragment.newInstance(event.type);
        picker.setTargetFragment(this, REQUEST_EVENT_TYPE);
        picker.show(requireFragmentManager(), DIALOG_EVENT_TYPE);
    }


    /**
     * When an EditText updates we update the corresponding Event field. Need to register this
     * object with the EditText objects with addTextChangedListener(this).
     *
     * @param s the editable object that just updated, equal to some EditText.getText() object
     */
    @Override
    public void afterTextChanged(Editable s) {
        event.name = eventNameView.getText().toString();
        event.description = description.getText().toString();
    }

    /**
     * Required to be implemented but not needed.
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * Required to be implemented but not needed.
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void onTimeChanged(Boolean isStartTime, Date date) {
        if (event.type != EventType.ASSIGNMENT) {
            if (isStartTime) {
                Date originalStart = event.startTime;
                event.startTime = DateUtils.combineDateAndTime(event.startTime, date);
                event.endTime = DateUtils.getNewEndTime(originalStart, event.startTime, event.endTime);
            } else {
                event.endTime = DateUtils.fixEndTime(event.startTime, date);
            }
        } else {
            event.startTime = DateUtils.combineDateAndTime(event.startTime, date);
        }
        updateUI();
    }

    @Override
    public void onDateSelected(Date date) {
        event.startTime = date;
        if (event.type != EventType.ASSIGNMENT) {
            event.endTime = date;
        }
        updateUI();
    }

    @Override
    public void onTypeSelected(EventType type) {
        event.type = type;
        updateUI();
    }
}
