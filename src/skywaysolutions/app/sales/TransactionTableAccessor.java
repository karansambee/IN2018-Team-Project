package skywaysolutions.app.sales;

import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
    public TransactionTableAccessor(IDB_Connector conn) {
        super(conn);
    }
    @Override
    protected String getTableName() {
        return "Transcation";
    }

    @Override
    protected Transaction loadOneFrom(ResultSet rs) {
        try {
            return new Transaction(conn, rs);
        } catch (SQLException e) {
            return null;
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

    @Override
    protected void createAllAuxRows() throws CheckedException {
        ArrayList<Long> transactionIDs = new ArrayList<>();
        try(PreparedStatement sta = conn.getStatement("SELECT TranscationID FROM " + getTableName())) {
            try (ResultSet rs = sta.executeQuery()) {
                while (rs.next()) transactionIDs.add(rs.getLong("TranscationID"));
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        try(PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            for (long c : transactionIDs) {
                sta.setLong(1, c);
                sta.addBatch();
            }
            sta.executeBatch();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }
}
