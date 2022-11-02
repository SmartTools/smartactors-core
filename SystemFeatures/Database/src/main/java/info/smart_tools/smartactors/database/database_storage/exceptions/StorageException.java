package info.smart_tools.smartactors.database.database_storage.exceptions;

public class StorageException extends Exception {
    public StorageException(final String message) {
        super(message);
    }

    public StorageException(final String message, final Exception cause) {
        super(message, cause);
    }
}