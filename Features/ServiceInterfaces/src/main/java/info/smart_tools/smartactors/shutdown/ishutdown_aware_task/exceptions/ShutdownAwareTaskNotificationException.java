package info.smart_tools.smartactors.shutdown.ishutdown_aware_task.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.shutdown.ishutdown_aware_task.IShutdownAwareTask}.
 */
public class ShutdownAwareTaskNotificationException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public ShutdownAwareTaskNotificationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public ShutdownAwareTaskNotificationException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public ShutdownAwareTaskNotificationException(final Throwable cause) {
        super(cause);
    }
}
