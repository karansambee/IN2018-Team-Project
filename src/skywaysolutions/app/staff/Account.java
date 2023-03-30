package skywaysolutions.app.staff;

import skywaysolutions.app.database.DB_Connector;
import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;
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

    public Account(IDB_Connector conn, ResultSet rs) throws SQLException {
        super(conn);
        accountID = rs.getLong("StaffID");
        currency = rs.getString("CurrencyName");
        role = StaffRole.getStaffRoleFromValue(rs.getInt("StaffRole"));
        commission = new Decimal(rs.getDouble("CommissionRate"), 2);
        info.setFirstName(rs.getString("Firstname"));
        info.setLastName(rs.getString("Surname"));
        info.setPhoneNumber(rs.getString("PhoneNumber"));
        info.setEmailAddress(rs.getString("EmailAddress"));
        info.setDateOfBirth(rs.getDate("DateOfBirth"));
        info.setPostcode(rs.getString("Postcode"));
        info.setHouseNumber(rs.getString("HouseNumber"));
        info.setStreetName(rs.getString("StreetName"));

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
     * Returns ID for a given email address
     *
     * @return AccountID
     */
    public static Long getID(IDB_Connector conn, String emailAddress) throws CheckedException {
        try(PreparedStatement pre = conn.getStatement(
                "SELECT StaffID FROM Staff WHERE EmailAddress = ?")){
            pre.setString(1, emailAddress);
            ResultSet rs = pre.executeQuery();
            rs.next();
            Long ID = rs.getLong("StaffID");
            pre.close();
            return ID;

        } catch (SQLException throwables){
            throw new CheckedException(throwables);
        }

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
                "DELETE FROM" + getAuxTableName() + "WHERE StaffID = ?")) {
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
                "INSERT INTO" + getAuxTableName() + "VALUES(?)")) {
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
                "INSERT INTO" + getTableName() + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            if (accountID == null) pre.setNull(1, Types.BIGINT); else pre.setLong(1, accountID);
            pre.setString(2, currency);
            pre.setInt(3, role.getValue());
            pre.setDouble(4, commission.getValue());
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
            try (PreparedStatement pre = conn.getStatement(
                    "SELECT MAX(StaffID) FROM Staff")){
            pre.executeUpdate();
            }

         catch(SQLException throwables){
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
                "UPDATE" + getTableName() + "SET CurrencyName = ?, StaffRole = ?, CommissionRate = ?, " +
                        "Firstname = ?, Surname = ?, PhoneNumber = ?, EmailAddress = ?, DateOfBirth = ?, Postcode = ?, " +
                        "HouseNumber = ?, StreetName = ?, HashedPassword = ?, PasswordSalt = ?" +
                        "WHERE StaffID = ?")) {
            pre.setString(1, currency);
            pre.setInt(2, role.getValue());
            pre.setDouble(3, commission.getValue());
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
                "CommissionRate, Firstname, Surname, PhoneNumber, EmailAddress, DateOfBirth, Postcode, " +
                "HouseNumber, StreetName, HashedPassword, PasswordSalt FROM" + getTableName() + "WHERE StaffID = ?")){
            pre.setLong(1, accountID);
            ResultSet rs = pre.executeQuery();
            rs.next();
            accountID = rs.getLong("StaffID");
            currency = rs.getString("CurrencyName");
            role = StaffRole.getStaffRoleFromValue(rs.getInt("StaffRole"));
            commission = new Decimal(rs.getDouble("CommissionRate"), 2);
            info.setFirstName(rs.getString("Firstname"));
            info.setLastName(rs.getString("Surname"));
            info.setPhoneNumber(rs.getString("PhoneNumber"));
            info.setEmailAddress(rs.getString("EmailAddress"));
            info.setDateOfBirth(rs.getDate("DateOfBirth"));
            info.setPostcode(rs.getString("Postcode"));
            info.setHouseNumber(rs.getString("HouseNumber"));
            info.setStreetName(rs.getString("StreetName"));

            password = new PasswordString(rs.getBytes("HashedPassword"), rs.getBytes("PasswordSalt"));


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
        try(PreparedStatement pre = conn.getStatement("DELETE FROM" + getTableName() + "WHERE StaffID = ?")){
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
        try(PreparedStatement pre = conn.getStatement("SELECT COUNT(*) AS rowCount FROM" + getTableName() +
                "WHERE StaffID = ?")){
            pre.setLong(1, accountID);
            ResultSet rs = pre.executeQuery();
            rs.next();
            int rc = rs.getInt("rowCount");
            rs.close();
            if (rc>0)
                return true;
            else
                return false;
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
