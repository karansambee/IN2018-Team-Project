package skywaysolutions.app.sales;

import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class provides the table accessor for transactions.
 *
 * @author Alfred Manville
 */
public class TransactionTableAccessor extends DatabaseTableBase<Transaction> {

    /**
     * Constructs a new TransactionTableAccessor with the specified connection.
     *
     * @param conn The connection to use.
     */
    TransactionTableAccessor(IDB_Connector conn) {
        super(conn);
    }
    @Override
    protected String getTableName() {
        return "Transcation";
    }

    @Override
    protected String getIDColumnName() {
        return "TranscationID";
    }

    @Override
    protected Transaction loadOneFrom(ResultSet rs, boolean locked) throws CheckedException {
        try {
            return new Transaction(conn, rs, locked);
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected Transaction noLoadOneFrom(ResultSet rs) throws CheckedException {
        try {
            return new Transaction(conn, rs.getLong(getIDColumnName()));
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected String getTableSchema() {
        return "TranscationID   bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                "  BlankNumber     bigint(19) NOT NULL, " +
                "  CurrencyName    char(4) NOT NULL, " +
                "  AmountPaid      numeric(10, 2) NOT NULL, " +
                "  AmountPaidInUSD numeric(10, 2), " +
                "  TransactionDate date NOT NULL, " +
                "  PaymentType     integer(1) NOT NULL, " +
                "  CardNumber      bigint(19), " +
                "  ChequeNumber    bigint(19), " +
                "  FOREIGN KEY (BlankNumber) REFERENCES Sale(BlankNumber), " +
                "  FOREIGN KEY (CurrencyName) REFERENCES ExchangeRate(CurrencyName)";
    }

    @Override
    protected String getAuxTableSchema() {
        return "TranscationID   bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT";
    }

    /**
     * Gets the ID of the object in the result set position.
     *
     * @param rs The result set.
     * @return The ID.
     * @throws SQLException An SQL error has occurred.
     */
    @Override
    protected Object getObjectID(ResultSet rs) throws SQLException {
        return rs.getLong(getIDColumnName());
    }

    /**
     * Loads one object via its ID.
     *
     * @param ID The ID of the object to load.
     * @return The object.
     * @throws CheckedException A load error has occurred.
     */
    @Override
    protected Transaction loadOne(Object ID) throws CheckedException {
        return internalLoad(new Transaction(conn, (Long) ID));
    }

    @Override
    protected void createAllAuxRows() throws CheckedException {
        createAllAuxRowsLongID();
    }
}
