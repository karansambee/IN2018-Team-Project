package skywaysolutions.app.utils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class provides the ability to return null for {@link ResultSet} getters using {@link ResultSet#wasNull()}.
 *
 * @author Alfred Manville
 */
public final class ResultSetNullableReturners {
    /**
     * This gets an integer value from the passed result set.
     *
     * @param rs The result set.
     * @param columnLabel The column label name.
     * @return The integer value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Integer getIntegerValue(ResultSet rs, String columnLabel) throws SQLException {
        int toret = rs.getInt(columnLabel);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets an integer value from the passed result set.
     *
     * @param rs The result set.
     * @param columnNumber The column number (1 based).
     * @return The integer value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Integer getIntegerValue(ResultSet rs, int columnNumber) throws SQLException {
        int toret = rs.getInt(columnNumber);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a long value from the passed result set.
     *
     * @param rs The result set.
     * @param columnLabel The column label name.
     * @return The long value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Long getLongValue(ResultSet rs, String columnLabel) throws SQLException {
        long toret = rs.getLong(columnLabel);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a long value from the passed result set.
     *
     * @param rs The result set.
     * @param columnNumber The column number (1 based).
     * @return The long value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Long getLongValue(ResultSet rs, int columnNumber) throws SQLException {
        long toret = rs.getLong(columnNumber);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a double value from the passed result set.
     *
     * @param rs The result set.
     * @param columnLabel The column label name.
     * @return The double value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Double getDoubleValue(ResultSet rs, String columnLabel) throws SQLException {
        double toret = rs.getDouble(columnLabel);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a double value from the passed result set.
     *
     * @param rs The result set.
     * @param columnNumber The column number (1 based).
     * @return The double value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Double getDoubleValue(ResultSet rs, int columnNumber) throws SQLException {
        double toret = rs.getDouble(columnNumber);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a string value from the passed result set.
     *
     * @param rs The result set.
     * @param columnLabel The column label name.
     * @return The string value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static String getStringValue(ResultSet rs, String columnLabel) throws SQLException {
        String toret = rs.getString(columnLabel);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a string value from the passed result set.
     *
     * @param rs The result set.
     * @param columnNumber The column number (1 based).
     * @return The string value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static String getStringValue(ResultSet rs, int columnNumber) throws SQLException {
        String toret = rs.getString(columnNumber);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a date value from the passed result set.
     *
     * @param rs The result set.
     * @param columnLabel The column label name.
     * @return The date value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Date getDateValue(ResultSet rs, String columnLabel) throws SQLException {
        Date toret = rs.getDate(columnLabel);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a date value from the passed result set.
     *
     * @param rs The result set.
     * @param columnNumber The column number (1 based).
     * @return The date value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Date getDateValue(ResultSet rs, int columnNumber) throws SQLException {
        Date toret = rs.getDate(columnNumber);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a boolean value from the passed result set.
     *
     * @param rs The result set.
     * @param columnLabel The column label name.
     * @return The boolean value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Boolean getBooleanValue(ResultSet rs, String columnLabel) throws SQLException {
        Boolean toret = rs.getBoolean(columnLabel);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a boolean value from the passed result set.
     *
     * @param rs The result set.
     * @param columnNumber The column number (1 based).
     * @return The boolean value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Boolean getBooleanValue(ResultSet rs, int columnNumber) throws SQLException {
        Boolean toret = rs.getBoolean(columnNumber);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a byte value from the passed result set.
     *
     * @param rs The result set.
     * @param columnLabel The column label name.
     * @return The byte value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Byte getByteValue(ResultSet rs, String columnLabel) throws SQLException {
        Byte toret = rs.getByte(columnLabel);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a byte value from the passed result set.
     *
     * @param rs The result set.
     * @param columnNumber The column number (1 based).
     * @return The byte value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static Byte getByteValue(ResultSet rs, int columnNumber) throws SQLException {
        Byte toret = rs.getByte(columnNumber);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a bytes value from the passed result set.
     *
     * @param rs The result set.
     * @param columnLabel The column label name.
     * @return The bytes value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static byte[] getBytesValue(ResultSet rs, String columnLabel) throws SQLException {
        byte[] toret = rs.getBytes(columnLabel);
        if (rs.wasNull()) return null; else return toret;
    }

    /**
     * This gets a bytes value from the passed result set.
     *
     * @param rs The result set.
     * @param columnNumber The column number (1 based).
     * @return The bytes value or null.
     * @throws SQLException An SQL Error occurs.
     */
    public static byte[] getBytesValue(ResultSet rs, int columnNumber) throws SQLException {
        byte[] toret = rs.getBytes(columnNumber);
        if (rs.wasNull()) return null; else return toret;
    }
}
