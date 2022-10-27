package info.smart_tools.smartactors.scheduler.interfaces.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter scheduler entry filter} when error
 * occurs deciding if entry state transaction should be performed. Such error may most probably occur during read of entry state.
 */
public class SchedulerEntryFilterException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public SchedulerEntryFilterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public SchedulerEntryFilterException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public SchedulerEntryFilterException(final Throwable cause) {
        super(cause);
    }
}
