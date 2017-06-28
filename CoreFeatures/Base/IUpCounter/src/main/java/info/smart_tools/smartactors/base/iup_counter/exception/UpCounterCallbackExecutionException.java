package info.smart_tools.smartactors.base.iup_counter.exception;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.base.iup_counter.IUpCounter} when error occurs executing callback(s).
 */
public class UpCounterCallbackExecutionException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public UpCounterCallbackExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public UpCounterCallbackExecutionException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public UpCounterCallbackExecutionException(final Throwable cause) {
        super(cause);
    }
}
