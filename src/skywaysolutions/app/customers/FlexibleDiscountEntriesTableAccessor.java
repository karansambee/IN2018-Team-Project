package skywaysolutions.app.customers;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FlexibleDiscountEntriesTableAccessor extends DatabaseTableBase<FlexibleDiscountEntry> {
    /**
     * Constructs a new DatabaseTableBase with the specified connection.
     *
     * @param conn The connection to use.
     */
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

    /**
     * This loads one instance of {@link FlexibleDiscountEntry} from the current result set.
     * DO NOT call {@link ResultSet#next()}.
     * <p>
     * This means that the class extending {@link DatabaseEntityBase} should have a
     * constructor that takes a {@link IDB_Connector} and {@link ResultSet}
     * to allow for a direct load to occur.
     * </p>
     *
     * @param rs The result set to use for loading.
     * @return An instance of {@link FlexibleDiscountEntry}.
     */
    @Override
    protected FlexibleDiscountEntry loadOneFrom(ResultSet rs) {
        return null;
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
        return "  EntryID            bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "  DiscountPlanID     bigint(19) NOT NULL," +
                "  AmountLowerBound   numeric(12, 2) NOT NULL," +
                "  AmountUpperBound   numeric(12, 2) NOT NULL, " +
                "  DiscountPercentage numeric(8, 6) NOT NULL)";
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
        return "  EntryID            bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT";
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
        ArrayList<Long> entryIDs = new ArrayList<>();
        try (PreparedStatement sta = conn.getStatement("SELECT EntryID FROM " + getTableName())) {
            ResultSet rs = sta.executeQuery();
            while (rs.next()) entryIDs.add(rs.getLong("EntryID"));
            rs.close();
        } catch (
                SQLException throwables) {
            throw new CheckedException(throwables);
        }
        try (PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            for (long c : entryIDs) {
                sta.setLong(1, c);
                sta.addBatch();
            }
            sta.executeBatch();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }
}
