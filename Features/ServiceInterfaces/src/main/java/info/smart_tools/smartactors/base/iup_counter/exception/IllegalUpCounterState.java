package info.smart_tools.smartactors.base.iup_counter.exception;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.base.iup_counter.IUpCounter} when it's methods are called in inappropriate state.
 */
public class IllegalUpCounterState extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public IllegalUpCounterState(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public IllegalUpCounterState(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public IllegalUpCounterState(final Throwable cause) {
        super(cause);
    }
}
