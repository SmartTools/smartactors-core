package info.smart_tools.smartactors.core.db_storage.exceptions;

public class QueryBuildException extends StorageException {
    public QueryBuildException(String message) {
        super(message);
    }

    public QueryBuildException(String message, Exception cause) {
        super(message,cause);
    }
}
