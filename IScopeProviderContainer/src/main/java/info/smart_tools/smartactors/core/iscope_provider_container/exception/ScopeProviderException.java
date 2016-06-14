package info.smart_tools.smartactors.core.iscope_provider_container.exception;

/**
 * Exception for runtime error in {@link info.smart_tools.smartactors.core.iscope_provider_container.IScopeProviderContainer} methods
 */
public class ScopeProviderException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ScopeProviderException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ScopeProviderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ScopeProviderException(final Throwable cause) {
        super(cause);
    }


}
