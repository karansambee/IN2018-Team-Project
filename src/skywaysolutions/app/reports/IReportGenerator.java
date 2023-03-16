package skywaysolutions.app.reports;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.MonthPeriod;

import java.awt.*;

/**
 * This interface provides a way to generate reports.
 *
 * @author Alfred Manville
 */
public interface IReportGenerator {
    /**
     * Generates a report based on the current period.
     *
     * @return The generated report image for display.
     * @throws CheckedException A report generation error has occurred.
     */
    Image generate() throws CheckedException;

    /**
     * Generates a report based on the current period and the passed subject ID.
     *
     * @param subject The ID.
     * @return The generated report image for display.
     * @throws CheckedException A report generation error has occurred.
     */
    Image generateFor(long subject) throws CheckedException;

    /**
     * Generates a report based on the current period.
     *
     * @return The generated report table.
     * @throws CheckedException A report generation error has occurred.
     */
    TableCell[][] generateTables() throws CheckedException;

    /**
     * Generates a report based on the current period and the passed subject ID.
     *
     * @param subject The ID.
     * @return The generated report table.
     * @throws CheckedException A report generation error has occurred.
     */
    TableCell[][] generateTablesFor(long subject) throws CheckedException;

    /**
     * Sets the report period month.
     *
     * @param period The month of the report.
     * @throws CheckedException Setting the month period has failed.
     */
    void setPeriod(MonthPeriod period) throws CheckedException;

    /**
     * Gets the current report period month.
     *
     * @return The current report period month.
     */
    MonthPeriod getPeriod();

    /**
     * Gets the name of the report.
     *
     * @return The name of the report.
     */
    String getReportName();
}
