package skywaysolutions.app.utils;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Time utilities class.
 *
 * @author Alfred Manville
 */
public final class Time {
    /**
     * Gets the current Date (Including the time).
     *
     * @return The current Date.
     */
    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    /**
     * Gets the calendar object representing the passed date.
     *
     * @param date The date.
     * @return The calendar object.
     */
    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Gets the SQL Date from a date.
     *
     * @param date The date to convert.
     * @return The SQL Date.
     */
    public static java.sql.Date toSQLDate(Date date) {
        if (date == null) return null;
        return java.sql.Date.valueOf(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    /**
     * Gets the date from an SQL Date.
     *
     * @param date The SQL Date to convert.
     * @return The date.
     */
    public static Date fromSQLDate(java.sql.Date date) {
        if (date == null) return null;
        return Date.from(date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Gets the date object of the given date information.
     *
     * @param year The year.
     * @param month The month (1-12).
     * @param dayOfMonth The day of the month (From 1).
     * @return The date representing the passed information.
     */
    public static Date getDate(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, dayOfMonth);
        return cal.getTime();
    }
}
