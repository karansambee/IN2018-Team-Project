package skywaysolutions.app.database;

import skywaysolutions.app.utils.CheckedException;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Provides an Interface for using a database connection.
 *
 * @author Alfred Manville
 */
public interface IDB_Connector {
    /**
     * Connects to a database on the specified path with the provided username and password.
     *
     * @param databasePath The path to the database containing the address and the path to the database.
     * @param username The username to authenticate as.
     * @param password The password to authenticate as.
     * @return The database connection.
     * @throws CheckedException A connection failure occurs.
     */
    Connection connect(String databasePath, String username, String password) throws CheckedException;

    /**
     * Executes the SQL on the contained connection.
     *
     * @param sql The SQL to execute.
     * @return The exit code of the SQL.
     * @throws CheckedException The execution failed.
     */
    int execute(String sql) throws CheckedException;

    /**
     * Execute and return a result set from an SQL execution on the contained connection.
     *
     * @param sql The SQL to execute.
     * @return The result set of the executed SQL.
     * @throws CheckedException
     */
    ResultSet executeAndReturn(String sql) throws CheckedException;

    /**
     * Backups the database contents.
     *
     * @return The database content as a binary array.
     * @throws CheckedException The retrieval of data has failed.
     */
    byte[] backup() throws CheckedException;

    /**
     * Restores the database from a backup.
     *
     * @param data The backup binary array.
     * @throws CheckedException The restoration of data has failed.
     */
    void restore(byte[] data) throws CheckedException;

    /**
     * Closes the contained connection.
     *
     * @return The exit code of the SQL.
     * @throws CheckedException The closing of a connection has failed.
     */
    int close() throws CheckedException;
}
