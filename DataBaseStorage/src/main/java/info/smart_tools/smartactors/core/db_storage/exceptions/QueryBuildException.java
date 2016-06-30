package info.smart_tools.smartactors.core.db_storage.exceptions;

public class QueryBuildException extends StorageException {
    public QueryBuildException(final String message) {
        super(message);
    }

    public QueryBuildException(final String message, final Exception cause) {
        super(message, cause);
    }
}
