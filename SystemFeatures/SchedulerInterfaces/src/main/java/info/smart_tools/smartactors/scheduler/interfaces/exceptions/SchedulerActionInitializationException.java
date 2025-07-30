package info.smart_tools.smartactors.scheduler.interfaces.exceptions;

/**
 * Error thrown by {@link info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction scheduler action} when it cannot be
 * initialized.
 */
public class SchedulerActionInitializationException extends Exception {
    /**
     * The constructor.
     *
     * @param msg      the message
     * @param cause    the cause
     */
    public SchedulerActionInitializationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
