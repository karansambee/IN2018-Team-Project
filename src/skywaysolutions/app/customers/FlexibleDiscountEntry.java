package skywaysolutions.app.customers;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FlexibleDiscountEntry extends DatabaseEntityBase {
    private Long id;
    private long discountPlanID;
    private FlexiblePlanRange range;
    private Decimal percentage;

    public FlexibleDiscountEntry(IDB_Connector conn, Long id) {
        super(conn);
        this.id = id;
    }

    public FlexibleDiscountEntry(IDB_Connector conn, long discountPlanID,FlexiblePlanRange range, Decimal percentage) {
        super(conn);
        id = null;
        this.discountPlanID = discountPlanID;
        this.range = range;
        this.percentage = percentage;
    }

    public FlexibleDiscountEntry(IDB_Connector conn, ResultSet rs, boolean locked) throws SQLException {
        super(conn,locked);
        setLoadedAndExists();
        id = rs.getLong("EntryID");
        discountPlanID = rs.getLong("DiscountPlanID");
        range = new FlexiblePlanRange(new Decimal(rs.getDouble("AmountLowerBound"),2),
                new Decimal(rs.getDouble("AmountUpperBound"), 2));
        percentage = new Decimal(rs.getDouble("DiscountPercentage"), 6);
    }

    /**
     * This should return the name of the table.
     *
     * @return The name of the table.
     */
    @Override
    protected String getTableName() {
        return "FlexibleDiscountEntries";
    }

    /**
     * This should delete the auxiliary row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getAuxTableName()} to get the auxiliary table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @return If the row deletion occurred.
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected boolean deleteAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("DELETE FROM " + getAuxTableName() + " WHERE EntryID = ?")) {
            sta.setLong(1, id);
            if (sta.executeUpdate() > 0) return true;
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        return false;
    }


    /**
     * This should insert the auxiliary row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getAuxTableName()} to get the auxiliary table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void createAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            sta.setLong(1, id);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should insert the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void createRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO " + getTableName() + " VALUES (?,?,?,?,?)")) {
            if (id == null) sta.setNull(1, Types.BIGINT); else sta.setLong(1, id);
            sta.setLong(2, discountPlanID);
            sta.setDouble(3, range.getLower().getValue());
            sta.setDouble(4, range.getUpper().getValue());
            sta.setDouble(5, percentage.getValue());
            sta.executeUpdate();
            if (id == null) {
                try (PreparedStatement stac = conn.getStatement("SELECT MAX(EntryID) as maxID FROM "+getTableName())) {
                    try (ResultSet rs = stac.executeQuery()) {
                        if (!rs.next()) throw new CheckedException("No Insert Occurred!");
                        id = rs.getLong("maxID");
                    }
                }
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should update the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void updateRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("UPDATE " + getTableName() + " SET DiscountPlanID = ?, " +
                "AmountLowerBound = ?, AmountUpperBound = ?, DiscountPercentage = ? WHERE EntryID = ?")) {
            sta.setLong(1, discountPlanID);
            sta.setDouble(2, range.getLower().getValue());
            sta.setDouble(3, range.getUpper().getValue());
            sta.setDouble(4, percentage.getValue());
            sta.setLong(5, id);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should select the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred (EG: Row does not exist).
     */
    @Override
    protected void loadRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT DiscountPlanID," +
                "AmountLowerBound, AmountUpperBound, DiscountPercentage FROM " + getTableName() + " WHERE EntryID = ?")) {
            sta.setLong(1, id);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                id = rs.getLong("EntryID");
                discountPlanID = rs.getLong("DiscountPlanID");
                range = new FlexiblePlanRange(new Decimal(rs.getDouble("AmountLowerBound"), 2),
                        new Decimal(rs.getDouble("AmountUpperBound"), 2));
                percentage = new Decimal(rs.getDouble("Percentage"), 6);
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should delete the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void deleteRow() throws CheckedException {
        try(PreparedStatement pre = conn.getStatement("DELETE FROM " + getTableName() + " WHERE EntryID = ?")){
            pre.setLong(1, id);
            pre.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should check if the row corresponding to the current object exists.
     * ( COUNT(*) and WHERE is your friend)
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM " + getTableName() + " WHERE EntryID = ?")) {
            sta.setLong(1, id);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    public long getID() {
        return id;
    }

    public long getDiscountPlanID() {
        return discountPlanID;
    }

    public FlexiblePlanRange getRange() {
        return range;
    }

    public void setRange(FlexiblePlanRange range) {
        this.range = range;
    }

    public Decimal getPercentage() {
        return percentage;
    }

    public void setPercentage(Decimal percentage) {
        this.percentage = percentage;
    }
}
