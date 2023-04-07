package skywaysolutions.app.customers;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

public class Customer extends DatabaseEntityBase {
    private Long customerID;
    private PersonalInformation info;
    private Long planID;
    private Decimal accountDiscountCredit;
    private Decimal purchaseAccumulation;
    private Date purchaseMonthStart;
    private String currency;
    private String alias;
    private CustomerType customerType;

    public Customer(IDB_Connector conn, Long id) {
        super(conn);
        customerID = id;
    }

    public Customer(IDB_Connector conn, PersonalInformation info,
                    Long planID, boolean customerDiscountCredited,
                    String currency, String alias, CustomerType type) {
        super(conn);
        this.info = info;
        this.planID = planID;
        this.accountDiscountCredit = (customerDiscountCredited) ? new Decimal() : null;
        purchaseAccumulation = new Decimal();
        purchaseMonthStart = new MonthPeriod(Time.now()).getThisMonth();
        this.currency = currency;
        this.alias = alias;
        customerType = type;
    }

    public Customer(IDB_Connector conn, ResultSet rs, boolean locked) throws SQLException {
        super(conn, locked);
        setLoadedAndExists();
        customerID = rs.getLong("CustomerID");
        planID = ResultSetNullableReturners.getLongValue(rs, "DiscountPlanID");
        currency = rs.getString("CurrencyName");
        info = new PersonalInformation(rs.getString("Firstname"), rs.getString("Surname"), rs.getString("PhoneNumber"), ResultSetNullableReturners.getStringValue(rs, "EmailAddress"),
                Time.fromSQLDate(rs.getDate("DateOfBirth")), rs.getString("Postcode"), rs.getString("HouseNumber"), rs.getString("StreetName"));
        Double adc = ResultSetNullableReturners.getDoubleValue(rs, "AccountDiscountCredit");
        accountDiscountCredit = (adc == null) ? null : new Decimal(adc, 2);
        purchaseAccumulation = new Decimal(rs.getDouble("PurchaseAccumulation"), 2);
        purchaseMonthStart = Time.fromSQLDate(rs.getDate("PurchaseMonthBeginning"));
        alias = rs.getString("Alias");
        customerType = CustomerType.getCustomerTypeFromValue(rs.getInt("CustomerType"));
    }

    /**
     * This should return the name of the table.
     *
     * @return The name of the table.
     */
    @Override
    protected String getTableName() {
        return "Customer";
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
        try (PreparedStatement sta = conn.getStatement("DELETE FROM " + getAuxTableName() + " WHERE CustomerID = ?")) {
            sta.setLong(1, customerID);
            if (sta.executeUpdate() > 0) return true;
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
    protected void createAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            sta.setLong(1, customerID);
            sta.executeUpdate();
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
        try (PreparedStatement sta = conn.getStatement("INSERT INTO " + getTableName() + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
            if (customerID == null) sta.setNull(1, Types.BIGINT); else sta.setLong(1, customerID);
            if (planID == null) sta.setNull(2, Types.BIGINT); else sta.setLong(2, planID);
            sta.setString(3, currency);
            sta.setString(4, info.getFirstName());
            sta.setString(5, info.getLastName());
            sta.setString(6, info.getPhoneNumber());
            if (info.getEmailAddress() == null) sta.setNull(7, Types.VARCHAR); else sta.setString(7, info.getEmailAddress());
            sta.setDate(8, Time.toSQLDate(info.getDateOfBirth()));
            sta.setString(9, info.getPostcode());
            sta.setString(10, info.getHouseNumber());
            sta.setString(11, info.getStreetName());
            if (accountDiscountCredit == null)  sta.setNull(12 ,Types.NUMERIC); else sta.setDouble(12, accountDiscountCredit.getValue());
            sta.setDouble(13, purchaseAccumulation.getValue());
            sta.setDate(14, Time.toSQLDate(purchaseMonthStart));
            sta.setString(15, alias);
            sta.setInt(16, customerType.getValue());
            sta.executeUpdate();
            if (customerID == null) {
                try (PreparedStatement stac = conn.getStatement("SELECT MAX(CustomerID) as maxID FROM "+getTableName())) {
                    try (ResultSet rs = stac.executeQuery()) {
                        if (!rs.next()) throw new CheckedException("No Insert Occurred!");
                        customerID = rs.getLong("maxID");
                    }
                }
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
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
        try (PreparedStatement sta = conn.getStatement("UPDATE " + getTableName() + " SET DiscountPlanID = ?, CurrencyName  = ?, Firstname = ?," +
                " Surname = ?, PhoneNumber = ?, EmailAddress = ?, DateOfBirth = ?," +
                " Postcode = ?, HouseNumber = ?, StreetName = ?," +
                " AccountDiscountCredit = ?, PurchaseAccumulation = ?," +
                " PurchaseMonthBeginning = ?, Alias = ?, CustomerType = ? WHERE CustomerID = ?")) {
            if (planID == null) sta.setNull(1, Types.BIGINT); else sta.setLong(1, planID);
            sta.setString(2, currency);
            sta.setString(3, info.getFirstName());
            sta.setString(4, info.getLastName());
            sta.setString(5, info.getPhoneNumber());
            if (info.getEmailAddress() == null) sta.setNull(6, Types.VARCHAR); else sta.setString(6, info.getEmailAddress());
            sta.setDate(7, Time.toSQLDate(info.getDateOfBirth()));
            sta.setString(8, info.getPostcode());
            sta.setString(9, info.getHouseNumber());
            sta.setString(10, info.getStreetName());
            if (accountDiscountCredit == null)  sta.setNull(11 ,Types.NUMERIC); else sta.setDouble(11, accountDiscountCredit.getValue());
            sta.setDouble(12, purchaseAccumulation.getValue());
            sta.setDate(13, Time.toSQLDate(purchaseMonthStart));
            sta.setString(14, alias);
            sta.setInt(15, customerType.getValue());
            sta.setLong(16, customerID);
            sta.executeUpdate();
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
        try (PreparedStatement sta = conn.getStatement("SELECT CustomerID, DiscountPlanID, CurrencyName, " +
                "Firstname, Surname, PhoneNumber, EmailAddress, DateOfBirth, Postcode, HouseNumber, StreetName," +
                " AccountDiscountCredit, PurchaseAccumulation, PurchaseMonthBeginning, Alias," +
                " CustomerType FROM " + getTableName() + " WHERE CustomerID = ?")) {
            sta.setLong(1, customerID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                customerID = rs.getLong("CustomerID");
                planID = ResultSetNullableReturners.getLongValue(rs, "DiscountPlanID");
                currency = rs.getString("CurrencyName");
                info = new PersonalInformation(rs.getString("Firstname"), rs.getString("Surname"), rs.getString("PhoneNumber"), ResultSetNullableReturners.getStringValue(rs, "EmailAddress"),
                        Time.fromSQLDate(rs.getDate("DateOfBirth")), rs.getString("Postcode"), rs.getString("HouseNumber"), rs.getString("StreetName"));
                Double adc = ResultSetNullableReturners.getDoubleValue(rs, "AccountDiscountCredit");
                accountDiscountCredit = (adc == null) ? null : new Decimal(adc, 2);
                purchaseAccumulation = new Decimal(rs.getDouble("PurchaseAccumulation"), 2);
                purchaseMonthStart = Time.fromSQLDate(rs.getDate("PurchaseMonthBeginning"));
                alias = rs.getString("Alias");
                customerType = CustomerType.getCustomerTypeFromValue(rs.getInt("CustomerType"));
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
        try (PreparedStatement sta = conn.getStatement("DELETE FROM " + getTableName() + " WHERE CustomerID = ?")) {
            sta.setLong(1, customerID);
            sta.executeUpdate();
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
        try (PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM " + getTableName() + " WHERE CustomerID = ?")) {
            sta.setLong(1, customerID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    public long getCustomerID() {
        return customerID;
    }


    public PersonalInformation getInfo() {
        return info;
    }

    public void setInfo(PersonalInformation info) {
        this.info = info;
    }

    public long getPlanID() {
        return planID;
    }

    public void setPlanID(Long planID) {
        this.planID = planID;
    }

    public Decimal getPurchaseAccumulation() {
        return purchaseAccumulation;
    }

    public void setPurchaseAccumulation(Decimal purchaseAccumulation) {
        this.purchaseAccumulation = purchaseAccumulation;
    }

    public boolean isCustomerDiscountCredited() {
        return accountDiscountCredit != null;
    }

    public void setCustomerDiscountCredited(boolean customerDiscountCredited) {
        if (customerDiscountCredited && accountDiscountCredit == null) accountDiscountCredit = new Decimal();
        else if (!customerDiscountCredited) accountDiscountCredit = null;
    }

    public Date getPurchaseMonthStart() {
        return purchaseMonthStart;
    }

    public void setPurchaseMonthStart(Date purchaseMonthStart) {
        this.purchaseMonthStart = purchaseMonthStart;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public Decimal getAccountDiscountCredit() {
        return accountDiscountCredit;
    }

    public void setAccountDiscountCredit(Decimal accountDiscountCredit) {
        this.accountDiscountCredit = accountDiscountCredit;
    }

}

