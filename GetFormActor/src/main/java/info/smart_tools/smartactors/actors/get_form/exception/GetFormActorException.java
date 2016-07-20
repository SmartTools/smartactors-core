package info.smart_tools.smartactors.actors.get_form.exception;

/**
 * Exception for {@link info.smart_tools.smartactors.actors.get_form.GetFormActor} errors
 */
public class GetFormActorException extends Exception {

    /**
     * Constructor with specific error message and cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public GetFormActorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
