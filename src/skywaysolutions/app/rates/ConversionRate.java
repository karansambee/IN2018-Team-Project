package skywaysolutions.app.rates;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConversionRate extends DatabaseEntityBase {
    private String currencyCode;
    private String currencySymbol;
    private Decimal conversionRate;

    public ConversionRate(IDB_Connector conn, String currencyCode) {
        super(conn);
        this.currencyCode = currencyCode;
    }

    ConversionRate(IDB_Connector conn, String currencyCode, String currencySymbol, Decimal conversionRate) {
        super(conn);
        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;
        this.conversionRate = conversionRate;
    }

    ConversionRate(IDB_Connector conn, ResultSet rs, boolean locked) throws SQLException, CheckedException {
        super(conn, locked);
        loadFrom(rs, locked);
    }

    /**
     * Gets the ID of the object that is used for caching.
     *
     * @return The ID of the object.
     */
    @Override
    public Object getPrimaryID() {
        return currencyCode;
    }

    @Override
    protected String getTableName() {
        return "ExchangeRate";
    }

    @Override
    protected boolean deleteAuxRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("DELETE FROM "+getAuxTableName()+" WHERE CurrencyName = ?")){
            sta.setString(1,currencyCode);
            if (sta.executeUpdate() > 0) return true;
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        return false;
    }

    @Override
    protected void createAuxRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("INSERT INTO "+getAuxTableName()+ " VALUES (?)")){
            sta.setString(1, currencyCode);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void createRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("INSERT INTO "+getTableName()+ " VALUES (?,?,?)")){
            sta.setString(1,currencyCode);
            sta.setDouble(2,conversionRate.getValue());
            sta.setString(3,currencySymbol);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void updateRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("UPDATE "+getTableName()+ " SET USDConversionRate = ?, CurrencySymbol = ? WHERE CurrencyName = ?")) {
            sta.setDouble(1,conversionRate.getValue());
            sta.setString(2,currencySymbol);
            sta.setString(3,currencyCode);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void loadRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("SELECT CurrencyName, USDConversionRate, CurrencySymbol FROM "+getTableName()+ " WHERE CurrencyName = ?")){
            sta.setString(1,currencyCode);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                currencyCode = rs.getString("CurrencyName");
                conversionRate = new Decimal(rs.getDouble("USDConversionRate"), 6);
                currencySymbol = rs.getString("CurrencySymbol");
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should load the current object from the passed result set.
     *
     * @param rs     The result set to load from.
     * @param locked If the object is considered locked.
     * @throws SQLException     An SQL error has occurred.
     * @throws CheckedException An error has occurred.
     */
    @Override
    public void loadFrom(ResultSet rs, boolean locked) throws SQLException, CheckedException {
        setLoadedAndExists();
        setLockedState(locked);
        currencyCode = rs.getString("CurrencyName");
        conversionRate = new Decimal(rs.getDouble("USDConversionRate"), 6);
        currencySymbol = rs.getString("CurrencySymbol");
    }

    @Override
    protected void deleteRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("DELETE FROM "+getTableName()+ " WHERE CurrencyName = ?")){
            sta.setString(1,currencyCode);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement(("SELECT COUNT(*) as rowCount FROM "+getTableName()+" WHERE CurrencyName = ?"))){
            sta.setString(1,currencyCode);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

    }

    public String getCurrencyCode(){
        return currencyCode;
    }

    public String getCurrencySymbol(){
        return currencySymbol;
    }

    public Decimal getConversionRate(){
        return conversionRate;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public void setConversionRate(Decimal conversionRate){
        this.conversionRate = conversionRate;
    }
}
