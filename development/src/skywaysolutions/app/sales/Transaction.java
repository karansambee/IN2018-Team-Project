package skywaysolutions.app.sales;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.ResultSetNullableReturners;
import skywaysolutions.app.utils.Time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * This class provides an entity representation of a transcation.
 *
 * @author Alfred Manville
 */
public class Transaction extends DatabaseEntityBase {
    private Long transactionID;
    private long saleID;
    private String currency;
    private Payment payment;
    private Decimal amountPaidInUSD;
    private Date transactionDate;

    /**
     * Constructs a new instance of transaction of the specified ID.
     *
     * @param conn The database connection.
     * @param transactionID The ID of the transaction.
     */
    Transaction(IDB_Connector conn, long transactionID) {
        super(conn);
        this.transactionID = transactionID;
    }

    /**
     * Constructs a new instance of transaction with the specified sale ID, currency, payment, amount in USD and transaction date.
     *
     * @param conn The database connection.
     * @param saleID The ID of the sale.
     * @param currency The currency of the payment.
     * @param payment The payment.
     * @param amountUSD The payment in USD.
     * @param transactionDate The date of transaction.
     */
    Transaction(IDB_Connector conn, long saleID, String currency, Payment payment, Decimal amountUSD, Date transactionDate) {
        super(conn);
        this.saleID = saleID;
        this.currency = currency;
        this.payment = payment;
        this.amountPaidInUSD = amountUSD;
        this.transactionDate = transactionDate;
    }

    /**
     * Constructs a new instance of transaction loading from the current row of the result set.
     *
     * @param conn The database connection.
     * @param rs The result set to load from.
     * @param locked If the object is already locked.
     * @throws SQLException An error has occurred.
     */
    Transaction(IDB_Connector conn, ResultSet rs, boolean locked) throws SQLException, CheckedException {
        super(conn, locked);
        loadFrom(rs, locked);
    }

    /**
     * Gets the ID of the object that is used for caching.
     *
     * @return The ID of the object.
     */
    @Override
    public Object getPrimaryID() {
        return transactionID;
    }

    @Override
    protected String getTableName() {
        return "Transcation";
    }

    @Override
    protected boolean deleteAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("DELETE FROM "+getAuxTableName()+" WHERE TranscationID = ?")) {
            sta.setLong(1, transactionID);
            return sta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void createAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO "+getAuxTableName()+" VALUES (?)")) {
            sta.setLong(1, transactionID);
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void createRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO "+getTableName()+" VALUES (?,?,?,?,?,?,?,?,?)")) {
            if (transactionID == null) sta.setNull(1, Types.BIGINT); else sta.setLong(1, transactionID);
            sta.setLong(2, saleID);
            sta.setString(3, currency);
            sta.setDouble(4, payment.getAmount().getValue());
            if(amountPaidInUSD == null) sta.setNull(5, Types.NUMERIC); else sta.setDouble(5, amountPaidInUSD.getValue());
            sta.setDate(6, Time.toSQLDate(transactionDate));
            sta.setInt(7, payment.getType().getValue());
            if (payment.getType() == PaymentType.Card) sta.setLong(8 ,payment.getAuxiliaryNumber()); else sta.setNull(8, Types.BIGINT);
            if (payment.getType() == PaymentType.Cheque) sta.setLong(9 ,payment.getAuxiliaryNumber()); else sta.setNull(9, Types.BIGINT);
            sta.executeUpdate();
            if (transactionID == null) {
                try (PreparedStatement stac = conn.getStatement("SELECT MAX(TranscationID) as maxID FROM "+getTableName())) {
                    try (ResultSet rs = stac.executeQuery()) {
                        if (!rs.next()) throw new CheckedException("No Insert Occurred!");
                        transactionID = rs.getLong("maxID");
                    }
                }
            }
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void updateRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("UPDATE "+getTableName()+" SET BlankNumber = ?, CurrencyName = ?, AmountPaid = ?, AmountPaidInUSD = ?, TransactionDate = ?, PaymentType = ?, CardNumber = ?, ChequeNumber = ? WHERE TranscationID = ?")) {
            sta.setLong(1, saleID);
            sta.setString(2, currency);
            sta.setDouble(3, payment.getAmount().getValue());
            if(amountPaidInUSD == null) sta.setNull(4, Types.NUMERIC); else sta.setDouble(4, amountPaidInUSD.getValue());
            sta.setDate(5, Time.toSQLDate(transactionDate));
            sta.setInt(6, payment.getType().getValue());
            if (payment.getType() == PaymentType.Card) sta.setLong(7 ,payment.getAuxiliaryNumber()); else sta.setNull(7, Types.BIGINT);
            if (payment.getType() == PaymentType.Cheque) sta.setLong(8 ,payment.getAuxiliaryNumber()); else sta.setNull(8, Types.BIGINT);
            sta.setLong(9, transactionID);
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void loadRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT TranscationID, BlankNumber, CurrencyName, AmountPaid, AmountPaidInUSD, TransactionDate, PaymentType, CardNumber, ChequeNumber FROM "+getTableName()+" WHERE TranscationID = ?")) {
            sta.setLong(1, transactionID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                transactionID = rs.getLong("TranscationID");
                saleID = rs.getLong("BlankNumber");
                currency = rs.getString("CurrencyName");
                PaymentType payType = PaymentType.getPaymentTypeFromValue(rs.getInt("PaymentType"));
                payment = switch (payType) {
                    case Card -> Payment.getCardPayment(new Decimal(rs.getDouble("AmountPaid"), 2), rs.getLong("CardNumber"));
                    case Invoice -> Payment.getInvoicePayment(new Decimal(rs.getDouble("AmountPaid"), 2));
                    case Cheque -> Payment.getChequePayment(new Decimal(rs.getDouble("AmountPaid"), 2), rs.getLong("ChequeNumber"));
                    default -> Payment.getCashPayment(new Decimal(rs.getDouble("AmountPaid"), 2));
                };
                Double apiusd = ResultSetNullableReturners.getDoubleValue(rs, "AmountPaidInUSD");
                amountPaidInUSD = (apiusd == null) ? null : new Decimal(apiusd, 2);
                transactionDate = Time.fromSQLDate(rs.getDate("TransactionDate"));
            }
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * This should load the current object from the passed result set.
     *
     * @param rs     The result set to load from.
     * @param locked If the object is considered locked.
     * @throws SQLException     An SQL error has occurred.
     * @throws CheckedException An error has occurred.
     */
    @Override
    public void loadFrom(ResultSet rs, boolean locked) throws SQLException, CheckedException {
        setLoadedAndExists();
        setLockedState(locked);
        transactionID = rs.getLong("TranscationID");
        saleID = rs.getLong("BlankNumber");
        currency = rs.getString("CurrencyName");
        PaymentType payType = PaymentType.getPaymentTypeFromValue(rs.getInt("PaymentType"));
        payment = switch (payType) {
            case Card -> Payment.getCardPayment(new Decimal(rs.getDouble("AmountPaid"), 2), rs.getLong("CardNumber"));
            case Invoice -> Payment.getInvoicePayment(new Decimal(rs.getDouble("AmountPaid"), 2));
            case Cheque -> Payment.getChequePayment(new Decimal(rs.getDouble("AmountPaid"), 2), rs.getLong("ChequeNumber"));
            default -> Payment.getCashPayment(new Decimal(rs.getDouble("AmountPaid"), 2));
        };
        Double apiusd = ResultSetNullableReturners.getDoubleValue(rs, "AmountPaidInUSD");
        amountPaidInUSD = (apiusd == null) ? null : new Decimal(apiusd, 2);
        transactionDate = Time.fromSQLDate(rs.getDate("TransactionDate"));
    }

    @Override
    protected void deleteRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("DELETE FROM "+getTableName()+" WHERE TranscationID = ?")) {
            sta.setLong(1, transactionID);
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM "+getTableName()+" WHERE TranscationID = ?")) {
            sta.setLong(1, transactionID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Gets the ID of the transaction.
     *
     * @return The ID.
     */
    public Long getTransactionID() {
        return transactionID;
    }

    /**
     * Gets the sale ID the transaction is associated with.
     *
     * @return The sale ID.
     */
    public long getSaleID() {
        return saleID;
    }

    /**
     * Sets the sale ID the transaction is associated with.
     *
     * @param saleID The sale ID.
     */
    public void setSaleID(long saleID) {
        this.saleID = saleID;
    }

    /**
     * Gets the currency the transaction was paid in.
     *
     * @return The currency.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency the transaction was paid in.
     *
     * @param currency The currency.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the payment object used.
     *
     * @return The payment object.
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Sets the payment object used.
     *
     * @param payment The payment object.
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    /**
     * Gets the amount paid in USD.
     *
     * @return The amount paid in USD (Could be null).
     */
    public Decimal getAmountPaidInUSD() {
        return amountPaidInUSD;
    }

    /**
     * Sets the amount paid in USD (Can be null).
     *
     * @param amountPaidInUSD The amount paid in USD (Can be null).
     */
    public void setAmountPaidInUSD(Decimal amountPaidInUSD) {
        this.amountPaidInUSD = amountPaidInUSD;
    }

    /**
     * Gets the date of transaction.
     *
     * @return The date.
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the date of transaction.
     *
     * @param transactionDate The date.
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
