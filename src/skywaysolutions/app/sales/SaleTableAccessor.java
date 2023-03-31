package skywaysolutions.app.sales;

import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class provides the table accessor for sales.
 *
 * @author Alfred Manville
 */
public class SaleTableAccessor extends DatabaseTableBase<Sale> {

    /**
     * Constructs a new SaleTableAccessor with the specified connection.
     *
     * @param conn The connection to use.
     */
    SaleTableAccessor(IDB_Connector conn) {
        super(conn);
    }

    @Override
    protected String getTableName() {
        return "Sale";
    }

    @Override
    protected Sale loadOneFrom(ResultSet rs) {
        try {
            return new Sale(conn, rs);
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    protected String getTableSchema() {
        return "BlankNumber     bigint(19) NOT NULL, " +
                "  CustomerID      bigint(19) NOT NULL, " +
                "  CurrencyName    char(4) NOT NULL, " +
                "  SaleType        integer(1) NOT NULL, " +
                "  CommissonRate   numeric(8, 6) NOT NULL, " +
                "  SaleDate        date NOT NULL, " +
                "  DueDate         date NOT NULL, " +
                "  Cost            numeric(8, 2) NOT NULL, " +
                "  CostInUSD       numeric(8, 2), " +
                "  Tax             numeric(8, 2) NOT NULL, " +
                "  AdditionalTax   numeric(8, 2), " +
                "  PreDiscountCost numeric(8, 2), " +
                "  PRIMARY KEY (BlankNumber), " +
                "  FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID), " +
                "  FOREIGN KEY (BlankNumber) REFERENCES Blank(BlankNumber), " +
                "  FOREIGN KEY (CurrencyName) REFERENCES ExchangeRate(CurrencyName)";
    }

    @Override
    protected String getAuxTableSchema() {
        return "BlankNumber     bigint(19) NOT NULL";
    }

    @Override
    protected void createAllAuxRows() throws CheckedException {
        ArrayList<Long> saleIDs = new ArrayList<>();
        try(PreparedStatement sta = conn.getStatement("SELECT BlankNumber FROM " + getTableName())) {
            try (ResultSet rs = sta.executeQuery()) {
                while (rs.next()) saleIDs.add(rs.getLong("BlankNumber"));
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        try(PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            for (long c : saleIDs) {
                sta.setLong(1, c);
                sta.addBatch();
            }
            sta.executeBatch();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }
}
