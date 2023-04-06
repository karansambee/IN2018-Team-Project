package skywaysolutions.app.reports;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.sales.ISalesAccessor;
import skywaysolutions.app.staff.AccountController;
import skywaysolutions.app.staff.IStaffAccessor;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.stock.IStockAccessor;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.MonthPeriod;

import java.awt.*;
import java.util.Arrays;
import java.util.Date;

/**
 * Class for generating a Ticket Stock Turnover report
 *
 * @author Karan Sambee
 */
public class TicketStockTurnoverReport implements IReportGenerator {
    MonthPeriod period;
    TableCell[][][] periodReport;

    IStockAccessor stockAccessor;
    IStaffAccessor staffAccessor;
    ISalesAccessor salesAccessor;

    TicketStockTurnoverReport(IStockAccessor stockAccessor, IStaffAccessor staffAccessor, ISalesAccessor salesAccessor){
        this.stockAccessor = stockAccessor;
        this.staffAccessor = staffAccessor;
        this.salesAccessor = salesAccessor;
    }

    TicketStockTurnoverReport() {

    }

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

        periodReport = new TableCell[20][20][20];

        //Top left table
        periodReport[0][0][0] = new TableCell("AGENT: ");
        periodReport[0][1][0] = new TableCell("Number: ");
        periodReport[0][2][0] = new TableCell("Sales Office Place: ");
        periodReport[0][3][0] = new TableCell("Report period: ");

        periodReport[0][0][1] = new TableCell("AIR LINK");
        periodReport[0][1][1] = new TableCell("/");
        periodReport[0][2][1] = new TableCell("");
        periodReport[0][3][1] = new TableCell((this.period.getMonth()+1) + "/" + (this.period.getYear()+1900));


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

        //Central table (static)
        periodReport[2][0][0] = new TableCell("AGENT'S STOCK STATUS REPORT");

        periodReport[2][1][1] = new TableCell("RECEIVED BLANKS", 5);
        periodReport[2][1][2] = new TableCell("ASSIGNED/USED BLANKS", 5);
        periodReport[2][1][3] = new TableCell("FINAL AMOUNTS", 5);

        periodReport[2][2][1] = new TableCell("AGENT'S STOCK");
        periodReport[2][2][2] = new TableCell("SUB AGENTS'");
        periodReport[2][2][3] = new TableCell("(SUB AGENT'S)", 5);
        periodReport[2][2][4] = new TableCell("AGENT'S AMOUNT", 2);
        periodReport[2][2][5] = new TableCell("SUB AGENT'S AMOUNT", 3);

        periodReport[2][3][0] = new TableCell("NN");
        periodReport[2][3][1] = new TableCell("FROM/TO BLANKS NBRS");
        periodReport[2][3][2] = new TableCell("AMNT");
        periodReport[2][3][3] = new TableCell("CODE");
        periodReport[2][3][4] = new TableCell("FROM/TO BLANKS NBRS");
        periodReport[2][3][5] = new TableCell("AMT");
        periodReport[2][3][6] = new TableCell("CODE");
        periodReport[2][3][7] = new TableCell("ASSIGNED TO/FROM");
        periodReport[2][3][8] = new TableCell("AMNT");
        periodReport[2][3][9] = new TableCell("USED FROM/TO");
        periodReport[2][3][10] = new TableCell("AMNT");
        periodReport[2][3][11] = new TableCell("FROM/TO");
        periodReport[2][3][12] = new TableCell("AMNT");
        periodReport[2][3][13] = new TableCell("CODE");
        periodReport[2][3][14] = new TableCell("FROM/TO");
        periodReport[2][3][15] = new TableCell("AMNT");

        //Central table (dynamic)
        long[] blanksReceivedByAgents = stockAccessor.getBlanks(-1);
        long[] blanksReceivedByAgentsInPeriod = new long[blanksReceivedByAgents.length];
        for (int i=0; i<blanksReceivedByAgents.length; i++){

        }


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
        return generateTables();
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