package info.smart_tools.smartactors.core.iwrapper_generator.exception;

/**
 * Exception for error in {@link info.smart_tools.smartactors.core.iwrapper_generator.IWrapperGenerator} method
 */
public class WrapperGeneratorException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public WrapperGeneratorException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public WrapperGeneratorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public WrapperGeneratorException(final Throwable cause) {
        super(cause);
    }
}
