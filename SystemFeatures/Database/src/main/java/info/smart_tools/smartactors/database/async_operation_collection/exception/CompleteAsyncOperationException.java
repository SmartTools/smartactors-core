package info.smart_tools.smartactors.database.async_operation_collection.exception;

import info.smart_tools.smartactors.database.async_operation_collection.IAsyncOperationCollection;

/**
 * Exception for error in {@link IAsyncOperationCollection}
 * complete method
 */
public class CompleteAsyncOperationException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public CompleteAsyncOperationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public CompleteAsyncOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
