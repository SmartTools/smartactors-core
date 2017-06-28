package info.smart_tools.smartactors.task.itask_preprocess_strategy.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy} when error occurs processing
 * a task.
 */
public class TaskProcessException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public TaskProcessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public TaskProcessException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public TaskProcessException(final Throwable cause) {
        super(cause);
    }
}
