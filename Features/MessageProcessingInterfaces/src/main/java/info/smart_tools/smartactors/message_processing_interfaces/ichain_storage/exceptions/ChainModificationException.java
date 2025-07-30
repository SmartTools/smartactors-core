package info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage} when error occurs
 * modifying a receiver chain.
 */
public class ChainModificationException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public ChainModificationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public ChainModificationException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public ChainModificationException(final Throwable cause) {
        super(cause);
    }
}
