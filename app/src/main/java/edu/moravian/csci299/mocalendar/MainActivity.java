package edu.moravian.csci299.mocalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.Date;
import java.util.UUID;

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
    private ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        CalendarFragment calendarFragment = (CalendarFragment) fm.findFragmentById(R.id.calendarView);

        listFragment = (ListFragment) fm.findFragmentByTag("list");

        if (calendarFragment == null) {
            calendarFragment = CalendarFragment.newInstance();
        }
        listFragment = ListFragment.newInstance(new Date());
        transaction.add(R.id.fragment_container, calendarFragment).add(R.id.fragment_container, listFragment).commit();

    }

    @Override
    public void onDayChanged(Date date) {
        listFragment.setDay(date);
    }

    @Override
    public void onEventSelected(Event event) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, EventFragment.newInstance(event))
                .addToBackStack(null)
                .commit();
    }
}
