package info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions;

/**
 * Exception thrown when description of required endpoint pipeline is not found.
 */
public class PipelineDescriptionNotFoundException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public PipelineDescriptionNotFoundException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public PipelineDescriptionNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public PipelineDescriptionNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public PipelineDescriptionNotFoundException(final Throwable cause) {
        super(cause);
    }
}

