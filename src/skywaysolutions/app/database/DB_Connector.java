package skywaysolutions.app.database;

import skywaysolutions.app.utils.CheckedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a MySQL {@link IDB_Connector} implementation.
 *
 * @author Alfred Manville
 */
public final class DB_Connector implements IDB_Connector {
    private Connection conn;
    private final ArrayList<String> tables = new ArrayList<>();
    private String dbName;

    /**
     * Connects to a MySQL database on the specified host port combinations and database name with the provided username and password.
     *
     * @param hostPortCombination The host:port combination (host:port, failoverHost:port...).
     * @param databaseName The name of the database.
     * @param username The username to authenticate as.
     * @param password The password to authenticate as.
     * @return The database connection.
     * @throws CheckedException A connection failure occurs.
     */
    @Override
    public Connection connect(String hostPortCombination, String databaseName, String username, String password) throws CheckedException {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://"+hostPortCombination+"/"+databaseName, username, password);
            dbName = databaseName;
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
        getTableList(true);
        return conn;
    }

    /**
     * Gets a prepared statement for the provided SQL template string.
     * DO NOT forget to use {@link PreparedStatement#close()} when finished.
     *
     * @param sqlTemplate The SQL template string.
     * @return The prepared statement for parameterization and execution.
     * @throws CheckedException The statement creation failed.
     */
    @Override
    public PreparedStatement getStatement(String sqlTemplate) throws CheckedException {
        try {
            return conn.prepareStatement(sqlTemplate);
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Closes the contained connection.
     *
     * @throws CheckedException The closing of a connection has failed.
     */
    @Override
    public void close() throws CheckedException {
        try {
            conn.close();
            tables.clear();
            dbName = null;
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Gets a list of table names.
     *
     * @param update Update the list with a query.
     * @return The list of table names.
     * @throws CheckedException The table names could not be obtained.
     */
    @Override
    public List<String> getTableList(boolean update) throws CheckedException {
        if (dbName == null) throw new CheckedException("Not Connected");
        if (update) {
            try {
                //Obtain a list of tables through the metadata of the connection object.
                try (ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), "", null, new String[]{"TABLE"})) {
                    tables.clear();
                    while (rs.next()) tables.add(rs.getString("TABLE_NAME"));
                }
            } catch (SQLException e) {
                throw new CheckedException(e);
            }
        }
        if (tables.size() == 0) throw new CheckedException("Tables not Obtained");
        return tables;
    }

    /**
     * Gets the database name.
     *
     * @return The database name.
     * @throws CheckedException The database name could not be obtained.
     */
    @Override
    public String getDatabaseName() throws CheckedException {
        if (dbName == null) throw new CheckedException("Not Connected");
        return dbName;
    }
}
