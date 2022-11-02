package info.smart_tools.smartactors.base.isynchronous_service.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.base.isynchronous_service.ISynchronousService service} instance methods when
 * service represented by that instance is not in appropriate state.
 */
public class IllegalServiceStateException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     */
    public IllegalServiceStateException(final String message) {
        super(message);
    }
}
