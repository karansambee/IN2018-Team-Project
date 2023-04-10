package skywaysolutions.app.rates;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.IRepairable;

/**
 * Provides an interface for rate access.
 *
 * @author Alfred Manville
 */
public interface IRateAccessor extends IRepairable {
    /**
     * Refreshes a cached conversion rate.
     *
     * @param currency The currency code.
     * @throws CheckedException The cache refresh operation failed.
     */
    void refreshConversionRate(String currency) throws CheckedException;

    /**
     * Assures that the USD currency is defined.
     *
     * @throws CheckedException An assurance error has occurred.
     */
    void assureUSDCurrency() throws CheckedException;

    /**
     * Gets the conversion rate for USD to the specified currency.
     *
     * @param currency The currency code.
     * @return The conversion rate from USD.
     * @throws CheckedException The conversion rate retrieval failed.
     */
    Decimal getConversionRate(String currency) throws CheckedException;

    /**
     * Sets the conversion rate for USD to a specified currency.
     *
     * @param currency The currency code.
     * @param rate     The new conversion rate from USD.
     * @throws CheckedException The conversion rate storing failed.
     */
    void setConversionRate(String currency, Decimal rate) throws CheckedException;

    /**
     * Gets the currency symbol.
     *
     * @param currency The currency code.
     * @return The currency symbol.
     * @throws CheckedException The conversion rate retrieval failed.
     */
    String getCurrencySymbol(String currency) throws CheckedException;

    /**
     * Sets the currency symbol.
     *
     * @param currency The currency code.
     * @param symbol The new currency symbol.
     * @throws CheckedException The conversion rate storing failed.
     */
    void setCurrencySymbol(String currency, String symbol) throws CheckedException;

    /**
     * Removes the conversion rate and symbol for a specified currency.
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
