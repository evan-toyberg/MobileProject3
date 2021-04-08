package edu.moravian.csci299.mocalendar;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CalendarRepository {
    // Internal singleton fields of the repository
    private final CalendarDataBase database;
    private final CalendarDao calendarDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private CalendarRepository(Context context) {
        database = Room.databaseBuilder(
                context.getApplicationContext(),
                CalendarDataBase.class,
                "calendar_database").build();
        calendarDao = database.calendarDao();
    }

    public LiveData<List<Event>> getAllItems() { return calendarDao.getAllEvents(); }
    public LiveData<Event> getItem(UUID id) { return calendarDao.getEventById(id); }

    public void addItems(Event event) {
        executor.execute(() -> {
            calendarDao.addEvent(event);
        });
    }
    public void updateItem(Event event) {
        executor.execute(() -> {
            calendarDao.updateEvent(event);
        });
    }

    // The single instance of the repository
    private static CalendarRepository INSTANCE;
    public static CalendarRepository get() {
        if (INSTANCE == null) { throw new IllegalStateException("CollectibleRepository must be initialized"); }
        return INSTANCE;
    }
    public static void initialize(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CalendarRepository(context);
        }
    }
}
