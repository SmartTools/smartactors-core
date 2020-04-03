package info.smart_tools.smartactors.testing.interfaces.itest_runner.exception;

import info.smart_tools.smartactors.testing.interfaces.itest_runner.ITestRunner;

/**
 * Exception for error in {@link ITestRunner} method
 */
public class TestExecutionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public TestExecutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public TestExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public TestExecutionException(final Throwable cause) {
        super(cause);
    }
}
