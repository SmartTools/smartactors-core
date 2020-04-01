package info.smart_tools.smartactors.scheduler.interfaces.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorageObserver scheduler entry storage
 * observer} when any error occurs handling storage events.
 */
public class SchedulerEntryStorageObserverException extends Exception {
    /**
     * The constructor.
     *
     * @param message    the message
     * @param cause      the cause
     */
    public SchedulerEntryStorageObserverException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
