package info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.base.exceptions;

/**
 * Exception thrown by some methods of message codec's.
 */
public class BlockCodecException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public BlockCodecException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public BlockCodecException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public BlockCodecException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public BlockCodecException(final Throwable cause) {
        super(cause);
    }
}
