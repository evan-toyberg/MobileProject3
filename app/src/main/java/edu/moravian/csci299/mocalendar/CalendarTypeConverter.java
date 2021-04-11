package edu.moravian.csci299.mocalendar;

import androidx.room.TypeConverter;

import java.util.Date;
import java.util.UUID;

public class CalendarTypeConverter {
    /**
     * Convert Date object to a Long usable by the database
     *
     * @param date Date object to be converted to a Long
     * @return Long value of Date parameter
     */
    @TypeConverter
    public static Long fromDate(Date date) {
        if (date == null) { return null; }
        return date.getTime();
    }

    /**
     * Convert date in ms to Date object
     *
     * @param ms the date represented in ms
     * @return date represented as Date object
     */
    @TypeConverter
    public static Date toDate(Long ms) {
        if (ms == null) { return null; }
        return new Date(ms);
    }

    /**
     * Converts a UUID to a String usable by the database
     *
     * @param uuid The UUID object to be converted to a String
     * @return String value of the UUID
     */
    @TypeConverter
    public static String fromUuid(UUID uuid) {
        if (uuid == null) { return null; }
        return uuid.toString();
    }

    /**
     * Convert a UUID from a String used in the database to a UUID
     *
     * @param uuid String value of UUID
     * @return UUID object of String parameter
     */
    @TypeConverter
    public static UUID toUuid(String uuid) {
        if (uuid == null) { return null; }
        return UUID.fromString(uuid);
    }

}
