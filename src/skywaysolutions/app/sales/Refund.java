package skywaysolutions.app.sales;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.Time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * This class provides an entity representation of a refund.
 *
 * @author Alfred Manville
 */
public class Refund extends DatabaseEntityBase {
    private Long refundID;
    private long transactionID;
    private Date refundDate;
    private Decimal refundAmount;

    /**
     * Constructs a new instance of refund of the specified ID.
     *
     * @param conn The database connection.
     * @param refundID The ID of the refund.
     */
    Refund(IDB_Connector conn, long refundID) {
        super(conn);
        this.refundID = refundID;
    }

    /**
     * Constructs a new instance of refund with the specified transaction, date and amount.
     *
     * @param conn The database connection.
     * @param transactionID The ID of the transaction.
     * @param date The date of the refund.
     * @param amount The amount refunded.
     */
    Refund(IDB_Connector conn, long transactionID, Date date, Decimal amount) {
        super(conn);
        refundID = null;
        this.transactionID = transactionID;
        this.refundDate = date;
        this.refundAmount = amount;
    }

    /**
     * Constructs a new instance of refund loading from the current row of the result set.
     *
     * @param conn The database connection.
     * @param rs The result set to load from.
     * @throws SQLException An error has occurred.
     */
    Refund(IDB_Connector conn, ResultSet rs) throws SQLException {
        super(conn);
        refundID = rs.getLong("RefundID");
        transactionID = rs.getLong("TranscationID");
        refundDate = Time.fromSQLDate(rs.getDate("RefundDate"));
        refundAmount = new Decimal(rs.getDouble("LocalCurrency") ,2);
    }

    @Override
    protected String getTableName() {
        return "Refund";
    }

    @Override
    protected boolean deleteAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("DELETE FROM "+getAuxTableName()+" WHERE RefundID = ?")) {
            sta.setLong(1, refundID);
            return sta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void createAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO "+getAuxTableName()+" VALUES (?)")) {
            sta.setLong(1, refundID);
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void createRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO "+getTableName()+" VALUES (?,?,?,?)")) {
            if (refundID == null) sta.setNull(1, Types.BIGINT); else sta.setLong(1, refundID);
            sta.setLong(2, transactionID);
            sta.setDate(3, Time.toSQLDate(refundDate));
            sta.setDouble(4, refundAmount.getValue());
            sta.executeUpdate();
            if (refundID == null) {
                try (PreparedStatement stac = conn.getStatement("SELECT MAX(RefundID) as maxID FROM "+getTableName())) {
                    try (ResultSet rs = stac.executeQuery()) {
                        if (!rs.next()) throw new CheckedException("No Insert Occurred!");
                        refundID = rs.getLong("maxID");
                    }
                }
            }
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void updateRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("UPDATE "+getTableName()+" SET TranscationID = ?, RefundDate = ?, LocalCurrency = ? WHERE RefundID = ?")) {
            sta.setLong(1, transactionID);
            sta.setDate(2, Time.toSQLDate(refundDate));
            sta.setDouble(3, refundAmount.getValue());
            sta.setLong(4, refundID);
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void loadRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT RefundID, TranscationID, RefundDate, LocalCurrency FROM "+getTableName()+" WHERE RefundID = ?")) {
            sta.setLong(1, refundID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                refundID = rs.getLong("RefundID");
                transactionID = rs.getLong("TranscationID");
                refundDate = Time.fromSQLDate(rs.getDate("RefundDate"));
                refundAmount = new Decimal(rs.getDouble("LocalCurrency") ,2);
            }
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void deleteRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("DELETE FROM "+getTableName()+" WHERE RefundID = ?")) {
            sta.setLong(1, refundID);
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM "+getTableName()+" WHERE RefundID = ?")) {
            sta.setLong(1, refundID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Gets the ID of the refund.
     *
     * @return The ID.
     */
    public long getRefundID() {
        return refundID;
    }

    /**
     * Gets the transaction ID the refund is associated with.
     *
     * @return The transaction ID.
     */
    public long getTransactionID() {
        return transactionID;
    }

    /**
     * Sets the transaction ID the refund is associated with.
     *
     * @param transactionID The transaction ID.
     */
    public void setTransactionID(long transactionID) {
        this.transactionID = transactionID;
    }

    /**
     * Gets the date the refund occurred.
     *
     * @return The date.
     */
    public Date getRefundDate() {
        return refundDate;
    }

    /**
     * Sets the date the refund occurred on.
     *
     * @param refundDate The date.
     */
    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    /**
     * Gets the refund amount.
     *
     * @return The amount refunded.
     */
    public Decimal getRefundAmount() {
        return refundAmount;
    }

    /**
     * Sets the amount refunded.
     *
     * @param refundAmount The amount refunded.
     */
    public void setRefundAmount(Decimal refundAmount) {
        this.refundAmount = refundAmount;
    }
}
