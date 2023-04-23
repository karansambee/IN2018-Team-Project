package skywaysolutions.app.rates;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides a table accessor to access the ExchangeRate table.
 *
 * @author Alfred Manville
 */
public class ExchangeRateTableAccessor extends DatabaseTableBase<ConversionRate> {

    /**
     * Constructs a new ExchangeRateTableAccessor with the specified connection.
     *
     * @param conn The connection to use.
     */
    public ExchangeRateTableAccessor(IDB_Connector conn) {
        super(conn);
    }

    /**
     * This should return the name of the table.
     *
     * @return The name of the table.
     */
    @Override
    protected String getTableName() {
        return "ExchangeRate";
    }

    /**
     * This should return the name of the ID column.
     *
     * @return The name of the ID column.
     */
    @Override
    protected String getIDColumnName() {
        return "CurrencyName";
    }

    /**
     * This loads one instance of {@link ConversionRate} from the current result set.
     * DO NOT call {@link ResultSet#next()}.
     * <p>
     * This means that the class extending {@link DatabaseEntityBase} should have a
     * constructor that takes a {@link IDB_Connector} and {@link ResultSet}
     * to allow for a direct load to occur.
     * </p>
     *
     * @param rs     The result set to use for loading.
     * @param locked If the object is currently locked.
     * @return An instance of {@link ConversionRate}.
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected ConversionRate loadOneFrom(ResultSet rs, boolean locked) throws CheckedException {
        try {
            return new ConversionRate(conn, rs, locked);
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * This creates one instance of {@link ConversionRate} using the ID from the current result set without loading.
     * DO NOT call {@link ResultSet#next()}.
     * <p>
     * This means that the class extending {@link DatabaseEntityBase} should have a
     * constructor that takes a {@link IDB_Connector} and {@link ResultSet}
     * to allow for a direct load to occur.
     * </p>
     *
     * @param rs The result set to use for loading.
     * @return An instance of {@link ConversionRate}.
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected ConversionRate noLoadOneFrom(ResultSet rs) throws CheckedException {
        try {
            return new ConversionRate(conn, rs.getString(getIDColumnName()));
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
        return "CurrencyName      char(4) NOT NULL, " +
                "  USDConversionRate numeric(8, 6) NOT NULL, " +
                "  CurrencySymbol    char(2) NOT NULL, " +
                "  PRIMARY KEY (CurrencyName)";
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
        return "CurrencyName      char(4) NOT NULL";
    }

    /**
     * Gets the ID of the object in the result set position.
     *
     * @param rs The result set.
     * @return The ID.
     * @throws SQLException An SQL error has occurred.
     */
    @Override
    protected Object getObjectID(ResultSet rs) throws SQLException {
        return rs.getString(getIDColumnName());
    }

    /**
     * Loads one object via its ID.
     *
     * @param ID The ID of the object to load.
     * @return The object.
     * @throws CheckedException A load error has occurred.
     */
    @Override
    protected ConversionRate loadOne(Object ID) throws CheckedException {
        return internalLoad(new ConversionRate(conn, (String) ID));
    }

    /**
     * This should insert all the aux rows.
     * Select all the IDs from the main table to get what to insert.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name for selection.
     * Use {@link #getAuxTableName()} to get the table name for insertion.
     * DO NOT use any public functions provided by {@link DatabaseTableBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void createAllAuxRows() throws CheckedException {
        createAllAuxRowsStringID();
    }
}
