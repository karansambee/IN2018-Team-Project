package skywaysolutions.app.database;

import skywaysolutions.app.utils.CheckedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Provides an Interface for using a database connection.
 *
 * @author Alfred Manville
 */
public interface IDB_Connector {
    /**
     * Connects to a database on the specified host port combinations and database name with the provided username and password.
     *
     * @param hostPortCombination The host:port combination (host:port, failoverHost:port...).
     * @param databaseName The name of the database.
     * @param username The username to authenticate as.
     * @param password The password to authenticate as.
     * @return The database connection.
     * @throws CheckedException A connection failure occurs.
     */
    Connection connect(String hostPortCombination, String databaseName, String username, String password) throws CheckedException;

    /**
     * Gets a prepared statement for the provided SQL template string.
     * DO NOT forget to use {@link PreparedStatement#close()} when finished.
     *
     * @param sqlTemplate The SQL template string.
     * @return The prepared statement for parameterization and execution.
     * @throws CheckedException The statement creation failed.
     */
    PreparedStatement getStatement(String sqlTemplate) throws CheckedException;

    /**
     * Closes the contained connection.
     *
     * @throws CheckedException The closing of a connection has failed.
     */
    void close() throws CheckedException;

    /**
     * Gets a list of table names.
     *
     * @param update Update the list with a query.
     * @return The list of table names.
     * @throws CheckedException The table names could not be obtained.
     */
    List<String> getTableList(boolean update) throws CheckedException;

    /**
     * Gets the database name.
     *
     * @return The database name.
     * @throws CheckedException The database name could not be obtained.
     */
    String getDatabaseName() throws CheckedException;
}
