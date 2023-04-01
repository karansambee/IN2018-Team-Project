package skywaysolutions.app.sales;

import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    protected String getIDColumnName() {
        return "BlankNumber";
    }

    @Override
    protected Sale loadOneFrom(ResultSet rs, boolean locked) throws CheckedException {
        try {
            return new Sale(conn, rs, locked);
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected Sale noLoadOneFrom(ResultSet rs) throws CheckedException {
        try {
            return new Sale(conn, rs.getLong(getIDColumnName()));
        } catch (SQLException e) {
            throw new CheckedException(e);
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
        createAllAuxRowsLongID();
    }
}
