package info.smart_tools.smartactors.database.interfaces.idatabase.exception;

import info.smart_tools.smartactors.database.interfaces.idatabase.IDatabase;

/**
 * Exception class for {@link IDatabase}
 */
public class IDatabaseException extends Exception {

    /**
     * Constructor with specific error message
     * @param message specific error message
     */
    public IDatabaseException(final String message) {
        super(message);
    }


    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public IDatabaseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     * @param cause specific cause
     */
    public IDatabaseException(final Throwable cause) {
        super(cause);
    }

}
