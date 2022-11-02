package info.smart_tools.smartactors.scheduler.interfaces.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction scheduler action} when error occurs
 * executing it.
 */
public class SchedulerActionExecutionException extends Exception {

    /**
     * The constructor.
     *
     * @param msg      the message
     * @param cause    the cause
     */
    public SchedulerActionExecutionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
