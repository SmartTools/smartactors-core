package info.smart_tools.smartactors.actors.create_async_operation.exception;

import info.smart_tools.smartactors.actors.create_async_operation.CreateAsyncOperationActor;

/**
 * Exception for error in {@link CreateAsyncOperationActor}
 */
public class CreateAsyncOperationActorException extends Exception {

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public CreateAsyncOperationActorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
