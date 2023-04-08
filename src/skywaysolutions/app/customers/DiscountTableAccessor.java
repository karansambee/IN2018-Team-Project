package skywaysolutions.app.customers;

import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscountTableAccessor extends DatabaseTableBase<Discount> {

    public DiscountTableAccessor(IDB_Connector conn) {
        super(conn);
    }

    /**
     * This should return the name of the table.
     *
     * @return The name of the table.
     */
    @Override
    protected String getTableName() {
        return "DiscountPlan";
    }

    @Override
    protected String getIDColumnName() {
        return "DiscountPlanID";
    }

    @Override
    protected Discount loadOneFrom(ResultSet rs, boolean locked) throws CheckedException {
        try {
            return new Discount(conn, rs, locked);
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected Discount noLoadOneFrom(ResultSet rs) throws CheckedException {
        try {
            return new Discount(conn, rs.getLong(getIDColumnName()));
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
        return "DiscountPlanID     bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                "  DiscountType       integer(1) NOT NULL, " +
                "  DiscountPercentage numeric(8, 6)";
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
        return "DiscountPlanID     bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT";
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
