package edu.moravian.csci299.mocalendar;

import androidx.room.TypeConverter;

import java.util.Date;

public class CalendarTypeConverter {
    @TypeConverter
    public Long fromDate(Date date) {
        if (date == null) { return null; }
        return date.getTime();
    }

    @TypeConverter
    public Date toDate(Long ms) {
        if (ms == null) { return null; }
        return new Date(ms);
    }

}
