package skywaysolutions.app.customers;

import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlexibleDiscountEntriesTableAccessor extends DatabaseTableBase<FlexibleDiscountEntry> {

    public FlexibleDiscountEntriesTableAccessor(IDB_Connector conn) {
        super(conn);
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

    @Override
    protected String getIDColumnName() {
        return "EntryID";
    }

    @Override
    protected FlexibleDiscountEntry loadOneFrom(ResultSet rs, boolean locked) throws CheckedException {
        try {
            return new FlexibleDiscountEntry(conn, rs, locked);
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected FlexibleDiscountEntry noLoadOneFrom(ResultSet rs) throws CheckedException {
        try {
            return new FlexibleDiscountEntry(conn, rs.getLong(getIDColumnName()));
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Gets the table schema (The bit that's located between the brackets).
     * For: "CREATE TABLE test (id int, test varchar(255))"
     * Becomes: "id int, test varchar(255)"
     *
     * @return The table schema portion between the brackets.
     */
    @Override
    protected String getTableSchema() {
        return "EntryID            bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                "  DiscountPlanID     bigint(19) NOT NULL, " +
                "  AmountLowerBound   numeric(12, 2) NOT NULL, " +
                "  AmountUpperBound   numeric(12, 2) NOT NULL, " +
                "  DiscountPercentage numeric(8, 6) NOT NULL, " +
                "  FOREIGN KEY (DiscountPlanID) REFERENCES DiscountPlan(DiscountPlanID)";
    }

    /**
     * Gets the table auxiliary schema (The bit that's located between the brackets).
     * This is only has the ID column corresponding to the main table.
     * For Main Table: "CREATE TABLE test (id int, test varchar(255))"
     * For Aux Table: "CREATE TABLE test (id int)"
     * Becomes: "id int"
     *
     * @return The aux table schema portion between the brackets.
     */
    @Override
    protected String getAuxTableSchema() {
        return "EntryID            bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT";
    }

    /**
     * This should insert all the aux rows.
     * Select all the IDs from the main table to get what to insert.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseTableBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void createAllAuxRows() throws CheckedException {
        createAllAuxRowsLongID();
    }
}
