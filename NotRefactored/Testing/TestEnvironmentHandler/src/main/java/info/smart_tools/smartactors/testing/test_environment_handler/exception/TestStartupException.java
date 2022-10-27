package info.smart_tools.smartactors.testing.test_environment_handler.exception;

import info.smart_tools.smartactors.testing.interfaces.itest_runner.ITestRunner;

/**
 * Exception for error in {@link ITestRunner} method
 */
public class TestStartupException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public TestStartupException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public TestStartupException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public TestStartupException(final Throwable cause) {
        super(cause);
    }
}
