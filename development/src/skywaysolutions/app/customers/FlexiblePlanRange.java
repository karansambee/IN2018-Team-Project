package skywaysolutions.app.customers;

import skywaysolutions.app.utils.Decimal;

import java.util.Objects;

/**
 * This provides the exportable flexible plan range class.
 *
 * @author Alfred Manville
 */
public final class FlexiblePlanRange {
    private final Decimal _lower;
    private final Decimal _upper;

    /**
     * Constructs a new FlexiblePlanRange with the specified lower (Inclusive) and upper (Exclusive) bounds.
     *
     * @param lower The lower bound.
     * @param upper The upper bound.
     */
    public FlexiblePlanRange(Decimal lower, Decimal upper) {
        _lower = lower;
        _upper = upper;
    }

    /**
     * Gets the lower value of the range (Inclusive).
     *
     * @return The minimum allowed value of the range.
     */
    public Decimal getLower() {
        return _lower;
    }

    /**
     * Gets the upper value of the range (Exclusive).
     *
     * @return The value that cannot be exceeded or equalled when checking using {@link #inRange(Decimal)}.
     */
    public Decimal getUpper() {
        return _upper;
    }

    /**
     * Gets if the amount is in the range.
     *
     * @param amount The amount to check.
     * @return If the amount is in the range.
     */
    public boolean inRange(Decimal amount) {
        return amount.greaterThanOrEqualTo(_lower) && amount.lessThan(_upper);
    }

    /**
     * Returns a string representation of a range.
     *
     * @return The string representation of a range.
     */
    @Override
    public String toString() {
        return _lower.toString() + "-" + _upper.toString();
    }

    /**
     * Checks if the provided FlexiblePlanRange is equivalent to this one.
     *
     * @param o The object to compare.
     * @return If the objects are equivalent.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlexiblePlanRange that)) return false;
        return Objects.equals(_lower, that._lower) && Objects.equals(_upper, that._upper);
    }
}
