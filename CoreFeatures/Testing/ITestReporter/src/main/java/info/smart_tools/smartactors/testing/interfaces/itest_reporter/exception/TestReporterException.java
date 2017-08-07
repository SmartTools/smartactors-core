package info.smart_tools.smartactors.testing.interfaces.itest_reporter.exception;

public class TestReporterException extends Exception {
    public TestReporterException() {
    }

    public TestReporterException(final String message) {
        super(message);
    }

    public TestReporterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TestReporterException(final Throwable cause) {
        super(cause);
    }
}
