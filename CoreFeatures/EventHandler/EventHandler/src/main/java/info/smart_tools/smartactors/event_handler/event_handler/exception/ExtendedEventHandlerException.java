package info.smart_tools.smartactors.event_handler.event_handler.exception;

/**
 * The checked exception that signals about incorrect execution of methods
 * {@link info.smart_tools.smartactors.event_handler.event_handler.IExtendedEventHandler#addProcessor(Object, Object)} or
 * {@link info.smart_tools.smartactors.event_handler.event_handler.IExtendedEventHandler#removeProcessor(Object)}
 *
 */
public class ExtendedEventHandlerException extends Exception {

    /**
     * Constructor with a specific error message as the argument
     * @param message the specific error message
     */
    public ExtendedEventHandlerException(final String message) {
        super(message);
    }

    /**
     * Constructor with a specific error message and a specific cause as arguments
     * @param message the specific error message
     * @param cause the specific cause
     */
    public ExtendedEventHandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a specific cause as the argument
     * @param cause the specific cause
     */
    public ExtendedEventHandlerException(final Throwable cause) {
        super(cause);
    }
}
