package info.smart_tools.smartactors.scheduler.interfaces.exceptions;

/**
 * Exception thrown by scheduler components when error occurs accessing local or remote entry storage.
 */
public class EntryStorageAccessException extends Exception {
    /**
     * The constructor.
     *
     * @param msg      the exception message
     * @param cause    cause
     */
    public EntryStorageAccessException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * The constructor.
     *
     * @param msg      the exception message
     */
    public EntryStorageAccessException(final String msg) {
        super(msg);
    }
}
