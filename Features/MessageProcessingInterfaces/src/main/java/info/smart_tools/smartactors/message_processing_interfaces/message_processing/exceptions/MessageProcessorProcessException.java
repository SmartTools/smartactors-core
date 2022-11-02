package info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 * Exception thrown by {@link IMessageProcessor#process(IObject, IObject)} when it cannot
 * start processing of message because of any reason.
 */
public class MessageProcessorProcessException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public MessageProcessorProcessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public MessageProcessorProcessException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public MessageProcessorProcessException(final Throwable cause) {
        super(cause);
    }
}
