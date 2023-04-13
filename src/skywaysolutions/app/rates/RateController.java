package skywaysolutions.app.rates;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.database.IFilterStatementCreator;
import skywaysolutions.app.database.MultiLoadSyncMode;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * This class provides a rate controller to access the rates.
 *
 * @author Alfred Manville
 */
public class RateController implements IRateAccessor {
    private final IDB_Connector conn;
    private final ExchangeRateTableAccessor accessor;
    private final AllFilter allFilter = new AllFilter();
    private final Object slock = new Object();

    /**
     * Creates a new instance of RateController with the specified database connection.
     *
     * @param conn The database connection.
     * @throws CheckedException Assuring the table schema has errored.
     */
    public RateController(IDB_Connector conn) throws CheckedException {
        this.conn = conn;
        accessor = new ExchangeRateTableAccessor(conn);
        conn.getTableList(true);
        accessor.assureTableSchema();
    }

    /**
     * Refreshes a cached conversion rate.
     *
     * @param currency The currency code.
     * @throws CheckedException The cache refresh operation failed.
     */
    @Override
    public void refreshConversionRate(String currency) throws CheckedException {
        accessor.load(currency, true);
    }

    /**
     * Assures that the USD currency is defined.
     *
     * @throws CheckedException An assurance error has occurred.
     */
    @Override
    public void assureUSDCurrency() throws CheckedException {
        synchronized (slock) {
            ConversionRate rate = accessor.load("USD", true);
            if (rate.exists(true)) {
                try {
                    rate.lock();
                    rate.load();
                    rate.setCurrencySymbol("$");
                    rate.setConversionRate(new Decimal(1));
                } finally {
                    rate.unlock();
                }
            } else {
                rate = new ConversionRate(conn, "USD", "$", new Decimal(1));
                rate.store();
                accessor.cacheOne(rate);
            }
        }
    }

    /**
     * Gets the conversion rate for USD to the specified currency.
     *
     * @param currency The currency code.
     * @return The conversion rate from USD.
     * @throws CheckedException The conversion rate retrieval failed.
     */
    @Override
    public Decimal getConversionRate(String currency) throws CheckedException {
        synchronized (slock) {
            return accessor.load(currency, false).getConversionRate();
        }
    }

    /**
     * Sets the conversion rate for USD to a specified currency.
     *
     * @param currency The currency code.
     * @param rate     The new conversion rate from USD.
     * @throws CheckedException The conversion rate storing failed.
     */
    @Override
    public void setConversionRate(String currency, Decimal rate) throws CheckedException {
        synchronized (slock) {
            ConversionRate crate = accessor.load(currency, true);
            try {
                crate.lock();
                if (crate.getCurrencySymbol() == null) crate.setCurrencySymbol("");
                crate.setConversionRate(rate);
                crate.store();
            } finally {
                crate.unlock();
            }
        }
    }

    /**
     * Gets the currency symbol.
     *
     * @param currency The currency code.
     * @return The currency symbol.
     * @throws CheckedException The conversion rate retrieval failed.
     */
    @Override
    public String getCurrencySymbol(String currency) throws CheckedException {
        synchronized (slock) {
            return accessor.load(currency, false).getCurrencySymbol();
        }
    }

    /**
     * Sets the currency symbol.
     *
     * @param currency The currency code.
     * @param symbol   The new currency symbol.
     * @throws CheckedException The conversion rate storing failed.
     */
    @Override
    public void setCurrencySymbol(String currency, String symbol) throws CheckedException {
        synchronized (slock) {
            ConversionRate crate = accessor.load(currency, true);
            try {
                crate.lock();
                if (crate.getConversionRate() == null) crate.setConversionRate(new Decimal());
                crate.setCurrencySymbol(symbol);
                crate.store();
            } finally {
                crate.unlock();
            }
        }
    }

    /**
     * Removes the conversion rate and symbol for a specified currency.
     *
     * @param currency The currency code.
     * @throws CheckedException The conversion rate removal failed.
     */
    @Override
    public void removeConversionRate(String currency) throws CheckedException {
        synchronized (slock) {
            ConversionRate crate = accessor.load(currency, false);
            try {
                crate.lock();
                crate.delete();
            } finally {
                crate.unlock();
            }
        }
    }

    /**
     * Lists the registered currencies that can be converted.
     *
     * @return The list of convertable currency codes.
     * @throws CheckedException The conversion rates could not be retrieved.
     */
    @Override
    public String[] getConvertableCurrencies() throws CheckedException {
        synchronized (slock) {
            List<ConversionRate> currencies = accessor.loadMany(allFilter, MultiLoadSyncMode.NoLoad);
            String[] ids = new String[currencies.size()];
            for (int i = 0; i < ids.length; i++) ids[i] = currencies.get(i).getCurrencyCode();
            return ids;
        }
    }

    /**
     * Gets an array of tables that can be backed up.
     *
     * @return The array of tables.
     */
    @Override
    public String[] getTables() {
        return new String[] {"ExchangeRate"};
    }

    /**
     * Forces a table to be fully unlocked.
     *
     * @param tableName The table to fully unlock.
     * @throws CheckedException The table could not be unlocked.
     */
    @Override
    public void forceFullUnlock(String tableName) throws CheckedException {
        synchronized (slock) {
            if (tableName.equals("ExchangeRate")) accessor.unlockAll(true);
        }
    }

    /**
     * Forces a table to be deleted (Along with its auxiliary table).
     *
     * @param tableName The table to purge.
     * @throws CheckedException The table could not be purged.
     */
    @Override
    public void forceFullPurge(String tableName) throws CheckedException {
        synchronized (slock) {
            conn.getTableList(true);
            if (tableName.equals("ExchangeRate")) accessor.purgeTableSchema();
        }
    }

    /**
     * Assures the existence of a table.
     *
     * @param tableName The table to assure the existence of.
     * @throws CheckedException The table could not be assured.
     */
    @Override
    public void assureExistence(String tableName) throws CheckedException {
        synchronized (slock) {
            conn.getTableList(true);
            if (tableName.equals("ExchangeRate")) accessor.assureTableSchema();
        }
    }

    /**
     * Refreshes the cache of a table accessor.
     *
     * @param tableName The name of the table to refresh the cache of.
     * @throws CheckedException The table could not be refreshed.
     */
    @Override
    public void refreshCache(String tableName) throws CheckedException {
        synchronized (slock) {
            conn.getTableList(true);
            if (tableName.equals("ExchangeRate")) accessor.refreshAll();
        }
    }

    /**
     * This class provides a filter that represents no filtering.
     *
     * @author Alfred Manville
     */
    private static class AllFilter implements IFilterStatementCreator {

        /**
         * Gets a prepared statement from the specified connection,
         * using the passed string as the beginning of the SQL template.
         * <p>
         * The statement will always begin with "SELECT * FROM [TABLE NAME] WHERE ",
         * EG: "SELECT * FROM test WHERE " where the table here is test.
         * </p>
         * @param conn The database connection.
         * @param startOfSQLTemplate The start of the SQL Template to use for the statement.
         * @return The prepared statement with the filters and their parameters applied.
         * @throws SQLException An SQL error occurred.
         * @throws CheckedException An error occurred.
         */
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException {
            return conn.getStatement(startOfSQLTemplate.substring(0, startOfSQLTemplate.length() - 6) + "ORDER BY CurrencyName");
        }
    }
}
