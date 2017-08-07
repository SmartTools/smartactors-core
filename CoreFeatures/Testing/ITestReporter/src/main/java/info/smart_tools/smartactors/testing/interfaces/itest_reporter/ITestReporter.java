package info.smart_tools.smartactors.testing.interfaces.itest_reporter;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.testing.interfaces.itest_reporter.exception.TestReporterException;

/**
 * Interface to fill test metrics and to report.
 */
public interface ITestReporter {
    /**
     * Before action. This method could start gathering metrics from the system and parse the testInfo.
     *
     * @param testCaseInfo contains all data about test case. This object is a "test/testCases" section in a configuration.json.
     */
    void beforeTest(final IObject testCaseInfo) throws TestReporterException;

    /**
     * Mark that test case completed, may be exceptionally or not.
     *
     * @param throwable is exception that test may throw.
     */
    void afterTest(final Throwable throwable) throws TestReporterException;

    /**
     * This method build a report. What exactly this method do is hidden in a implementation.
     * Implementation supposed to build a string representation of the report in a format of xml or json or plain text.
     * Then it prints result in some output - System.our, Database, file, etc...
     *
     * @param featureName is a name of a feature to test. This object is located in a "featureName" section of a config.json.
     * @param testSuiteInfo contains all data about test. This object is located in a "test" section of a config.json.
     * @throws TestReporterException may be thrown on some error during building and sending report.
     */
    void make(final String featureName, final IObject testSuiteInfo) throws TestReporterException;
}
