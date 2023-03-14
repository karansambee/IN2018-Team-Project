package skywaysolutions.app.utils;

/**
 * Provides a checked Exception in order to make sure errors are handled.
 * Check {@link #getCause()} for the underlying exception.
 *
 * @author Alfred Manville
 */
public class CheckedException extends Exception {
    /**
     * Constructs a new instance of checked exception.
     */
    public CheckedException() {super();}

    /**
     * Constructs a new instance of checked exception with a message.
     *
     * @param message The message of the exception.
     */
    public CheckedException(String message) {super(message);}

    /**
     * Constructs a new instance of checked exception with a message and a throwable cause.
     *
     * @param message The message of the exception.
     * @param cause The throwable cause, can be null.
     */
    public CheckedException(String message, Throwable cause) {super(message, cause);}

    /**
     * Constructs a new instance of checked exception with a throwable cause.
     *
     * @param cause The throwable cause, can be null.
     */
    public CheckedException(Throwable cause) {super(cause);}
    protected CheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
