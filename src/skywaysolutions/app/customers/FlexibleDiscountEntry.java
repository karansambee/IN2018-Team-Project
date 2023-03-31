package skywaysolutions.app.customers;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlexibleDiscountEntry extends DatabaseEntityBase {

    private long id;
    private long discountPlanID;
    private FlexiblePlanRange range;
    private Decimal percentage;

    /**
     * Constructs a new DatabaseEntityBase with the specified connection.
     *
     * @param conn The connection to use.
     */
    public FlexibleDiscountEntry(IDB_Connector conn, long id) {
        super(conn);
        this.id = id;
    }

    public FlexibleDiscountEntry(IDB_Connector conn, FlexiblePlanRange range, Decimal percentage) {
        super(conn);
        this.range = range;
        this.percentage = percentage;
    }

    public FlexibleDiscountEntry(IDB_Connector conn, ResultSet rs) throws SQLException {
        super(conn);
        id = rs.getLong("EntryID");
        discountPlanID = rs.getLong("DiscountPlanID");
        range = new FlexiblePlanRange(new Decimal(rs.getDouble("AmountLowerBound"),10),
                new Decimal(rs.getDouble("AmountUpperBound"), 10));
        percentage = new Decimal(rs.getDouble("DiscountPercentage"), 2);
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
            sta.setLong(1, id);
            sta.setLong(2, discountPlanID);
            sta.setDouble(3, range.getLower().getValue());
            sta.setDouble(4, range.getUpper().getValue());
            sta.setDouble(5, percentage.getValue());
            sta.executeUpdate();
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
                "AmountLowerBound = ?, AmountUpperBound = ?, DiscountPercentage = ?  WHERE EntryID = ?")) {
            sta.setLong(1, discountPlanID);
            sta.setLong(2, discountPlanID);
            sta.setDouble(3, range.getLower().getValue());
            sta.setDouble(4, range.getUpper().getValue());
            sta.setDouble(5, percentage.getValue());
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
                "AmountLowerBound, AmountUpperBound, DiscountPercentage FROM" + getTableName() + "WHERE EntryID = ?")) {
            sta.setLong(1, id);
            ResultSet rs = sta.executeQuery();
            rs.next();

            id = rs.getLong("EntryID");
            discountPlanID = rs.getLong("DiscountPlanID");
            range = new FlexiblePlanRange(new Decimal(rs.getDouble("AmountLowerBound"), 10),
                    new Decimal(rs.getDouble("AmountUpperBound"), 10));
            percentage = new Decimal(rs.getDouble("Percentage"), 2);

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
        try(PreparedStatement pre = conn.getStatement("DELETE FROM" + getTableName() + "WHERE EntryID = ?")){
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
            ResultSet rs = sta.executeQuery();
            rs.next();
            int rc = rs.getInt("rowCount");
            rs.close();
            if (rc > 0) return true;
            else return false;
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    public long getId() {
        return id;
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
