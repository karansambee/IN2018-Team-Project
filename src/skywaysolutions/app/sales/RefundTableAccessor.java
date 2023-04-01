package skywaysolutions.app.sales;

import skywaysolutions.app.database.DatabaseTableBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    RefundTableAccessor(IDB_Connector conn) {
        super(conn);
    }

    @Override
    protected String getTableName() {
        return "Refund";
    }

    @Override
    protected Refund loadOneFrom(ResultSet rs, boolean locked) throws CheckedException {
        try {
            return new Refund(conn, rs, locked);
        } catch (SQLException e) {
            throw new CheckedException(e);
        }
    }

    @Override
    protected Refund noLoadOneFrom(ResultSet rs) throws CheckedException {
        try {
            return new Refund(conn, rs.getLong("RefundID"));
        } catch (SQLException e) {
            throw new CheckedException(e);
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
        createAllAuxRowsLongID("RefundID");
    }
}
