package info.smart_tools.smartactors.core.db_storage.exceptions;

public class QueryExecutionException extends StorageException {
    public QueryExecutionException(String message) {
        super(message);
    }

    public QueryExecutionException(String message, Exception cause) {
        super(message,cause);
    }
}