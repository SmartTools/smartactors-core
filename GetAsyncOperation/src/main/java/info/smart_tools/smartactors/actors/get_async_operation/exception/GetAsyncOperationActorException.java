package info.smart_tools.smartactors.actors.get_async_operation.exception;

import info.smart_tools.smartactors.actors.get_async_operation.GetAsyncOperationActor;

/**
 * Exception for error in {@link GetAsyncOperationActor}
 */
public class GetAsyncOperationActorException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public GetAsyncOperationActorException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public GetAsyncOperationActorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
