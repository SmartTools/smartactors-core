package info.smart_tools.smartactors.scheduler.interfaces.exceptions;

import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;

/**
 * Exception thrown by {@link ISchedulerEntry schedule entry} when error occurs
 * scheduling/cancelling it.
 */
public class EntryScheduleException extends Exception {
    /**
     * The constructor.
     *
     * @param message    the message
     * @param cause      the cause
     */
    public EntryScheduleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
