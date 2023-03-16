package skywaysolutions.app.customers;

/**
 * Provides the plan type enum.
 *
 * @author Alfred Manville
 */
public enum PlanType {
    /**
     * Any discount plan, used for filtering.
     */
    Any(-1),
    /**
     * Fixed discount.
     */
    FixedDiscount(0),
    /**
     * Flexible Discount.
     */
    FlexibleDiscount(1);
    private final int theValue;
    PlanType(int value) {
        theValue = value;
    }
    /**
     * Get the integer value of the plan type.
     *
     * @return The integer value of the plan type.
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
        return (theValue == 0) ? "Fixed Discount" : "Flexible Discount";
    }
    /**
     * Gets the plan type given its ID.
     *
     * @param theValueIn The plan type ID.
     * @return The plan type enum value.
     */
    public static PlanType getPlanTypeFromValue(int theValueIn) {
        return (theValueIn == 0) ? PlanType.FixedDiscount : PlanType.FlexibleDiscount;
    }
}
