package info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener}
 * when error occurs handling any event passed from the creator.
 */
public class ReceiverObjectListenerException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public ReceiverObjectListenerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public ReceiverObjectListenerException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public ReceiverObjectListenerException(final Throwable cause) {
        super(cause);
    }
}
