package skywaysolutions.app.database;

import skywaysolutions.app.utils.*;
import skywaysolutions.app.utils.Time;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
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
                            case Types.BOOLEAN, Types.BIT -> ResultSetNullableReturners.getBooleanValue(rs, i + 1);
                            case Types.TINYINT -> getBooleanFromByte(rs, i + 1);
                            case Types.INTEGER -> ResultSetNullableReturners.getIntegerValue(rs, i + 1);
                            case Types.BIGINT -> ResultSetNullableReturners.getLongValue(rs, i + 1);
                            case Types.CHAR, Types.VARCHAR -> ResultSetNullableReturners.getStringValue(rs, i + 1);
                            case Types.BINARY, Types.VARBINARY -> ResultSetNullableReturners.getBytesValue(rs, i + 1);
                            case Types.NUMERIC, Types.DECIMAL, Types.DOUBLE, Types.FLOAT -> getLongValueFromDouble(rs, i + 1);
                            case Types.DATE -> getLongValueFromDate(rs, i + 1);
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
            tableName = getNonNullString(Stream.readBytes(is));
            //Read column information from the stream
            int columnCount = Stream.readInteger(is);
            for (int i = 0; i < columnCount; ++i) {
                columnTypes.add(Stream.readInteger(is));
                columnNames.add(getNonNullString(Stream.readBytes(is)));
            }
            //Read row information from the stream
            int rowCount = Stream.readInteger(is);
            for (int j = 0; j < rowCount; ++j) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnTypes.size(); ++i) {
                    row[i] = switch (columnTypes.get(i)) {
                        case Types.BOOLEAN, Types.TINYINT, Types.BIT -> getNullableBool(is.read());
                        case Types.INTEGER -> Stream.readNullableInteger(is);
                        case Types.BIGINT, Types.DATE, Types.NUMERIC, Types.DECIMAL, Types.DOUBLE, Types.FLOAT -> Stream.readNullableLong(is);
                        case Types.CHAR, Types.VARCHAR -> getNullableString(Stream.readBytes(is));
                        case Types.BINARY, Types.VARBINARY -> Stream.readBytes(is);
                        default -> null;
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
                    if (row[i] == null) sta.setNull(i + 1, columnTypes.get(i)); else {
                        switch (columnTypes.get(i)) {
                            case Types.BOOLEAN, Types.TINYINT, Types.BIT -> sta.setBoolean(i + 1, (boolean) row[i]);
                            case Types.INTEGER -> sta.setInt(i + 1, (int) row[i]);
                            case Types.BIGINT -> sta.setLong(i + 1, (long) row[i]);
                            case Types.CHAR, Types.VARCHAR -> sta.setString(i + 1, (String) row[i]);
                            case Types.BINARY, Types.VARBINARY -> sta.setBytes(i + 1, (byte[]) row[i]);
                            case Types.NUMERIC, Types.DECIMAL, Types.DOUBLE, Types.FLOAT -> sta.setDouble(i + 1, Decimal.fromStored((long) row[i], 8).getValue());
                            case Types.DATE -> sta.setDate(i + 1, Time.toSQLDate(new Date((long) row[i])));
                            default -> sta.setNull(i + 1, columnTypes.get(i));
                        }
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
                        case Types.BOOLEAN, Types.TINYINT, Types.BIT -> os.write(toNullableBool((Boolean) row[i]));
                        case Types.INTEGER -> Stream.writeNullableInteger(os, (Integer) row[i]);
                        case Types.BIGINT, Types.DATE, Types.NUMERIC, Types.DECIMAL, Types.DOUBLE, Types.FLOAT -> Stream.writeNullableLong(os, (Long) row[i]);
                        case Types.CHAR, Types.VARCHAR -> Stream.writeBytes(os, toNullableString((String) row[i]));
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
                                case Types.BOOLEAN, Types.BIT -> os.write(toNullableBool(ResultSetNullableReturners.getBooleanValue(rs, i + 1)));
                                case Types.TINYINT -> os.write(toNullableBool(getBooleanFromByte(rs, i + 1)));
                                case Types.INTEGER -> Stream.writeNullableInteger(os, ResultSetNullableReturners.getIntegerValue(rs, i + 1));
                                case Types.BIGINT -> Stream.writeNullableLong(os, ResultSetNullableReturners.getLongValue(rs, i + 1));
                                case Types.CHAR, Types.VARCHAR -> Stream.writeBytes(os, toNullableString(ResultSetNullableReturners.getStringValue(rs, i + 1)));
                                case Types.BINARY, Types.VARBINARY -> Stream.writeBytes(os, ResultSetNullableReturners.getBytesValue(rs, i + 1));
                                case Types.NUMERIC, Types.DECIMAL, Types.DOUBLE, Types.FLOAT -> Stream.writeNullableLong(os, getLongValueFromDouble(rs, i + 1));
                                case Types.DATE -> Stream.writeNullableLong(os, getLongValueFromDate(rs, i + 1));
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
            String tableName = getNonNullString(Stream.readBytes(is));
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
            Object v = null;
            try(PreparedStatement sta = conn.getStatement("INSERT INTO " + tableName + " VALUES (" + templateIndicators + ")")) {
                for (int j = 0; j < rowCount; ++j) {
                    for (int i = 0; i < columnTypes.size(); ++i) {
                        switch (columnTypes.get(i)) {
                            case Types.BOOLEAN, Types.TINYINT, Types.BIT -> {
                                v = getNullableBool(is.read());
                                if (v != null) sta.setBoolean(i+1, (boolean) v);
                            }
                            case Types.INTEGER -> {
                                v = Stream.readNullableInteger(is);
                                if (v != null) sta.setInt(i+1, (Integer) v);
                            }
                            case Types.BIGINT -> {
                                v = Stream.readNullableLong(is);
                                if (v != null) sta.setLong(i+1, (Long) v);
                            }
                            case Types.CHAR, Types.VARCHAR -> {
                                v = getNullableString(Stream.readBytes(is));
                                if (v != null) sta.setString(i+1, (String) v);
                            }
                            case Types.BINARY, Types.VARBINARY -> {
                                v = Stream.readBytes(is);
                                if (v != null) sta.setBytes(i+1, (byte[]) v);
                            }
                            case Types.NUMERIC, Types.DECIMAL, Types.DOUBLE, Types.FLOAT -> {
                                v = Stream.readNullableLong(is);
                                if (v != null) sta.setDouble(i+1, Decimal.fromStored((Long) v, 8).getValue());
                            }
                            case Types.DATE -> {
                                v = Stream.readNullableLong(is);
                                if (v != null) sta.setDate(i+1, Time.toSQLDate(new Date((Long) v)));
                            }
                        }
                        if (v == null) sta.setNull(i + 1, columnTypes.get(i));
                        v = null;
                    }
                    sta.addBatch();
                }
                sta.executeBatch();
            }
        } catch (IOException | SQLException e) {
            throw new CheckedException(e);
        }
    }

    private static Boolean getBooleanFromByte(ResultSet rs, int columnNumber) throws SQLException {
        Byte b = ResultSetNullableReturners.getByteValue(rs, columnNumber);
        if (b == null) return null;
        return b != 0;
    }

    private static Long getLongValueFromDouble(ResultSet rs, int columnNumber) throws SQLException {
        Double d = ResultSetNullableReturners.getDoubleValue(rs, columnNumber);
        if (d == null) return null;
        return new Decimal(d, 8).getStoredValue();
    }

    private static Long getLongValueFromDate(ResultSet rs, int columnNumber) throws SQLException {
        java.sql.Date d = ResultSetNullableReturners.getDateValue(rs, columnNumber);
        if (d == null) return null;
        return Time.fromSQLDate(d).getTime();
    }

    private static Boolean getNullableBool(int b) {
        if (b == 0) return null;
        return b > 1;
    }

    private static String getNullableString(byte[] bytes) {
        if (bytes == null) return null;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static String getNonNullString(byte[] bytes) {
        if (bytes == null) return "";
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static int toNullableBool(Boolean b) {
        if (b == null) return 0;
        return (b) ? 2 : 1;
    }

    private static byte[] toNullableString(String s) {
        if (s == null) return null;
        return s.getBytes(StandardCharsets.UTF_8);
    }
}
