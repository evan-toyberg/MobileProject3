package edu.moravian.csci299.mocalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.Date;

/**
 * The main (and only) activity for the application that hosts all of the fragments.
 * <p>
 * It starts out with a calendar and list fragment (if vertical then they are above/below each
 * other, if horizontal then they are left/right of each other). When a day is clicked in the
 * calendar, the list shows all events for that day.
 * <p>
 * When an event is being edited/viewed (because it was clicked in the list or a new event is being
 * added) then the fragments are replaced with an event fragment which shows the details for a
 * specific event and allows editing.
 * <p>
 * NOTE: This Activity is the bare-bones, empty, Activity. Work will be definitely needed in
 * onCreate() along with implementing some callbacks.
 */
public class MainActivity extends AppCompatActivity implements CalendarFragment.Callbacks, ListFragment.Callbacks {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();  // get fragment manager
        FragmentTransaction transaction = fm.beginTransaction();  // begin transaction with fragment manager
        CalendarFragment calendarFragment = (CalendarFragment) fm.findFragmentById(R.id.calendarView);  // get calendar fragment
        if (calendarFragment == null) {
            calendarFragment = CalendarFragment.newInstance();
        }  // create new instance of CalendarFragment if it cannot be found
        ListFragment listFragment = ListFragment.newInstance();  // get new list fragment
        transaction.add(R.id.fragment_container, calendarFragment).add(R.id.fragment_container, listFragment).commit(); // add both fragments to main view

        // todo : still need to figure out how to get clicks on dates to register and show events for current date
        // Also make sure the fragments show up in correct positions


    }


    @Override
    public void onDayChanged(Date date) {

    }

    @Override
    public void onEventSelected(EventType type) {

    }
}
