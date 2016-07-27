package info.smart_tools.smartactors.actors.validate_form_data.exception;

/**
 * Exception for {@link info.smart_tools.smartactors.actors.validate_form_data.ValidateFormDataActor} errors
 */
public class ValidateFormException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ValidateFormException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ValidateFormException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ValidateFormException(final Throwable cause) {
        super(cause);
    }
}
