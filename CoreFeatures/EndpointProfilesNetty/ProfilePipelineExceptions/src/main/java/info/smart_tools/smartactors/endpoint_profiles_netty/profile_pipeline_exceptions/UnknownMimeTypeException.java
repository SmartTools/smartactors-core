package info.smart_tools.smartactors.endpoint_profiles_netty.profile_pipeline_exceptions;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;

/**
 * Exception thrown when endpoint does not recognize MIME type of inbound message.
 */
public class UnknownMimeTypeException extends MessageHandlerException {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public UnknownMimeTypeException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public UnknownMimeTypeException(final String message) {
        super(message);
    }
}
