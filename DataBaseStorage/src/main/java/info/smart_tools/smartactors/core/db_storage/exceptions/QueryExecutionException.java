package info.smart_tools.smartactors.core.db_storage.exceptions;

public class QueryExecutionException extends StorageException {
    public QueryExecutionException(final String message) {
        super(message);
    }

    public QueryExecutionException(final String message, final Exception cause) {
        super(message, cause);
    }
}