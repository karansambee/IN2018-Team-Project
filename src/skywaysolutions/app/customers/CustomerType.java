package skywaysolutions.app.customers;

/**
 * Provides the customer type enum.
 *
 * @author Alfred Manville
 */
public enum CustomerType {
    /**
     * Any customer type, used for filtering.
     */
    Any(-1),
    /**
     * Casual customer.
     */
    Casual(0),
    /**
     * Regular customer (Can pay late).
     */
    Regular(1),
    /**
     * Valued customer (Can pay late and has a discount).
     */
    Valued(2);
    private final int theValue;
    CustomerType(int value) {
        theValue = value;
    }
    /**
     * Get the integer value of the customer type.
     *
     * @return The integer value of the type.
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
        return switch (theValue) {
            case -1 -> "Any";
            case 1 -> "Regular";
            case 2 -> "Valued";
            default -> "Casual";
        };
    }
    /**
     * Gets the customer type given its ID.
     *
     * @param theValueIn The customer type ID.
     * @return The customer type enum value.
     */
    public static CustomerType getCustomerTypeFromValue(int theValueIn) {
        return switch (theValueIn) {
            case -1 -> CustomerType.Any;
            case 1 -> CustomerType.Regular;
            case 2 -> CustomerType.Valued;
            default -> CustomerType.Casual;
        };
    }
}
