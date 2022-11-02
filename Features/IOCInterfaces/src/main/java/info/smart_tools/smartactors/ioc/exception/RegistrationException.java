package info.smart_tools.smartactors.ioc.exception;

/**
 * Exception that occurs when registration of new IGeneralStrategy has been failed
 */
public class RegistrationException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public RegistrationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public RegistrationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public RegistrationException(final Throwable cause) {
        super(cause);
    }
}