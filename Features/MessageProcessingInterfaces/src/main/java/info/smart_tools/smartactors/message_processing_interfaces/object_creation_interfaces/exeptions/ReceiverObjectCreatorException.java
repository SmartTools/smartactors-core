package info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator}
 * when any error not related to invalid pipeline structure occurs.
 */
public class ReceiverObjectCreatorException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public ReceiverObjectCreatorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public ReceiverObjectCreatorException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public ReceiverObjectCreatorException(final Throwable cause) {
        super(cause);
    }
}
