package skywaysolutions.app.sales;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.ResultSetNullableReturners;
import skywaysolutions.app.utils.Time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * This class provides an entity representation of a sale.
 *
 * @author Alfred Manville
 */
public class Sale extends DatabaseEntityBase {
    private long blankNumber;
    private long customerID;
    private SaleType saleType;
    private Decimal commissionRate;
    private Date saleDate;
    private Date dueDate;
    private Decimal cost;
    private Decimal costInUSD;
    private Decimal tax;
    private Decimal additionalTax;
    private String currency;
    private Decimal preDiscountCost;

    /**
     * Constructs a new instance of sale of the specified blankNumber.
     *
     * @param conn The database connection.
     * @param blankNumber The blank number being sold.
     */
    Sale(IDB_Connector conn, long blankNumber) {
        super(conn);
        this.blankNumber = blankNumber;
    }

    /**
     * Constructs a new instance of sale for the specified blank number, customer ID, type, commission rate, due date,
     * sale date, cost, cost in USD, tax, secondary tax, currency of sale and pre discount amount.
     *
     * @param conn The database connection.
     * @param blankNumber The blank number being sold.
     * @param customerID The ID of the customer being sold to.
     * @param type The type of sale.
     * @param commissionRate The commission rate used.
     * @param dueDate The due date of the sale.
     * @param saleDate The date of sale.
     * @param cost The cost of the sale.
     * @param costInUSD The cost of the sale in USD (Can be null).
     * @param tax The tax of the sale.
     * @param secondaryTax The secondary tax of the sale (Can be null).
     * @param currency The currency the sale was made in.
     * @param preDiscountCost The cost of the sale before discounting.
     */
    Sale(IDB_Connector conn, long blankNumber, long customerID, SaleType type, Decimal commissionRate, Date dueDate, Date saleDate, Decimal cost, Decimal costInUSD, Decimal tax, Decimal secondaryTax, String currency, Decimal preDiscountCost) {
        this(conn, blankNumber);
        this.customerID = customerID;
        this.saleType = type;
        this.commissionRate = commissionRate;
        this.dueDate = dueDate;
        this.saleDate = saleDate;
        this.cost = cost;
        this.costInUSD = costInUSD;
        this.tax = tax;
        this.additionalTax = secondaryTax;
        this.currency = currency;
        this.preDiscountCost = preDiscountCost;
    }

    Sale(IDB_Connector conn, ResultSet rs) throws SQLException {
        super(conn);
        blankNumber = rs.getLong("BlankNumber");
        customerID = rs.getLong("CustomerID");
        currency = rs.getString("CurrencyName");
        saleType = SaleType.getSaleTypeFromValue(rs.getInt("SaleType"));
        commissionRate = new Decimal(rs.getDouble("CommissonRate"),2);
        saleDate = Time.fromSQLDate(rs.getDate("SaleDate"));
        dueDate = Time.fromSQLDate(rs.getDate("DueDate"));
        cost = new Decimal(rs.getDouble("Cost"), 2);
        Double ciusd = ResultSetNullableReturners.getDoubleValue(rs, "CostInUSD");
        costInUSD = (ciusd == null) ? null : new Decimal(ciusd, 2);
        tax = new Decimal(rs.getDouble("Tax"), 2);
        Double at = ResultSetNullableReturners.getDoubleValue(rs, "AdditionalTax");
        additionalTax = (at == null) ? null : new Decimal(at, 2);
        Double pdc = ResultSetNullableReturners.getDoubleValue(rs, "PreDiscountCost");
        preDiscountCost = (pdc == null) ? null : new Decimal(pdc, 2);
    }

    @Override
    protected String getTableName() {
        return "Sale";
    }

    @Override
    protected boolean deleteAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("DELETE FROM "+getAuxTableName()+" WHERE BlankNumber = ?")) {
            sta.setLong(1, blankNumber);
            return sta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void createAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO "+getAuxTableName()+" VALUES (?)")) {
            sta.setLong(1, blankNumber);
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void createRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO "+getTableName()+" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)")) {
            sta.setLong(1, blankNumber);
            sta.setLong(2, customerID);
            sta.setString(3, currency);
            sta.setInt(4, saleType.getValue());
            sta.setDouble(5, commissionRate.getValue());
            sta.setDate(6, Time.toSQLDate(saleDate));
            sta.setDate(7, Time.toSQLDate(dueDate));
            sta.setDouble(8, cost.getValue());
            if (costInUSD == null) sta.setNull(9, Types.NUMERIC); else sta.setDouble(9 ,costInUSD.getValue());
            sta.setDouble(10, tax.getValue());
            if (additionalTax == null) sta.setNull(11, Types.NUMERIC); else sta.setDouble(11, additionalTax.getValue());
            if (preDiscountCost == null) sta.setNull(12, Types.NUMERIC); else sta.setDouble(12 ,preDiscountCost.getValue());
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void updateRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("UPDATE "+getTableName()+" SET CustomerID = ?, CurrencyName = ?, SaleType = ?, CommissonRate = ?, SaleDate = ?, DueDate = ?, Cost = ?, CostInUSD = ?, Tax = ?, AdditionalTax = ?, PreDiscountCost = ? WHERE BlankNumber = ?")) {
            sta.setLong(1, customerID);
            sta.setString(2, currency);
            sta.setInt(3, saleType.getValue());
            sta.setDouble(4, commissionRate.getValue());
            sta.setDate(5, Time.toSQLDate(saleDate));
            sta.setDate(6, Time.toSQLDate(dueDate));
            sta.setDouble(7, cost.getValue());
            if (costInUSD == null) sta.setNull(8, Types.NUMERIC); else sta.setDouble(8 ,costInUSD.getValue());
            sta.setDouble(9, tax.getValue());
            if (additionalTax == null) sta.setNull(10, Types.NUMERIC); else sta.setDouble(10, additionalTax.getValue());
            if (preDiscountCost == null) sta.setNull(11, Types.NUMERIC); else sta.setDouble(11 ,preDiscountCost.getValue());
            sta.setLong(12, blankNumber);
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void loadRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT BlankNumber, CustomerID, CurrencyName, SaleType, CommissonRate, SaleDate, DueDate, Cost, CostInUSD, Tax, AdditionalTax, PreDiscountCost FROM "+getTableName()+" WHERE BlankNumber = ?")) {
            sta.setLong(1, blankNumber);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                blankNumber = rs.getLong("BlankNumber");
                customerID = rs.getLong("CustomerID");
                currency = rs.getString("CurrencyName");
                saleType = SaleType.getSaleTypeFromValue(rs.getInt("SaleType"));
                commissionRate = new Decimal(rs.getDouble("CommissonRate"),2);
                saleDate = Time.fromSQLDate(rs.getDate("SaleDate"));
                dueDate = Time.fromSQLDate(rs.getDate("DueDate"));
                cost = new Decimal(rs.getDouble("Cost"), 2);
                Double ciusd = ResultSetNullableReturners.getDoubleValue(rs, "CostInUSD");
                costInUSD = (ciusd == null) ? null : new Decimal(ciusd, 2);
                tax = new Decimal(rs.getDouble("Tax"), 2);
                Double at = ResultSetNullableReturners.getDoubleValue(rs, "AdditionalTax");
                additionalTax = (at == null) ? null : new Decimal(at, 2);
                Double pdc = ResultSetNullableReturners.getDoubleValue(rs, "PreDiscountCost");
                preDiscountCost = (pdc == null) ? null : new Decimal(pdc, 2);
            }
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected void deleteRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("DELETE FROM "+getTableName()+" WHERE BlankNumber = ?")) {
            sta.setLong(1, blankNumber);
            sta.executeUpdate();
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM "+getTableName()+" WHERE BlankNumber = ?")) {
            sta.setLong(1, blankNumber);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Gets the blank number of the sale (Which is also the sale ID).
     *
     * @return The blank number / sale ID.
     */
    public long getBlankNumber() {
        return blankNumber;
    }

    /**
     * Gets the customer the sale is for.
     *
     * @return The customer ID.
     */
    public long getCustomerID() {
        return customerID;
    }

    /**
     * Sets the customer the sale is for.
     *
     * @param customerID The customer ID.
     */
    public void setCustomerID(long customerID) {
        this.customerID = customerID;
    }

    /**
     * Gets the sale type.
     *
     * @return The sale type.
     */
    public SaleType getSaleType() {
        return saleType;
    }

    /**
     * Sets the sale type.
     *
     * @param saleType The sale type.
     */
    public void setSaleType(SaleType saleType) {
        this.saleType = saleType;
    }

    /**
     * Gets the commission rate.
     *
     * @return The commission rate.
     */
    public Decimal getCommissionRate() {
        return commissionRate;
    }

    /**
     * Sets the commission rate.
     *
     * @param commissionRate The commission rate.
     */
    public void setCommissionRate(Decimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    /**
     * Gets the date of sale.
     *
     * @return The date of sale.
     */
    public Date getSaleDate() {
        return saleDate;
    }

    /**
     * Sets the date of sale.
     *
     * @param saleDate The date of sale.
     */
    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    /**
     * Gets the due date of payment.
     *
     * @return The due date.
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date of payment.
     *
     * @param dueDate The due date.
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the cost of sale.
     *
     * @return The cost.
     */
    public Decimal getCost() {
        return cost;
    }

    /**
     * Sets the cost of sale.
     *
     * @param cost The cost.
     */
    public void setCost(Decimal cost) {
        this.cost = cost;
    }

    /**
     * Gets the cost of sale in USD.
     *
     * @return The cost of sale (USD).
     */
    public Decimal getCostInUSD() {
        return costInUSD;
    }

    /**
     * Sets the cost of sale in USD.
     *
     * @param costInUSD The cost of sale (USD).
     */
    public void setCostInUSD(Decimal costInUSD) {
        this.costInUSD = costInUSD;
    }

    /**
     * Gets the amount of tax added.
     *
     * @return The amount of tax added.
     */
    public Decimal getTax() {
        return tax;
    }

    /**
     * Sets the amount of tax added.
     *
     * @param tax The amount of tax added.
     */
    public void setTax(Decimal tax) {
        this.tax = tax;
    }

    /**
     * Gets the amount of additional tax added.
     *
     * @return The amount of additional tax to add.
     */
    public Decimal getAdditionalTax() {
        return additionalTax;
    }

    /**
     * Sets the amount of additional tax added.
     *
     * @param additionalTax The amount of additional tax to add.
     */
    public void setAdditionalTax(Decimal additionalTax) {
        this.additionalTax = additionalTax;
    }

    /**
     * Gets the currency the sale is made in.
     *
     * @return The currency of the sale.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency the sale is made in.
     *
     * @param currency The currency of the sale.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the pre discount cost.
     *
     * @return The pre-discount cost.
     */
    public Decimal getPreDiscountCost() {
        return preDiscountCost;
    }

    /**
     * Sets the pre discount cost.
     *
     * @param preDiscountCost The pre-discount cost.
     */
    public void setPreDiscountCost(Decimal preDiscountCost) {
        this.preDiscountCost = preDiscountCost;
    }
}
