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

    /**
     * Constructs a new DatabaseEntityBase with the specified connection..
     *
     * @param conn The connection to use.
     */

    public ConversionRate(IDB_Connector conn) {
        super(conn);
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
            sta.setString(2,currencySymbol);
            sta.setDouble(3,conversionRate.getValue());
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void updateRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("UPDATE "+getTableName()+ " SET USDConversionRate = ? ,CurrencySymbol = ? WHERE CurrencyName = ?")) {
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
        try(PreparedStatement sta = conn.getStatement("SELECT CurrencyName , USDConversionRate , CurrencySymbol FROM "+getTableName()+ " WHERE CurrencyName = ?")){
            sta.setString(1,currencyCode);
            ResultSet rs = sta.executeQuery();
            rs.next();
            currencyCode = rs.getString("CurrencyName:");
            conversionRate = new Decimal(rs.getDouble("ConversionRate"),6);
            currencySymbol = rs.getString("CurrencySymbol");
            rs.close();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
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
            ResultSet rs = sta.executeQuery();
            rs.next();
            int rc = rs.getInt("rowCount");
            rs.close();
            return rc > 0;
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


    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public void setConversionRate(Decimal conversionRate){
        this.conversionRate = conversionRate;
    }


}
