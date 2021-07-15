package info.smart_tools.smartactors.launcher.interfaces.exception.iproperties_reader;

/**
 * Exception thrown when reading properties
 */
public class PropertiesReaderException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public PropertiesReaderException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public PropertiesReaderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public PropertiesReaderException(final Throwable cause) {
        super(cause);
    }
}
