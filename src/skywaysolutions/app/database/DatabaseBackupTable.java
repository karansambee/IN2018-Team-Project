package skywaysolutions.app.database;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a backup of a database table.
 * This also provides a static way of streaming the backup
 * to/from a file to/from the database.
 *
 * @author Alfred Manville
 */
public final class DatabaseBackupTable {
    private final String tableName;
    private final List<Integer> columnTypes = new ArrayList<>();
    private final List<String> columnNames = new ArrayList<>();
    private final List<Object[]> rows = new ArrayList<>();

    /**
     * Constructs a new DatabaseBackupTable for the specified table loading from the provided database connection.
     *
     * @param tableName The table to back up.
     * @param conn The database connection.
     * @throws CheckedException A backup error occurs.
     */
    public DatabaseBackupTable(String tableName, IDB_Connector conn) throws CheckedException {
        this.tableName = tableName;
        //Check if table exists
        if (!conn.getTableList(true).contains(tableName)) throw new CheckedException("Table does not exist");
        //Load the result set
        try(PreparedStatement sta = conn.getStatement("SELECT * FROM "+tableName)) {
            try (ResultSet rs = sta.executeQuery()) {
                //Load column information from the result set
                ResultSetMetaData rsm = rs.getMetaData();
                for (int i = 1; i <= rsm.getColumnCount(); ++i) {
                    columnNames.add(rsm.getColumnName(i));
                    columnTypes.add(rsm.getColumnType(i));
                }
                //Load row information from the result set
                while (rs.next()) {
                    Object[] row = new Object[columnTypes.size()];
                    for (int i = 0; i < columnTypes.size(); ++i) {
                        row[i] = switch (columnTypes.get(i)) {
                            case Types.BOOLEAN -> rs.getBoolean(i + 1);
                            case Types.TINYINT -> rs.getByte(i + 1) != 0;
                            case Types.INTEGER -> rs.getInt(i + 1);
                            case Types.BIGINT -> rs.getLong(i + 1);
                            case Types.CHAR, Types.VARCHAR -> rs.getString(i + 1);
                            case Types.BINARY, Types.VARBINARY -> rs.getBytes(i + 1);
                            default -> 0;
                        };
                    }
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Reads a database backup into a new DatabaseBackupTable instance.
     *
     * @param is The input stream to load from.
     * @throws CheckedException A read error occurs.
     */
    public DatabaseBackupTable(InputStream is) throws CheckedException {
        try {
            //Read table name from the stream
            tableName = new String(Stream.readBytes(is), StandardCharsets.UTF_8);
            //Read column information from the stream
            int columnCount = Stream.readInteger(is);
            for (int i = 0; i < columnCount; ++i) {
                columnTypes.add(Stream.readInteger(is));
                columnNames.add(new String(Stream.readBytes(is), StandardCharsets.UTF_8));
            }
            //Read row information from the stream
            int rowCount = Stream.readInteger(is);
            for (int j = 0; j < rowCount; ++j) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnTypes.size(); ++i) {
                    row[i] = switch (columnTypes.get(i)) {
                        case Types.BOOLEAN, Types.TINYINT -> is.read() > 0;
                        case Types.INTEGER -> Stream.readInteger(is);
                        case Types.BIGINT -> Stream.readLong(is);
                        case Types.CHAR, Types.VARCHAR -> new String(Stream.readBytes(is), StandardCharsets.UTF_8);
                        case Types.BINARY, Types.VARBINARY -> Stream.readBytes(is);
                        default -> 0;
                    };
                }
                rows.add(row);
            }
        } catch (IOException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Restores a DatabaseBackupTable onto the specified database connection.
     *
     * @param conn The database connection.
     * @throws CheckedException A restore error occurs.
     */
    public void store(IDB_Connector conn) throws CheckedException {
        //Check if table exists
        if (!conn.getTableList(true).contains(tableName)) throw new CheckedException("Table does not exist");
        StringBuilder templateIndicators = new StringBuilder();
        templateIndicators.append("?, ".repeat(columnTypes.size()));
        if (templateIndicators.length() > 0) templateIndicators = new StringBuilder(templateIndicators.substring(0, templateIndicators.length() - 2));
        //Clear the existing table in the database
        try(PreparedStatement sta = conn.getStatement("DELETE FROM " + tableName)) {
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
        //Upload the backup to the database
        try(PreparedStatement sta = conn.getStatement("INSERT INTO " + tableName + " VALUES (" + templateIndicators + ")")) {
            for (Object[] row : rows) {
                for (int i = 0; i < columnTypes.size(); ++i) {
                    switch (columnTypes.get(i)) {
                        case Types.BOOLEAN, Types.TINYINT -> sta.setBoolean(i+1, (boolean) row[i]);
                        case Types.INTEGER -> sta.setInt(i+1, (int) row[i]);
                        case Types.BIGINT -> sta.setLong(i+1, (long) row[i]);
                        case Types.CHAR, Types.VARCHAR -> sta.setString(i+1, (String) row[i]);
                        case Types.BINARY, Types.VARBINARY -> sta.setBytes(i+1, (byte[]) row[i]);
                    }
                }
                sta.addBatch();
            }
            sta.executeBatch();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Writes a database backup to the specified output stream.
     *
     * @param os The database backup.
     * @throws CheckedException A write error occurs.
     */
    public void save(OutputStream os) throws CheckedException {
        try {
            //Write table name to stream
            Stream.writeBytes(os, tableName.getBytes(StandardCharsets.UTF_8));
            //Write column information to the stream
            Stream.writeInteger(os, columnTypes.size());
            for (int i = 0; i < columnTypes.size(); ++i) {
                Stream.writeInteger(os ,columnTypes.get(i));
                Stream.writeBytes(os, columnNames.get(i).getBytes(StandardCharsets.UTF_8));
            }
            //Write row information to the stream
            Stream.writeInteger(os, rows.size());
            for (Object[] row : rows) {
                for (int i = 0; i < columnTypes.size(); ++i) {
                    switch (columnTypes.get(i)) {
                        case Types.BOOLEAN, Types.TINYINT -> os.write(((boolean) row[i]) ? 1 : 0);
                        case Types.INTEGER -> Stream.writeInteger(os, (int) row[i]);
                        case Types.BIGINT -> Stream.writeLong(os, (long) row[i]);
                        case Types.CHAR, Types.VARCHAR -> Stream.writeBytes(os, ((String) row[i]).getBytes(StandardCharsets.UTF_8));
                        case Types.BINARY, Types.VARBINARY -> Stream.writeBytes(os, (byte[]) row[i]);
                    }
                }
            }
        } catch (IOException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * This static method provides the ability to stream the backup.
     *
     * @param tableName The table to back up.
     * @param conn The connection to load from.
     * @param os The output stream to write to.
     * @throws CheckedException A backup error has occurred.
     */
    public static void backup(String tableName, IDB_Connector conn, OutputStream os) throws CheckedException {
        try {
            //Check if table exists
            if (!conn.getTableList(true).contains(tableName)) throw new CheckedException("Table does not exist");
            //Write table name to stream
            Stream.writeBytes(os, tableName.getBytes(StandardCharsets.UTF_8));
            //Load the result set
            try(PreparedStatement sta = conn.getStatement("SELECT * FROM "+tableName)) {
                try (ResultSet rs = sta.executeQuery()) {
                    //Load column information from the result set
                    ResultSetMetaData rsm = rs.getMetaData();
                    List<Integer> columnTypes = new ArrayList<>();
                    //Write column information to the stream
                    Stream.writeInteger(os, rsm.getColumnCount());
                    for (int i = 1; i <= rsm.getColumnCount(); ++i) {
                        Stream.writeInteger(os, rsm.getColumnType(i));
                        columnTypes.add(rsm.getColumnType(i));
                        Stream.writeBytes(os, rsm.getColumnName(i).getBytes(StandardCharsets.UTF_8));
                    }
                    //Load the row count
                    try (PreparedStatement stac = conn.getStatement("SELECT COUNT(*) as rowCount FROM " + tableName)) {
                        try (ResultSet rsc = stac.executeQuery()) {
                            rsc.next();
                            //Write the row count
                            Stream.writeInteger(os, rsc.getInt("rowCount"));
                        }
                    }
                    //Load row information from the result set
                    //And, Write row information to the stream
                    while (rs.next()) {
                        for (int i = 0; i < rsm.getColumnCount(); ++i) {
                            switch (columnTypes.get(i)) {
                                case Types.BOOLEAN -> os.write((rs.getBoolean(i + 1)) ? 1 : 0);
                                case Types.TINYINT -> os.write((rs.getByte(i + 1) != 0) ? 1 : 0);
                                case Types.INTEGER -> Stream.writeInteger(os, rs.getInt(i + 1));
                                case Types.BIGINT -> Stream.writeLong(os, rs.getLong(i + 1));
                                case Types.CHAR, Types.VARCHAR -> Stream.writeBytes(os, rs.getString(i + 1).getBytes(StandardCharsets.UTF_8));
                                case Types.BINARY, Types.VARBINARY -> Stream.writeBytes(os, rs.getBytes(i + 1));
                            }
                        }
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * This static method provides the ability to stream the restore.
     *
     * @param conn The connection to store to.
     * @param is The input stream to read from.
     * @throws CheckedException A restore error has occurred.
     */
    public static void restore(IDB_Connector conn, InputStream is) throws CheckedException {
        try {
            //Read table name from the stream
            String tableName = new String(Stream.readBytes(is), StandardCharsets.UTF_8);
            //Check if table exists
            if (!conn.getTableList(true).contains(tableName)) throw new CheckedException("Table does not exist");
            //Read column information from the stream
            int columnCount = Stream.readInteger(is);
            List<Integer> columnTypes = new ArrayList<>();
            StringBuilder templateIndicators = new StringBuilder();
            for (int i = 0; i < columnCount; ++i) {
                columnTypes.add(Stream.readInteger(is));
                Stream.readBytes(is); //Read and discord column name
                templateIndicators.append("?, ");
            }
            if (templateIndicators.length() > 0) templateIndicators = new StringBuilder(templateIndicators.substring(0, templateIndicators.length() - 2));
            //Clear the existing table in the database
            try(PreparedStatement sta = conn.getStatement("DELETE FROM " + tableName)) {
                sta.executeUpdate();
            }
            //Read row information from the stream
            //And, Upload the backup to the database
            int rowCount = Stream.readInteger(is);
            try(PreparedStatement sta = conn.getStatement("INSERT INTO " + tableName + " VALUES (" + templateIndicators + ")")) {
                for (int j = 0; j < rowCount; ++j) {
                    for (int i = 0; i < columnTypes.size(); ++i) {
                        switch (columnTypes.get(i)) {
                            case Types.BOOLEAN, Types.TINYINT -> sta.setBoolean(i+1, is.read() > 0);
                            case Types.INTEGER -> sta.setInt(i+1, Stream.readInteger(is));
                            case Types.BIGINT -> sta.setLong(i+1, Stream.readLong(is));
                            case Types.CHAR, Types.VARCHAR -> sta.setString(i+1, new String(Stream.readBytes(is), StandardCharsets.UTF_8));
                            case Types.BINARY, Types.VARBINARY -> sta.setBytes(i+1, Stream.readBytes(is));
                        }
                    }
                    sta.addBatch();
                }
                sta.executeBatch();
            }
        } catch (IOException | SQLException e) {
            throw new CheckedException(e);
        }
    }
}
