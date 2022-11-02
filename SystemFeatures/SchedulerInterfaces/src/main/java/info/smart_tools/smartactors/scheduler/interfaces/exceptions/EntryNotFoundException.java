package info.smart_tools.smartactors.scheduler.interfaces.exceptions;

/**
 * Exception thrown by methods of scheduler components performing search for a scheduler entry in local or remote storage when the required
 * entry is not found there.
 */
public class EntryNotFoundException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public EntryNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public EntryNotFoundException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public EntryNotFoundException(final Throwable cause) {
        super(cause);
    }
}
