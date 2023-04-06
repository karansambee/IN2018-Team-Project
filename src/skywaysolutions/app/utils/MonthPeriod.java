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

    /**
     * Gets the beginning of the current month represented by this period.
     *
     * @return The date of the beginning of this month.
     */
    public Date getThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(_year, _month - 1, 1);
        return cal.getTime();
    }

    /**
     * Gets the beginning of the month after this period.
     *
     * @return The date of the beginning of the next month.
     */
    public Date getNextMonth() {
        Calendar cal = Calendar.getInstance();
        if (_month > 10) {
            cal.set(_year+1,0, 1);
        } else {
            cal.set(_year, _month, 1);
        }
        return cal.getTime();
    }
}
