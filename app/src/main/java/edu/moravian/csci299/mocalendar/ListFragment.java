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

import android.util.Log;
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

    // data
    private LiveData<List<Event>> eventDataItems;
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
     * lists events for today.
     *
     * @return a new instance of fragment ListFragment
     */
    public static ListFragment newInstance() {
        return newInstance(new Date());
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
        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_list, container, false);
        listView = base.findViewById(R.id.list_view); // Have to init recycler view
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        EventListAdapter adapter = new EventListAdapter();
        listView.setAdapter(adapter);
        dateText = base.findViewById(R.id.date_text);

        return base; // return the base view
    }

    /**
     * When the date is changed for this fragment we need to grab a new list of events and update
     * the UI.
     */
    private void onDateChange() {
        Log.e("onDateChangeDate", DateUtils.toTimeString(date));
        int[] yearMonthDay = DateUtils.getYearMonthDay(date);

        eventDataItems = CalendarRepository.get().getEventsOnDay(DateUtils.getDate(yearMonthDay[0], yearMonthDay[1], yearMonthDay[2]));
        eventDataItems.observe(this, events -> {
            this.events = events;
            Log.e("onDateChangeEvents", events.toString());
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
            event.endTime = new Date(date.getTime() + 3600000);

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
            holder.startTime.setText(DateUtils.toTimeString(date));
            holder.typeView.setImageResource(event.type.iconResourceId);
            if (event.endTime != null) {
                holder.endTime.setText(DateUtils.toTimeString(date));
            }

        }

        /**
         * @return the total number of items to be displayed in the list
         */
        @Override
        public int getItemCount() {
            return events.size();
        }

        private Context context = getContext();

        /**
         * @return the Context for the adapter
         */
        public Context getContext() {
            return context;
        }
    }

// TODO: some code for the swipe-to-delete


    private class SwipeToDelete extends ItemTouchHelper.SimpleCallback {

        private ListFragment.EventListAdapter mAdapter;

        private Drawable icon;
        private final ColorDrawable background;


        public SwipeToDelete(ListFragment.EventListAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            mAdapter = adapter;
            // TODO this might work...
            icon = ContextCompat.getDrawable(mAdapter.getContext(),
                    R.drawable.ic_delete_white);
            background = new ColorDrawable(Color.RED);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // used for up and down movements
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // TODO this might not work
            CalendarRepository.get().removeEvent(events.get(viewHolder.getAdapterPosition()));

        }

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
                // TODO try with above code, then below. Below should fix a bug
//                int iconLeft = itemView.getLeft() + iconMargin;
//                int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
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


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

}