package info.smart_tools.smartactors.core.db_storage.exceptions;

/**
 * Called when query can't be built
 */
public class QueryBuildException extends StorageException {
    /**
     * @param message Using for say what happen
     */
    public QueryBuildException(final String message) {
        super(message);
    }

    /**
     * @param message Using for say what happen
     * @param cause Using for say reason of exception
     */
    public QueryBuildException(final String message, final Exception cause) {
        super(message, cause);
    }
}
