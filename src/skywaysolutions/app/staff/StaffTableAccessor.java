package skywaysolutions.app.staff;

import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StaffTableAccessor extends DatabaseTableBase<Account> {
    ArrayList<Long> staffIDs;

    /**
     * Constructs a new DatabaseTableBase with the specified connection.
     *
     * @param conn The connection to use.
     */
    public StaffTableAccessor(IDB_Connector conn) {
        super(conn);
    }

    /**
     * This should return the name of the table.
     *
     * @return The name of the table.
     */
    @Override
    protected String getTableName() {
        return "Staff";
    }

    /**
     * This loads one instance of {@link T} from the current result set.
     * DO NOT call {@link ResultSet#next()}.
     * <p>
     * This means that the class extending {@link DatabaseEntityBase} should have a
     * constructor that takes a {@link IDB_Connector} and {@link ResultSet}
     * to allow for a direct load to occur.
     * </p>
     *
     * @param rs The result set to use for loading.
     * @return An instance of {@link T}.
     */
    @Override
    protected Account loadOneFrom(ResultSet rs) {
        try {
            return new Account(conn, rs);
        } catch (SQLException e) {
            return null;
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
        return "StaffID        bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                "CurrencyName   char(4), " +
                "StaffRole      integer(1) NOT NULL, " +
                "ComissionRate  numeric(8, 6), " +
                "Firstname      varchar(15) NOT NULL, " +
                "Surname        varchar(15) NOT NULL, " +
                "PhoneNumber    varchar(15) NOT NULL, " +
                "EmailAddress   varchar(25) NOT NULL UNIQUE, " +
                "DateOfBirth    date NOT NULL, " +
                "Postcode       varchar(7) NOT NULL, " +
                "HouseNumber    varchar(4) NOT NULL, " +
                "StreetName     varchar(20) NOT NULL, " +
                "HashedPassword binary(32) NOT NULL, " +
                "PasswordSalt   binary(32) NOT NULL, " +
                "FOREIGN KEY (CurrencyName) REFERENCES ExchangeRate(CurrencyName)";
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
        return "StaffID        bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT";
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
        ArrayList<Long> staffIDs = new ArrayList<>();
        try(PreparedStatement sta = conn.getStatement("SELECT StaffID FROM " + getTableName())) {
            ResultSet rs = sta.executeQuery();
            while (rs.next()) staffIDs.add(rs.getLong("StaffID"));
            rs.close();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        try(PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            for (long c : staffIDs) {
                sta.setLong(1, c);
                sta.addBatch();
            }
            sta.executeBatch();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }
}

