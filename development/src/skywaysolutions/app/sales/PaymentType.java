package skywaysolutions.app.sales;

/**
 * Provides the payment type enum.
 *
 * @author Alfred Manville
 */
public enum PaymentType {
    /**
     * Any, used for filtering.
     */
    Any(-1),
    /**
     * Cash.
     */
    Cash(0),
    /**
     * Card.
     */
    Card(1),
    /**
     * Invoice.
     */
    Invoice(2),
    /**
     * Cheque.
     */
    Cheque(3);
    private final int theValue;
    PaymentType(int value) {
        theValue = value;
    }
    /**
     * Get the integer value of the payment type.
     *
     * @return The integer value of the payment type.
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
            case 1 -> "Card";
            case 2 -> "Invoice";
            case 3 -> "Cheque";
            default -> "Cash";
        };
    }
    /**
     * Gets the payment type given its ID.
     *
     * @param theValueIn The payment type ID.
     * @return The payment type enum value.
     */
    public static PaymentType getPaymentTypeFromValue(int theValueIn) {
        return switch (theValueIn) {
            case -1 -> PaymentType.Any;
            case 1 -> PaymentType.Card;
            case 2 -> PaymentType.Invoice;
            case 3 -> PaymentType.Cheque;
            default -> PaymentType.Cash;
        };
    }
}
