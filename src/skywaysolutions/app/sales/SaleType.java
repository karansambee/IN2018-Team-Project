package skywaysolutions.app.sales;

/**
 * Provides the sale type enum.
 *
 * @author Alfred Manville
 */
public enum SaleType {
    /**
     * Domestic sale.
     */
    Domestic(0),
    /**
     * Interline Sale.
     */
    Interline(1);
    private final int theValue;
    SaleType(int value) {
        theValue = value;
    }
    /**
     * Get the integer value of the sale type.
     *
     * @return The integer value of the sale type.
     */
    public int getValue() {
        return theValue;
    }
    /**
     * Gets a string that can be displayed.
     *
     * @return The display string.
     */
    @Override
    public String toString() {
        return (theValue == 0) ? "Domestic" : "Interline";
    }
    /**
     * Gets the sale type given its ID.
     *
     * @param theValueIn The sale type ID.
     * @return The sale type enum value.
     */
    public static SaleType getSaleTypeFromValue(int theValueIn) {
        return (theValueIn == 0) ? SaleType.Domestic : SaleType.Interline;
    }
}
