package info.smart_tools.smartactors.database.interfaces.istorage_connection.exception;

import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;

/**
 * Exception for error in {@link IStorageConnection}
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