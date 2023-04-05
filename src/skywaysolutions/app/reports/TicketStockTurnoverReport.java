package skywaysolutions.app.reports;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.MonthPeriod;

import java.awt.*;
import java.util.Date;

/**
 * Class for generating a Ticket Stock Turnover report
 *
 * @author Karan Sambee
 */
public class TicketStockTurnoverReport implements IReportGenerator {
    MonthPeriod period;
    TableCell[][][] periodReport;

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
     * @return The generated report table.
     * @throws CheckedException A report generation error has occurred.
     */
    @Override
    public TableCell[][][] generateTables() throws CheckedException {

        setPeriod(new MonthPeriod(new Date().getMonth(), new Date().getYear()));

        periodReport = new TableCell[0][][];

        //Top left table
        periodReport[0][0][0] = new TableCell("AGENT: ");
        periodReport[0][1][0] = new TableCell("Number: ");
        periodReport[0][2][0] = new TableCell("Sales Office Place: ");
        periodReport[0][3][0] = new TableCell("Report period: ");

        periodReport[0][0][1] = new TableCell("AIR LINK");
        periodReport[0][1][1] = new TableCell("/");
        periodReport[0][2][1] = new TableCell("");
        periodReport[0][3][1] = new TableCell(this.period.getMonth() + "/" + this.period.getYear());


        //Top right table
        periodReport[1][0][0] = new TableCell("Batch NBR");
        periodReport[1][1][0] = new TableCell("Port of SALE");
        periodReport[1][2][0] = new TableCell("Period");
        periodReport[1][3][0] = new TableCell("Operator's Code");
        periodReport[1][4][0] = new TableCell("Report NBR");

        periodReport[1][0][1] = new TableCell("Curr. of Sale");
        periodReport[1][1][1] = new TableCell("Curr. conv. RATE");
        periodReport[1][2][1] = new TableCell("S. AGENT'S Code");
        periodReport[1][3][1] = new TableCell("");
        periodReport[1][4][1] = new TableCell("Supervisor's code");

        //Central table
        periodReport[2][0][0] = new TableCell("AGENT'S STOCK STATUS REPORT");
        periodReport[2][1][0] = new TableCell("NN", 3);


        //Bottom table

        return periodReport;
    }

    /**
     * Generates a report based on the current period and the passed subject ID.
     *
     * @param subject The ID.
     * @return The generated report table.
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
        this.period = period;
    }

    /**
     * Gets the current report period month.
     *
     * @return The current report period month.
     */
    @Override
    public MonthPeriod getPeriod() {
       return this.period;
    }

    /**
     * Gets the name of the report.
     *
     * @return The name of the report.
     */
    @Override
    public String getReportName() {
        return "Ticket Stock Turnover Report";
    }
}