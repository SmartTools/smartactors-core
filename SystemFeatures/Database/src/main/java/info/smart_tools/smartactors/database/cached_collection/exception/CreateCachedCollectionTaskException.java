package info.smart_tools.smartactors.database.cached_collection.exception;

/**
 * Exception for constructors task-facades
 */
public class CreateCachedCollectionTaskException extends Exception {

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public CreateCachedCollectionTaskException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
