package edu.moravian.csci299.mocalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * The main (and only) activity for the application that hosts all of the fragments.
 *
 * It starts out with a calendar and list fragment (if vertical then they are above/below each
 * other, if horizontal then they are left/right of each other). When a day is clicked in the
 * calendar, the list shows all events for that day.
 *
 * When an event is being edited/viewed (because it was clicked in the list or a new event is being
 * added) then the fragments are replaced with an event fragment which shows the details for a
 * specific event and allows editing.
 *
 * NOTE: This Activity is the bare-bones, empty, Activity. Work will be definitely needed in
 * onCreate() along with implementing some callbacks.
 */


/*
TODO:
   1. create an instance of a calendar object
   2. create a list fragment
   3. create an onClick for each day on the calendar
        3a. in onClick, get events for that day from the database
   4. replace fragment every time it is clicked or edited
   5. Implement callbacks
 */


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



}
