package skywaysolutions.app.rates;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;

/**
 * Provides an interface for rate access.
 *
 * @author Alfred Manville
 */
public interface IRateAccessor {
    /**
     * Sets the database connector used for conversion.
     *
     * @param conn The DB connector.
     */
    void setDBConverter(IDB_Connector conn);

    /**
     * Gets the conversion rate for the specified currency to USD.
     *
     * @param currency The currency code.
     * @return The conversion rate to USD.
     * @throws CheckedException The conversion rate retrieval failed.
     */
    Decimal getConversionRate(String currency) throws CheckedException;

    /**
     * Sets the conversion rate for a specified currency to USD.
     *
     * @param currency The currency code.
     * @param rate The new conversion rate to USD.
     * @throws CheckedException The conversion rate storing failed.
     */
    void setConversionRate(String currency, Decimal rate) throws CheckedException;

    /**
     * Removes the conversion rate for a specified currency.
     *
     * @param currency The currency code.
     * @throws CheckedException The conversion rate removal failed.
     */
    void removeConversionRate(String currency) throws CheckedException;

    /**
     * Lists the registered currencies that can be converted.
     *
     * @return The list of convertable currency codes.
     * @throws CheckedException The conversion rates could not be retrieved.
     */
    String[] getConvertableCurrencies() throws CheckedException;
}
