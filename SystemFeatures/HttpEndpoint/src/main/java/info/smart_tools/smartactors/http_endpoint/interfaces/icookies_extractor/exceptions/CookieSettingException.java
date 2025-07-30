package info.smart_tools.smartactors.http_endpoint.interfaces.icookies_extractor.exceptions;

import info.smart_tools.smartactors.http_endpoint.interfaces.icookies_extractor.ICookiesSetter;

/**
 * Exception for {@link ICookiesSetter}
 */
public class CookieSettingException extends Exception {

    /**
     * Default constructor
     */
    public CookieSettingException() {
        super();
    }


    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public CookieSettingException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public CookieSettingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     * @param cause specific cause
     */

    public CookieSettingException(final Throwable cause) {
        super(cause);
    }
}
