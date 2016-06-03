package info.smart_tools.smartactors.core.db_storage.exceptions;

public class StorageException extends Exception {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Exception cause) {
        super(message,cause);
    }
}