package info.smart_tools.smartactors.database.async_operation_collection.exception;

import info.smart_tools.smartactors.database.async_operation_collection.AsyncOperationCollection;

/**
 * Exception for {@link AsyncOperationCollection} createAsyncOperation() method
 */
public class CreateAsyncOperationException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public CreateAsyncOperationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and cause as arguments
     * @param message specific error message
     * @param cause throwable cause
     */
    public CreateAsyncOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
