package info.smart_tools.smartactors.core.iioccontainer.exception;

/**
 * Exception that occurs when deletion of existing IGeneralStrategy has been failed
 */
public class DeletionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public DeletionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public DeletionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public DeletionException(final Throwable cause) {
        super(cause);
    }
}