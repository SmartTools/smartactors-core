package info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception;

import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.IMessageBusContainer;

/**
 * Exception for error in {@link IMessageBusContainer} methods
 */
public class SendingMessageException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public SendingMessageException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public SendingMessageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public SendingMessageException(final Throwable cause) {
        super(cause);
    }
}
