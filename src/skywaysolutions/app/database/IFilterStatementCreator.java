package skywaysolutions.app.database;

import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This interface provides the ability for {@link DatabaseTableBase#loadMany(IFilterStatementCreator, MultiLoadSyncMode)} to have custom filters.
 *
 * @author Alfred Manville
 */
public interface IFilterStatementCreator {
    /**
     * Gets a prepared statement from the specified connection,
     * using the passed string as the beginning of the SQL template.
     * <p>
     * The statement will always begin with "SELECT * FROM [TABLE NAME] WHERE ",
     * EG: "SELECT * FROM test WHERE " where the table here is test.
     * </p>
     * @param conn The database connection.
     * @param startOfSQLTemplate The start of the SQL Template to use for the statement.
     * @return The prepared statement with the filters and their parameters applied.
     * @throws SQLException An SQL error occurred.
     * @throws CheckedException An error occurred.
     */
    PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException;
}
