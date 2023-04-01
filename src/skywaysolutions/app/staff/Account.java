package skywaysolutions.app.staff;

import skywaysolutions.app.database.DB_Connector;
import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.*;
import skywaysolutions.app.utils.Time;

import java.sql.*;

public class Account extends DatabaseEntityBase {
    private Long accountID;
    private String email;
    private PasswordString password;
    private StaffRole role;
    private Decimal commission;
    private String currency;
    private PersonalInformation info;

    /**
     * Constructs a new DatabaseEntityBase with the specified connection.
     *
     * @param conn The connection to use.
     */
    public Account(IDB_Connector conn, Long id) {
        super(conn);
        accountID = id;
    }

    public Account(IDB_Connector conn, ResultSet rs, boolean locked) throws SQLException, CheckedException {
        super(conn, locked);
        setLoadedAndExists();
        accountID = rs.getLong("StaffID");
        currency = ResultSetNullableReturners.getStringValue(rs, "CurrencyName");
        role = StaffRole.getStaffRoleFromValue(rs.getInt("StaffRole"));
        Double cr = ResultSetNullableReturners.getDoubleValue(rs, "ComissionRate");
        commission = (cr == null) ? null : new Decimal(cr, 6);
        info.setFirstName(rs.getString("Firstname"));
        info.setLastName(rs.getString("Surname"));
        info.setPhoneNumber(rs.getString("PhoneNumber"));
        info.setEmailAddress(rs.getString("EmailAddress"));
        info.setDateOfBirth(rs.getDate("DateOfBirth"));
        info.setPostcode(rs.getString("Postcode"));
        info.setHouseNumber(rs.getString("HouseNumber"));
        info.setStreetName(rs.getString("StreetName"));
        password = new PasswordString(rs.getBytes("HashedPassword"), rs.getBytes("PasswordSalt"));
    }

    public Account(IDB_Connector conn, PersonalInformation info, StaffRole role, Decimal commission, String currency, PasswordString password, Long id) {
        super(conn);
        accountID = id;
        this.info = info;
        this.role = role;
        this.commission = commission;
        this.currency = currency;
        this.password = password;
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
     * This should delete the auxiliary row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getAuxTableName()} to get the auxiliary table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @return If the row deletion occurred.
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected boolean deleteAuxRow() throws CheckedException {
        try(PreparedStatement pre = conn.getStatement(
                "DELETE FROM " + getAuxTableName() + " WHERE StaffID = ?")) {
            pre.setLong(1, accountID);
            if (pre.executeUpdate()>0)
                return true;
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        return false;
    }

    /**
     * This should insert the auxiliary row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getAuxTableName()} to get the auxiliary table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void createAuxRow() throws CheckedException {
        try(PreparedStatement pre = conn.getStatement(
                "INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            pre.setLong(1, accountID);
            pre.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should insert the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void createRow() throws CheckedException {
        DB_Connector conn = new DB_Connector();

        try(PreparedStatement pre = conn.getStatement(
                "INSERT INTO " + getTableName() + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            if (accountID == null) pre.setNull(1, Types.BIGINT); else pre.setLong(1, accountID);
            if (currency == null) pre.setNull(2, Types.VARCHAR); else pre.setString(2, currency);
            pre.setInt(3, role.getValue());
            if (commission == null) pre.setNull(4, Types.NUMERIC); else pre.setDouble(4, commission.getValue());
            pre.setString(5, info.getFirstName());
            pre.setString(6, info.getLastName());
            pre.setString(7, info.getPhoneNumber());
            pre.setString(8, info.getEmailAddress());
            pre.setDate(9, Time.toSQLDate(info.getDateOfBirth()));
            pre.setString(10, info.getPostcode());
            pre.setString(11, info.getHouseNumber());
            pre.setString(12, info.getStreetName());
            pre.setBytes(13, password.getHash());
            pre.setBytes(14, password.getSalt());
            pre.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

        if (accountID == null) {
            try (PreparedStatement pre = conn.getStatement("SELECT MAX(StaffID) as lastStaffID FROM "+getTableName())) {
                try (ResultSet rs = pre.executeQuery()) {
                    if (!rs.next()) throw new CheckedException("No Insert Occurred!");
                    accountID = rs.getLong("lastStaffID");
                }
            } catch(SQLException throwables){
                throw new CheckedException(throwables);
            }
        }
    }

    /**
     * This should update the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void updateRow() throws CheckedException {
        try(PreparedStatement pre = conn.getStatement(
                "UPDATE " + getTableName() + " SET CurrencyName = ?, StaffRole = ?, ComissionRate = ?, " +
                        "Firstname = ?, Surname = ?, PhoneNumber = ?, EmailAddress = ?, DateOfBirth = ?, Postcode = ?, " +
                        "HouseNumber = ?, StreetName = ?, HashedPassword = ?, PasswordSalt = ?" +
                        " WHERE StaffID = ?")) {
            if (currency == null) pre.setNull(1, Types.VARCHAR); else pre.setString(1, currency);
            pre.setInt(2, role.getValue());
            if (commission == null) pre.setNull(3, Types.NUMERIC); else pre.setDouble(3, commission.getValue());
            pre.setString(4, info.getFirstName());
            pre.setString(5, info.getLastName());
            pre.setString(6, info.getPhoneNumber());
            pre.setString(7, info.getEmailAddress());
            pre.setDate(8, Time.toSQLDate(info.getDateOfBirth()));
            pre.setString(9, info.getPostcode());
            pre.setString(10, info.getHouseNumber());
            pre.setString(11, info.getStreetName());
            pre.setBytes(12, password.getHash());
            pre.setBytes(13, password.getSalt());
            pre.setLong(14, accountID);
            pre.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should select the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred (EG: Row does not exist).
     */
    @Override
    protected void loadRow() throws CheckedException {
        try(PreparedStatement pre = conn.getStatement("SELECT StaffID, CurrencyName, StaffRole, " +
                "ComissionRate, Firstname, Surname, PhoneNumber, EmailAddress, DateOfBirth, Postcode, " +
                "HouseNumber, StreetName, HashedPassword, PasswordSalt FROM " + getTableName() + " WHERE StaffID = ?")){
            pre.setLong(1, accountID);
            try (ResultSet rs = pre.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                accountID = rs.getLong("StaffID");
                currency = ResultSetNullableReturners.getStringValue(rs, "CurrencyName");
                role = StaffRole.getStaffRoleFromValue(rs.getInt("StaffRole"));
                Double cr = ResultSetNullableReturners.getDoubleValue(rs, "ComissionRate");
                commission = (cr == null) ? null : new Decimal(cr, 6);
                info = new PersonalInformation(rs.getString("Firstname"), rs.getString("Surname"), rs.getString("PhoneNumber"), rs.getString("EmailAddress"),
                        Time.fromSQLDate(rs.getDate("DateOfBirth")), rs.getString("Postcode"), rs.getString("HouseNumber"), rs.getString("StreetName"));
                password = new PasswordString(rs.getBytes("HashedPassword"), rs.getBytes("PasswordSalt"));
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should delete the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void deleteRow() throws CheckedException {
        try(PreparedStatement pre = conn.getStatement("DELETE FROM " + getTableName() + " WHERE StaffID = ?")){
            pre.setLong(1, accountID);
            pre.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should check if the row corresponding to the current object exists.
     * ( COUNT(*) and WHERE is your friend)
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try(PreparedStatement pre = conn.getStatement("SELECT COUNT(*) AS rowCount FROM " + getTableName() +
                " WHERE StaffID = ?")){
            pre.setLong(1, accountID);
            try (ResultSet rs = pre.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
        } catch (SQLException throwables){
            throw new CheckedException(throwables);
        }
    }

    public Long getAccountID() {
        return accountID;
    }

    public String getEmail() {
        return email;
    }

    public PasswordString getPassword() {
        return password;
    }

    public StaffRole getRole() {
        return role;
    }

    public Decimal getCommission() {
        return commission;
    }

    public String getCurrency() {
        return currency;
    }

    public PersonalInformation getInfo() {
        return info;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(PasswordString password) {
        this.password = password;
    }

    public void setRole(StaffRole role) {
        this.role = role;
    }

    public void setCommission(Decimal commission) {
        this.commission = commission;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setInfo(PersonalInformation info) {
        this.info = info;
    }
}
