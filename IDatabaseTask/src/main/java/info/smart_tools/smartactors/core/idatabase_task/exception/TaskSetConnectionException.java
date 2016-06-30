package info.smart_tools.smartactors.core.idatabase_task.exception;

public class TaskSetConnectionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public TaskSetConnectionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public TaskSetConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public TaskSetConnectionException(final Throwable cause) {
        super(cause);
    }
}
