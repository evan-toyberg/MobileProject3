package edu.moravian.csci299.mocalendar;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CalendarRepository {
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

    public LiveData<List<Event>> getAllEvents() {
        return calendarDao.getAllEvents();
    }

    public LiveData<Event> getEventById(UUID id) {
        return calendarDao.getEventById(id);
    }

    public LiveData<List<Event>> getEventsBetween(Date start, Date end) {
        return calendarDao.getEventsBetween(start, end);
    }

    public LiveData<List<Event>> getEventsOnDay(Date day) {
        return calendarDao.getEventsOnDay(day);
    }

    public void addEvent(Event event) {
        executor.execute(() -> {
            calendarDao.addEvent(event);
        });
    }

    public void removeEvent(Event event) {
        executor.execute(() -> {
            calendarDao.removeEvent(event);
        });
    }

    public void updateEvent(Event event) {
        executor.execute(() -> {
            calendarDao.updateEvent(event);
        });
    }

    // The single instance of the repository
    private static CalendarRepository INSTANCE;

    public static CalendarRepository get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("CalendarRepository must be initialized");
        }
        return INSTANCE;
    }

    public static void initialize(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CalendarRepository(context);
        }
    }
}
