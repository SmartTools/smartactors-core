package info.smart_tools.smartactors.database.async_operation_collection.exception;

import info.smart_tools.smartactors.database.async_operation_collection.IAsyncOperationCollection;

/**
 * Exception for error in {@link IAsyncOperationCollection}
 * delete method
 */
public class DeleteAsyncOperationException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public DeleteAsyncOperationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public DeleteAsyncOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
