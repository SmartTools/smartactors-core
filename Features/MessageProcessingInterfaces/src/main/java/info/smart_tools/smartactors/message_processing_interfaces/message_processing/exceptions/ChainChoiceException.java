package info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 * Exception thrown by {@link IMessageProcessingSequence#callChainSecurely(Object,IMessageProcessor)}
 * when it's impossible to choose a chain to call.
 */
public class ChainChoiceException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ChainChoiceException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ChainChoiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
