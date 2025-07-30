package info.smart_tools.smartactors.database.database_storage.exceptions;

/**
 * Throw when query can't be executed
 */
public class QueryExecutionException extends StorageException {
    /**
     *
     * @param message Using for say what happen
     */
    public QueryExecutionException(final String message) {
        super(message);
    }

    /**
     * @param message Using for say what happen
     * @param cause Using for say reason of exception
     */
    public QueryExecutionException(final String message, final Exception cause) {
        super(message, cause);
    }
}