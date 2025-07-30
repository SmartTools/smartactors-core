package info.smart_tools.smartactors.scheduler.interfaces.exceptions;

import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;

/**
 * Exception thrown by {@link ISchedulerEntry scheduer entry} {@link ISchedulerEntry#pause() pause} and {@link ISchedulerEntry#unpause()
 * unpause} methods.
 */
public class EntryPauseException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public EntryPauseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public EntryPauseException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public EntryPauseException(final Throwable cause) {
        super(cause);
    }
}
