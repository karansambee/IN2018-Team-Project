package skywaysolutions.app.sales;

import skywaysolutions.app.utils.Decimal;

/**
 * This class provides a payment structure.
 *
 * @author Alfred Manville
 */
public final class Payment {
    private final PaymentType _type;
    private final Decimal _amount;
    private final long _auxiliary; //Holds either the card number or cheque number.
    private Payment(PaymentType type, Decimal amount, long auxiliary) {
        _type = type;
        _amount = amount;
        _auxiliary = auxiliary;
    }

    /**
     * Gets a new instance of payment as a cash payment.
     *
     * @param amount The amount of cash paid.
     * @return The payment instance.
     */
    public static Payment getCashPayment(Decimal amount) {
        return new Payment(PaymentType.Cash, amount, 0);
    }

    /**
     * Gets a new instance of payment as a card payment.
     *
     * @param amount The amount paid by card.
     * @param cardNumber The card number.
     * @return The payment instance.
     */
    public static Payment getCardPayment(Decimal amount, long cardNumber) {
        return new Payment(PaymentType.Card, amount, cardNumber);
    }

    /**
     * Gets a new instance of payment as an invoice payment.
     *
     * @param amount The amount paid in the invoice.
     * @return The payment instance.
     */
    public static Payment getInvoicePayment(Decimal amount) {
        return new Payment(PaymentType.Invoice, amount, 0);
    }

    /**
     * Gets a new instance of payment as a cheque payment.
     *
     * @param amount The amount paid by cheque.
     * @param chequeNumber The cheque number.
     * @return The payment instance.
     */
    public static Payment getChequePayment(Decimal amount, long chequeNumber) {
        return new Payment(PaymentType.Cheque, amount, chequeNumber);
    }

    /**
     * Gets the type of payment.
     *
     * @return The payment type.
     */
    public PaymentType getType() {
        return _type;
    }

    /**
     * Gets the amount paid.
     *
     * @return The amount paid.
     */
    public Decimal getAmount() {
        return _amount;
    }

    /**
     * Gets the cheque number if the {@link #getType()} is {@link PaymentType#Cheque} or
     * the card number if the {@link #getType()} is {@link PaymentType#Card}.
     *
     * @return The auxiliary number.
     */
    public long getAuxiliaryNumber() {
        return _auxiliary;
    }
}
