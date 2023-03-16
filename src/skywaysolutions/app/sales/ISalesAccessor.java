package skywaysolutions.app.sales;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.MonthPeriod;

import java.util.Date;

/**
 * Provides the sales accessor interface for the package.
 *
 * @author Alfred Manville
 */
public interface ISalesAccessor {
    /**
     * Sells a blank to the specified customer for the specified type of sale, with a specific commission rate,
     * a due date, a sale date, a cost and taxes.
     *
     * @param blank The blank to be sold.
     * @param customer The customer ID to sell the blank to.
     * @param type The type of sale.
     * @param commissionRate The commission rate on the sale.
     * @param dueDate The due date of the sales payments.
     * @param saleDate The date the sale was created.
     * @param cost The cost of the blank.
     * @param tax The tax on the blank.
     * @param secondaryTax The secondary tax on the blank.
     * @return The sale ID.
     * @throws CheckedException An error occurred during the sale.
     */
    long sell(long blank, long customer, SaleType type, double commissionRate, Date dueDate, Date saleDate, Decimal cost, Decimal tax, Decimal secondaryTax) throws CheckedException;

    /**
     * Performs a transaction for the specified sale on a specified date with the type of currency being used and the payment being made.
     *
     * @param saleID The ID of the sale.
     * @param date The date of the transaction.
     * @param currency The currency of the transaction.
     * @param payment The payment of the transaction.
     * @return The transaction ID.
     * @throws CheckedException An error occurred during the transaction.
     */
    long transact(long saleID, Date date, String currency, Payment payment) throws CheckedException;

    /**
     * Checks if a sale has been fully paid.
     *
     * @param saleID The sale ID.
     * @return If the sale has been fully paid.
     * @throws CheckedException The sale could not be retrieved.
     */
    boolean fullyPaid(long saleID) throws CheckedException;

    /**
     * Checks if a sale is late (Is overdue and not fully paid).
     *
     * @param saleID The sale ID.
     * @param date The current date.
     * @return If the sale is overdue for payment.
     * @throws CheckedException The sale could not be retrieved.
     */
    boolean late(long saleID, Date date) throws CheckedException;

    /**
     * Refunds or gets the refunds of a sale.
     *
     * @param saleID The sale ID.
     * @return The IDs of the refunds.
     * @throws CheckedException The refund operation / obtaining the refund IDs has failed.
     */
    long[] refund(long saleID) throws CheckedException;

    /**
     * Gets the sales given the period and the type of payment.
     *
     * @param period The month of the sales (Null for any time).
     * @param type The type of payment.
     * @return An array of sale IDs.
     * @throws CheckedException Retrieving the sales has failed.
     */
    long[] getSales(MonthPeriod period, PaymentType type) throws CheckedException;

    /**
     * Gets the sales given the period, type of payment and the staff ID.
     *
     * @param period The month of the sales (Null for any time).
     * @param type The type of payment.
     * @param staffID The ID of the staff member.
     * @return An array of sale IDs.
     * @throws CheckedException Retrieving the sales has failed.
     */
    long[] getSalesByStaff(MonthPeriod period, PaymentType type, long staffID) throws CheckedException;

    /**
     * Gets the sales given the period, type of payment and the customer ID.
     * @param period The month of the sales (Null for any time).
     * @param type The type of payment.
     * @param customerID The ID of the customer.
     * @return An array of sale IDs.
     * @throws CheckedException Retrieving the sales has failed.
     */
    long[] getSalesByCustomers(MonthPeriod period, PaymentType type, long customerID) throws CheckedException;

    /**
     * Gets the sale given the ID.
     *
     * @param saleID The sale ID.
     * @return The sale corresponding to the ID.
     * @throws CheckedException Retrieving the sale has failed.
     */
    Sale getSale(long saleID) throws CheckedException;

    /**
     * Gets the refund given the ID.
     *
     * @param refundID The refund ID.
     * @return The refund corresponding to the ID.
     * @throws CheckedException Retrieving the refund has failed.
     */
    Refund getRefund(long refundID) throws CheckedException;
}
