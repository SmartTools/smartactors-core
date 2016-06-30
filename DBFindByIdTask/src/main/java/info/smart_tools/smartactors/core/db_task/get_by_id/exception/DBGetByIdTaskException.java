package info.smart_tools.smartactors.core.db_task.get_by_id.exception;

/**
 * Custom exception for DBGetByIdTask
 */
public class DBGetByIdTaskException extends Exception {

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public DBGetByIdTaskException(final String message, final Throwable cause) {
        super(message, cause);
    }
}