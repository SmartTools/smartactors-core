package info.smart_tools.smartactors.core.iclass_generator.exception;

/**
 * Exception that occurs when deletion of existing IGeneralStrategy has been failed
 */
public class ClassGenerationException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ClassGenerationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ClassGenerationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ClassGenerationException(final Throwable cause) {
        super(cause);
    }
}