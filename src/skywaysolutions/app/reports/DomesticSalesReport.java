package skywaysolutions.app.reports;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.MonthPeriod;

import java.awt.*;

public class DomesticSalesReport implements IReportGenerator{
    /**
     * Generates a report based on the current period.
     *
     * @return The generated report image for display.
     * @throws CheckedException A report generation error has occurred.
     */
    @Override
    public Image generate() throws CheckedException {
        return null;
    }

    /**
     * Generates a report based on the current period and the passed subject ID.
     *
     * @param subject The ID.
     * @return The generated report image for display.
     * @throws CheckedException A report generation error has occurred.
     */
    @Override
    public Image generateFor(long subject) throws CheckedException {
        return null;
    }

    /**
     * Generates a report based on the current period.
     *
     * @return The generated report tables.
     * @throws CheckedException A report generation error has occurred.
     */
    @Override
    public TableCell[][][] generateTables() throws CheckedException {
        return new TableCell[0][][];
    }

    /**
     * Generates a report based on the current period and the passed subject ID.
     *
     * @param subject The ID.
     * @return The generated report tables.
     * @throws CheckedException A report generation error has occurred.
     */
    @Override
    public TableCell[][][] generateTablesFor(long subject) throws CheckedException {
        return new TableCell[0][][];
    }

    /**
     * Sets the report period month.
     *
     * @param period The month of the report.
     * @throws CheckedException Setting the month period has failed.
     */
    @Override
    public void setPeriod(MonthPeriod period) throws CheckedException {

    }

    /**
     * Gets the current report period month.
     *
     * @return The current report period month.
     */
    @Override
    public MonthPeriod getPeriod() {
        return null;
    }

    /**
     * Gets the name of the report.
     *
     * @return The name of the report.
     */
    @Override
    public String getReportName() {
        return null;
    }
}
