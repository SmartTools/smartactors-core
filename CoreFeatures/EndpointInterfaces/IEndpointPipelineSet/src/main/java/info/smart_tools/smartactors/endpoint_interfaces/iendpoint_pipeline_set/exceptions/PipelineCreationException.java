package info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions;

/**
 * Exception thrown when error occurs creating a endpoint pipeline.
 */
public class PipelineCreationException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public PipelineCreationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public PipelineCreationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public PipelineCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public PipelineCreationException(final Throwable cause) {
        super(cause);
    }
}
