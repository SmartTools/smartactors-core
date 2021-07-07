package info.smart_tools.smartactors.database_postgresql.postgres_getbyid_task;

/**
 * Exception used by {@link PostgresGetByIdTask} task document not found by specified ID.
 */
public class DocumentNotFoundException extends Exception {
    /**
     * Class constructor with exception message.
     *
     * @param message exception message
     */
    public DocumentNotFoundException(final String message) {
        super(message);
    }

    /**
     * Class constructor with exception message and cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public DocumentNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
