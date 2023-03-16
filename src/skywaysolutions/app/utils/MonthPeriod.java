package skywaysolutions.app.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Provides a month period class, representing a single month.
 *
 * @author Alfred Manville
 */
public final class MonthPeriod {
    private final int _month;
    private final int _year;

    /**
     * Creates a new MonthPeriod with the specified month and year.
     *
     * @param month The month.
     * @param year The year.
     */
    public MonthPeriod(int month, int year) {
        _month = month;
        _year = year;
    }

    /**
     * Gets the month of the period.
     *
     * @return The month.
     */
    public int getMonth() {
        return _month;
    }

    /**
     * Gets the year of the period.
     *
     * @return The year.
     */
    public int getYear() {
        return _year;
    }

    /**
     * Checks if the passed date is within the period.
     *
     * @param date The date to check.
     * @return If the date is within the month period.
     */
    public boolean dateInPeriod(Date date) {
        Calendar cal = Time.getCalendar(date);
        return _month == cal.get(Calendar.MONTH) + 1 && _year == cal.get(Calendar.YEAR);
    }
}
