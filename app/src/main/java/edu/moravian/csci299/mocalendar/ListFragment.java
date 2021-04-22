package edu.moravian.csci299.mocalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A fragment that displays a list of events. The list is a RecyclerView. When an event on the list
 * is clicked, a callback method is called to inform the hosting activity. When an item on the list
 * is swiped, it causes the event to be deleted (see https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e).
 * This is the fragment that also controls the menu of options in the app bar.
 * <p>
 * Above the list is a text box that states the date being displayed on the list.
 * <p>
 * NOTE: Finish CalendarFragment first then work on this one. Also, look at how a few things
 * related to dates are dealt with in the CalendarFragment and use similar ideas here.
 */
public class ListFragment extends Fragment {
    // fragment initialization parameters
    private static final String ARG_DATE = "date";

    private Date date;
    private List<Event> events = Collections.emptyList();
    private TextView dateText;
    private RecyclerView listView;
    private Callbacks callbacks;

    interface Callbacks {

        void onEventSelected(Event event);
    }


    /**
     * Use this factory method to create a new instance of this fragment that
     * lists events for the given day.
     *
     * @param date the date to show the event list for
     * @return a new instance of fragment ListFragment
     */
    public static ListFragment newInstance(Date date) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Set the day for the events being listed.
     *
     * @param date the new day for the list to show events for
     */
    public void setDay(Date date) {
        this.date = date;
        assert getArguments() != null;
        getArguments().putSerializable(ARG_DATE, date);
        onDateChange();
    }

    /**
     * Upon creation need to enable the options menu and update the view for the initial date.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        date = DateUtils.useDateOrNow((Date) getArguments().getSerializable(ARG_DATE));
        onDateChange();

        setHasOptionsMenu(true);
    }

    /**
     * Create the view for this layout. Also sets up the adapter for the RecyclerView, its swipe-to-
     * delete helper, and gets the date text view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventListAdapter adapter = new EventListAdapter();

        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_list, container, false);

        // setup RecyclerView
        listView = base.findViewById(R.id.list_view);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(adapter);
        dateText = base.findViewById(R.id.date_text);

        // setup SwipeToDelete helper and attach it to the RecyclerView
        new ItemTouchHelper(new SwipeToDelete(adapter)).attachToRecyclerView(listView);

        return base; // return the base view
    }

    /**
     * When the date is changed for this fragment we need to grab a new list of events and update
     * the UI.
     */
    private void onDateChange() {
        int[] yearMonthDay = DateUtils.getYearMonthDay(date);
        // get events for currently selected date from 12 AM to 12 PM
        LiveData<List<Event>> eventDataItems = CalendarRepository.get().getEventsOnDay(DateUtils.getDate(yearMonthDay[0], yearMonthDay[1], yearMonthDay[2]));
        eventDataItems.observe(this, events -> {
            this.events = events;
            Objects.requireNonNull(listView.getAdapter()).notifyDataSetChanged();
            dateText.setText(DateUtils.toFullDateString(date));

        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    /**
     * Setup the menu for the fragment
     *
     * @param menu current menu
     * @param inflater inflater for the menu layout
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    /**
     * Creates a new Event according to the item selected in the menu.
     *
     * @param item selected MenuItem from menu
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_event) {
            Event event = new Event();
            event.startTime = date;
            event.endTime = new Date(date.getTime() + 3600000);

            CalendarRepository.get().addEvent(event);
            callbacks.onEventSelected(event);
            return true;
        } else if (item.getItemId() == R.id.new_assignment) {
            Event event = new Event();
            event.startTime = date;
            event.type = EventType.ASSIGNMENT;
            CalendarRepository.get().addEvent(event);
            callbacks.onEventSelected(event);
            return true;
        } else
            return super.onOptionsItemSelected(item);


    }



    private class EventViewHolder extends RecyclerView.ViewHolder {
        Event event;
        TextView name, description, startTime, endTime;
        ImageView typeView;

        public EventViewHolder(@NonNull View eventView) {
            super(eventView);
            name = eventView.findViewById(R.id.event_name);
            description = eventView.findViewById(R.id.event_description);
            startTime = eventView.findViewById(R.id.event_start_time);
            endTime = eventView.findViewById(R.id.event_end_time);
            typeView = eventView.findViewById(R.id.imageView);

            eventView.setOnClickListener(v -> callbacks.onEventSelected(event));
        }
    }



    /**
     * The adapter for the items list to be displayed in a RecyclerView.
     */
    private class EventListAdapter extends RecyclerView.Adapter<EventViewHolder> {
        /**
         * To create the view holder we inflate the layout we want to use for
         * each item and then return an ItemViewHolder holding the inflated
         * view.
         */
        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new EventViewHolder(v);
        }

        /**
         * When we bind a view holder to an item (i.e. use the view with a view
         * holder to display a specific item in the list) we need to update the
         * various views within the holder for our new values.
         *
         * @param holder   the ItemViewHolder holding the view to be updated
         * @param position the position in the list of the item to display
         */
        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

            Event event = events.get(position);
            holder.event = event;
            holder.name.setText(event.name);
            holder.description.setText(event.description);
            holder.startTime.setText(DateUtils.toTimeString(event.startTime));
            holder.typeView.setImageResource(event.type.iconResourceId);
            if (event.endTime != null) { holder.endTime.setText(DateUtils.toTimeString(event.endTime)); }
        }

        /**
         * @return the total number of items to be displayed in the list
         */
        @Override
        public int getItemCount() {
            return events.size();
        }
    }


    /**
     * Allows RecyclerView items to be swiped away and deleted.
     * Must be attached to the RecyclerView.
     */
    private class SwipeToDelete extends ItemTouchHelper.SimpleCallback {
        private EventListAdapter adapter;
        private Drawable icon;
        private final ColorDrawable background;

        public SwipeToDelete(EventListAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.adapter = adapter;
            icon = ContextCompat.getDrawable(getContext(),
                    R.drawable.ic_delete_white);
            background = new ColorDrawable(Color.RED);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) { return false; }

        /**
         * Delete an item from the database when swiped in the fragment
         *
         * @param viewHolder uses viewHolder to get the position
         * @param direction direction the view is being swiped
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            CalendarRepository.get().removeEvent(events.get(position)); // delete from database
            adapter.notifyItemRemoved(position);
        }

        /**
         * Set colored background and image behind the item in recyclerView that is shown when swiped
         *
         * @param c canvas being used
         * @param recyclerView recyclerView being used
         * @param viewHolder viewHolder being used
         * @param dX
         * @param dY
         * @param actionState
         * @param isCurrentlyActive
         */
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView

            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                int iconRight = itemView.getLeft() + iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                icon.setBounds(0, 0, 0, 0);
                background.setBounds(0, 0, 0, 0);
            }

            background.draw(c);
            icon.draw(c);
        }
    }
}