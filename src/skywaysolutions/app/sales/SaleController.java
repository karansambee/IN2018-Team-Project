package skywaysolutions.app.sales;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.database.IFilterStatementCreator;
import skywaysolutions.app.database.MultiLoadSyncMode;
import skywaysolutions.app.rates.IRateAccessor;
import skywaysolutions.app.stock.IStockAccessor;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.MonthPeriod;
import skywaysolutions.app.utils.Time;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides an implementation of a sales accessor.
 *
 * @author Alfred Manville
 */
public class SaleController implements ISalesAccessor {
    private final IDB_Connector conn;
    private final SaleTableAccessor saleTableAccessor;
    private final TransactionTableAccessor transactionTableAccessor;
    private final RefundTableAccessor refundTableAccessor;
    private final IRateAccessor rateAccessor;
    private final IStockAccessor stockAccessor;
    private final IDFilterer idFilterer = new IDFilterer();
    private final SaleFilter saleFilter = new SaleFilter();
    private final Object slock = new Object();

    /**
     * Constructs a new instance of sale controller with the specified connection, stock accessor and rate accessor.
     *
     * @param conn The database connection to use.
     * @param rateAccessor The rate accessor to use.
     * @param stockAccessor The stock accessor to use.
     * @throws CheckedException Assuring the table schema has errored.
     */
    public SaleController(IDB_Connector conn, IRateAccessor rateAccessor, IStockAccessor stockAccessor) throws CheckedException {
        this.conn = conn;
        saleTableAccessor = new SaleTableAccessor(conn);
        saleTableAccessor.assureTableSchema();
        transactionTableAccessor = new TransactionTableAccessor(conn);
        transactionTableAccessor.assureTableSchema();
        refundTableAccessor = new RefundTableAccessor(conn);
        refundTableAccessor.assureTableSchema();
        this.rateAccessor = rateAccessor;
        this.stockAccessor = stockAccessor;
    }

    /**
     * Sells a blank to the specified customer for the specified type of sale, with a specific commission rate,
     * a due date, a sale date, a cost, taxes, currency and a cost pre discount.
     *
     * @param blank           The blank to be sold.
     * @param customer        The customer ID to sell the blank to.
     * @param type            The type of sale.
     * @param commissionRate  The commission rate on the sale.
     * @param dueDate         The due date of the sales payments.
     * @param saleDate        The date the sale was created.
     * @param cost            The cost of the blank.
     * @param tax             The tax on the blank.
     * @param secondaryTax    The secondary tax on the blank.
     * @param currency        The currency of the sale.
     * @param costPreDiscount The cost pre discount, can be null.
     * @return The sale ID.
     * @throws CheckedException An error occurred during the sale.
     */
    @Override
    public long sell(long blank, long customer, SaleType type, Decimal commissionRate, Date dueDate, Date saleDate, Decimal cost, Decimal tax, Decimal secondaryTax, String currency, Decimal costPreDiscount) throws CheckedException {
        synchronized (slock) {
            if (stockAccessor.isBlankBlacklisted(blank)) throw new CheckedException("Blank blacklisted");
            if (stockAccessor.isBlankReturned(blank)) throw new CheckedException("Blank returned");
            if (stockAccessor.isBlankVoided(blank)) throw new CheckedException("Blank voided");
            Sale sale = new Sale(conn, blank, customer, type, commissionRate, dueDate, saleDate, cost, (currency.equals("USD")) ? null : cost.mul(rateAccessor.getConversionRate(currency)),
                    tax, secondaryTax, currency, costPreDiscount);
            if (sale.exists(true)) throw new CheckedException("Sale already exists"); else {
                sale.store();
                return blank;
            }
        }
    }

    /**
     * Performs a transaction for the specified sale on a specified date with the type of currency being used and the payment being made.
     *
     * @param saleID   The ID of the sale.
     * @param date     The date of the transaction.
     * @param currency The currency of the transaction.
     * @param payment  The payment of the transaction.
     * @return The transaction ID.
     * @throws CheckedException An error occurred during the transaction.
     */
    @Override
    public long transact(long saleID, Date date, String currency, Payment payment) throws CheckedException {
        synchronized (slock) {
            if (!new Sale(conn, saleID).exists(true)) throw new CheckedException("Blank not sold");
            if (stockAccessor.isBlankBlacklisted(saleID)) throw new CheckedException("Blank is blacklisted");
            Transaction transaction = new Transaction(conn, saleID, currency, payment, (currency.equals("USD")) ? null : payment.getAmount().mul(rateAccessor.getConversionRate(currency)), date);
            transaction.store();
            return transaction.getTransactionID();
        }
    }

    private Transaction[] getTransactions(long saleID, MultiLoadSyncMode mode) throws CheckedException {
        idFilterer.columnName = "BlankNumber";
        idFilterer.id = saleID;
        return transactionTableAccessor.loadMany(idFilterer, mode).toArray(new Transaction[0]);
    }

    private Refund getRefundInt(long transactionID, MultiLoadSyncMode mode) throws CheckedException {
        idFilterer.columnName = "TranscationID";
        idFilterer.id = transactionID;
        Refund[] refunds = refundTableAccessor.loadMany(idFilterer, mode).toArray(new Refund[0]);
        return (refunds.length > 0) ? refunds[0] : null;
    }

    private boolean fullyPaidInt(long saleID, Date date) throws CheckedException {
        Sale sale = new Sale(conn, saleID);
        sale.lock();
        sale.load();
        sale.unlock();
        Transaction[] transactions = getTransactions(saleID, MultiLoadSyncMode.UnlockAfterLoad);
        Decimal paid = new Decimal();
        for (Transaction c : transactions) {
            paid = paid.add(c.getPayment().getAmount());
            Refund rf = getRefundInt(c.getTransactionID(), MultiLoadSyncMode.UnlockAfterLoad);
            if (rf != null) paid = paid.sub(rf.getRefundAmount());
        }
        if (date == null) {
            //Not passing date checks if fully paid
            return paid.greaterThanOrEqualTo(sale.getCost());
        } else {
            //Passing date means true if late and false if can still pay in time
            return paid.lessThan(sale.getCost()) && date.after(sale.getDueDate());
        }
    }

    /**
     * Checks if a sale has been fully paid.
     *
     * @param saleID The sale ID.
     * @return If the sale has been fully paid.
     * @throws CheckedException The sale could not be retrieved.
     */
    @Override
    public boolean fullyPaid(long saleID) throws CheckedException {
        synchronized (slock) {
            return fullyPaidInt(saleID, null);
        }
    }

    /**
     * Checks if a sale is late (Is overdue and not fully paid).
     *
     * @param saleID The sale ID.
     * @param date   The current date.
     * @return If the sale is overdue for payment.
     * @throws CheckedException The sale could not be retrieved.
     */
    @Override
    public boolean late(long saleID, Date date) throws CheckedException {
        synchronized (slock) {
            return fullyPaidInt(saleID, date);
        }
    }

    /**
     * Refunds or gets the refunds of a sale.
     *
     * @param saleID The sale ID.
     * @param date The date of any new refunds.
     * @return The IDs of the refunds.
     * @throws CheckedException The refund operation / obtaining the refund IDs has failed.
     */
    @Override
    public long[] refund(long saleID, Date date) throws CheckedException {
        synchronized (slock) {
            Transaction[] transactions = getTransactions(saleID, MultiLoadSyncMode.NoLoad);
            long[] refundIDs = new long[transactions.length];
            int refundCount = 0;
            for (int i = 0; i < transactions.length; i++) {
                Refund c = getRefundInt(transactions[i].getTransactionID(), MultiLoadSyncMode.NoLoad);
                if (c == null) {
                    if (date != null) { //If a date is provided, process the transactions without refunds and refund them
                        c = new Refund(conn, transactions[i].getTransactionID(), date, transactions[i].getPayment().getAmount());
                        c.store();
                        refundCount++;
                    }
                } else refundCount++;
                refundIDs[i] = (c == null) ? Long.MIN_VALUE : c.getRefundID(); //Use the MIN_VALUE of long to signify no refund
            }
            if (refundCount == refundIDs.length) {
                return refundIDs;
            } else {
                long[] tRefundIDs = new long[refundCount];
                int i = 0;
                for (long c : refundIDs) if (c != Long.MIN_VALUE) tRefundIDs[i++] = c;
                return tRefundIDs;
            }
        }
    }

    /**
     * Gets the sales given the period, type of payment and the currency.
     *
     * @param period   The month of the sales (Null for any time).
     * @param type     The type of payment.
     * @param currency The currency of the sale, if null, this filter is ignored.
     * @return An array of sale IDs.
     * @throws CheckedException Retrieving the sales has failed.
     */
    @Override
    public long[] getSales(MonthPeriod period, PaymentType type, String currency) throws CheckedException {
        synchronized (slock) {
            saleFilter.period = period;
            saleFilter.type = type;
            saleFilter.currency = currency;
            saleFilter.idColumnName = null;
            List<Sale> sales = saleTableAccessor.loadMany(saleFilter, MultiLoadSyncMode.NoLoad);
            long[] ids = new long[sales.size()];
            for (int i = 0; i < ids.length; i++) ids[i] = sales.get(i).getBlankNumber();
            return ids;
        }
    }

    /**
     * Gets the sales given the period, type of payment, the currency and the staff ID.
     *
     * @param period   The month of the sales (Null for any time).
     * @param type     The type of payment.
     * @param currency The currency of the sale, if null, this filter is ignored.
     * @param staffID  The ID of the staff member.
     * @return An array of sale IDs.
     * @throws CheckedException Retrieving the sales has failed.
     */
    @Override
    public long[] getSalesByStaff(MonthPeriod period, PaymentType type, String currency, long staffID) throws CheckedException {
        synchronized (slock) {
            saleFilter.period = period;
            saleFilter.type = type;
            saleFilter.currency = currency;
            saleFilter.idColumnName = null;
            List<Sale> lSales = saleTableAccessor.loadMany(saleFilter, MultiLoadSyncMode.NoLoad);
            List<Long> blanks = Arrays.stream(stockAccessor.getBlanks(staffID)).boxed().collect(Collectors.toList());
            List<Sale> sales = new ArrayList<>();
            for (Sale c : lSales) if (blanks.contains(c.getBlankNumber())) sales.add(c);
            long[] ids = new long[sales.size()];
            for (int i = 0; i < ids.length; i++) ids[i] = sales.get(i).getBlankNumber();
            return ids;
        }
    }

    /**
     * Gets the sales given the period, type of payment, the currency and the customer ID.
     *
     * @param period     The month of the sales (Null for any time).
     * @param type       The type of payment.
     * @param currency   The currency of the sale, if null, this filter is ignored.
     * @param customerID The ID of the customer.
     * @return An array of sale IDs.
     * @throws CheckedException Retrieving the sales has failed.
     */
    @Override
    public long[] getSalesByCustomer(MonthPeriod period, PaymentType type, String currency, long customerID) throws CheckedException {
        synchronized (slock) {
            saleFilter.period = period;
            saleFilter.type = type;
            saleFilter.currency = currency;
            saleFilter.idColumnName = "CustomerID";
            saleFilter.id = customerID;
            List<Sale> sales = saleTableAccessor.loadMany(saleFilter, MultiLoadSyncMode.NoLoad);
            long[] ids = new long[sales.size()];
            for (int i = 0; i < ids.length; i++) ids[i] = sales.get(i).getBlankNumber();
            return ids;
        }
    }

    /**
     * Gets the sale given the ID.
     *
     * @param saleID The sale ID.
     * @return The sale corresponding to the ID.
     * @throws CheckedException Retrieving the sale has failed.
     */
    @Override
    public Sale getSale(long saleID) throws CheckedException {
        synchronized (slock) {
            Sale sale = new Sale(conn, saleID);
            sale.lock();
            sale.load();
            sale.unlock();
            return sale;
        }
    }

    /**
     * Gets the refund given the ID.
     *
     * @param refundID The refund ID.
     * @return The refund corresponding to the ID.
     * @throws CheckedException Retrieving the refund has failed.
     */
    @Override
    public Refund getRefund(long refundID) throws CheckedException {
        synchronized (slock) {
            Refund refund = new Refund(conn, refundID);
            refund.lock();
            refund.load();
            refund.unlock();
            return refund;
        }
    }

    /**
     * Gets if the specified blank has been sold.
     *
     * @param blankID The blank ID.
     * @return If the blank has been sold.
     * @throws CheckedException Retrieving the sales has failed.
     */
    @Override
    public boolean blankSold(long blankID) throws CheckedException {
        synchronized (slock) {
            Sale sale = new Sale(conn, blankID);
            return sale.exists(true);
        }
    }

    /**
     * Gets an array of tables that can be backed up.
     *
     * @return The array of tables.
     */
    @Override
    public String[] getTables() {
        return new String[] {"Sale", "Transcation", "Refund"};
    }

    /**
     * Forces a table to be fully unlocked.
     *
     * @param tableName The table to fully unlock.
     * @throws CheckedException The table could not be unlocked.
     */
    @Override
    public void forceFullUnlock(String tableName) throws CheckedException {
        switch (tableName) {
            case "Sale" -> saleTableAccessor.unlockAll(true);
            case "Transcation" -> transactionTableAccessor.unlockAll(true);
            case "Refund" -> refundTableAccessor.unlockAll(true);
        }
    }

    /**
     * This class provides a filter statement for an ID of a specified column.
     *
     * @author Alfred Manville
     */
    private static class IDFilterer implements IFilterStatementCreator {
        public String columnName;
        public long id;

        /**
         * Gets a prepared statement from the specified connection,
         * using the passed string as the beginning of the SQL template.
         * <p>
         * The statement will always begin with "SELECT * FROM [TABLE NAME] WHERE ",
         * EG: "SELECT * FROM test WHERE " where the table here is test.
         * </p>
         *
         * @param conn               The database connection.
         * @param startOfSQLTemplate The start of the SQL Template to use for the statement.
         * @return The prepared statement with the filters and their parameters applied.
         * @throws SQLException An SQL error occurred.
         * @throws CheckedException An error occurred.
         */
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException {
            PreparedStatement sta = conn.getStatement(startOfSQLTemplate + columnName + " = ?");
            sta.setLong(1, id);
            return sta;
        }
    }

    /**
     * This class provides a filter statement for a sale.
     *
     * @author Alfred Manville
     */
    private static class SaleFilter implements IFilterStatementCreator {
        public String currency;
        public String idColumnName;
        public long id;
        public PaymentType type;
        public MonthPeriod period;

        /**
         * Gets a prepared statement from the specified connection,
         * using the passed string as the beginning of the SQL template.
         * <p>
         * The statement will always begin with "SELECT * FROM [TABLE NAME] WHERE ",
         * EG: "SELECT * FROM test WHERE " where the table here is test.
         * </p>
         *
         * @param conn               The database connection.
         * @param startOfSQLTemplate The start of the SQL Template to use for the statement.
         * @return The prepared statement with the filters and their parameters applied.
         * @throws SQLException     An SQL error occurred.
         * @throws CheckedException An error occurred.
         */
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException {
            PreparedStatement sta = conn.getStatement(startOfSQLTemplate + "CurrencyName = ?" + ((idColumnName == null) ? "" : " AND "+idColumnName+" = ?") +
                    ((type == PaymentType.Any) ? "" : " AND SaleType = ?") + ((period == null) ? "" : " AND SaleDate >= ? AND SaleDate < ?"));
            sta.setString(1, currency);
            int index = 2;
            if (idColumnName != null) sta.setLong(index++, id);
            if (type != PaymentType.Any) sta.setInt(index++, type.getValue());
            if (period != null) {
                sta.setDate(index++, Time.toSQLDate(period.getThisMonth()));
                sta.setDate(index, Time.toSQLDate(period.getNextMonth()));
            }
            return sta;
        }
    }
}
