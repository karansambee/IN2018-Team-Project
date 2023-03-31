package skywaysolutions.app.sales;

import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class provides the table accessor for refunds.
 *
 * @author Alfred Manville
 */
public class RefundTableAccessor extends DatabaseTableBase<Refund> {
    /**
     * Constructs a new RefundTableAccessor with the specified connection.
     *
     * @param conn The connection to use.
     */
    public RefundTableAccessor(IDB_Connector conn) {
        super(conn);
    }

    @Override
    protected String getTableName() {
        return "Refund";
    }

    @Override
    protected Refund loadOneFrom(ResultSet rs) {
        try {
            return new Refund(conn, rs);
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    protected String getTableSchema() {
        return "RefundID                  bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                "  TranscationID             bigint(19) NOT NULL, " +
                "  RefundDate                date NOT NULL, " +
                "  LocalCurrency             integer(10) NOT NULL, " +
                "  FOREIGN KEY (TranscationID) REFERENCES Transcation(TranscationID)";
    }

    @Override
    protected String getAuxTableSchema() {
        return "RefundID                  bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT";
    }

    @Override
    protected void createAllAuxRows() throws CheckedException {
        ArrayList<Long> refundIDs = new ArrayList<>();
        try(PreparedStatement sta = conn.getStatement("SELECT RefundID FROM " + getTableName())) {
            try (ResultSet rs = sta.executeQuery()) {
                while (rs.next()) refundIDs.add(rs.getLong("RefundID"));
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        try(PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            for (long c : refundIDs) {
                sta.setLong(1, c);
                sta.addBatch();
            }
            sta.executeBatch();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }
}
