package info.smart_tools.smartactors.scheduler.interfaces.exceptions;

import info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy;

/**
 * Exception thrown by {@link ISchedulingStrategy scheduling strategy} when error
 * occurs.
 */
public class SchedulingStrategyExecutionException extends Exception {
    /**
     * The constructor.
     *
     * @param msg      the message
     * @param cause    the cause
     */
    public SchedulingStrategyExecutionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * The constructor.
     *
     * @param msg    the message
     */
    public SchedulingStrategyExecutionException(final String msg) {
        this(msg, null);
    }

    /**
     * The constructor.
     *
     * @param cause    the cause
     */
    public SchedulingStrategyExecutionException(final Throwable cause) {
        super(cause);
    }
}
