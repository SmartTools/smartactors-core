package info.smart_tools.smartactors.core.istorage_connection.exception;

/**
 * Exception for error in {@link info.smart_tools.smartactors.core.istorage_connection.IStorageConnection}
 */
public class StorageException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public StorageException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public StorageException(final String message, final Exception cause) {
        super(message, cause);
    }
}