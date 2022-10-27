package info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.exception;

import info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.ISslEngineProvider;

/**
 * Exception for {@link ISslEngineProvider}
 */
public class SSLEngineProviderException extends Exception {
    /**
     * Constructor with specific error message
     * @param message specific error message
     */
    public SSLEngineProviderException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public SSLEngineProviderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     * @param cause specific cause
     */
    public SSLEngineProviderException(final Throwable cause) {
        super(cause);
    }
}
