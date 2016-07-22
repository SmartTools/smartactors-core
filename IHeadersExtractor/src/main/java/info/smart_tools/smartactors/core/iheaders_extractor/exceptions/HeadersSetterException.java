package info.smart_tools.smartactors.core.iheaders_extractor.exceptions;

/**
 * Exception for {@link info.smart_tools.smartactors.core.iheaders_extractor.IHeadersSetter}
 */
public class HeadersSetterException extends Exception {
    /**
     * Default constructor
     */
    public HeadersSetterException() {
        super();
    }

    /**
     * Constructor with specific error message
     * @param message specific error message
     */

    public HeadersSetterException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public HeadersSetterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     * @param cause specific cause
     */

    public HeadersSetterException(final Throwable cause) {
        super(cause);
    }
}
