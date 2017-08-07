package info.smart_tools.smartactors.testing.test_reporter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.testing.interfaces.itest_reporter.ITestReporter;
import info.smart_tools.smartactors.testing.interfaces.itest_reporter.exception.TestReporterException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This implementation build simple test suites and send them into MessageBus to a chain defined in a `test` section in a config.json file.
 */
public class TestReporter implements ITestReporter {
    private Long startTime;
    private final List<IObject> testCases;

    private final IFieldName timestampField;
    private final IFieldName testTimeField;
    private final IFieldName featureNameField;
    private final IFieldName testCasesField;
    private final IFieldName failureField;
    private final IFieldName failuresCountField;
    private final IFieldName testsCountField;
    private final IField chainNameField;
    private final IFieldName testSuiteFieldName;


    public TestReporter() throws ResolutionException {
        this.testTimeField = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "testTime");
        this.featureNameField = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "featureName");
        this.testCasesField = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "testCases");
        this.failureField = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "failure");
        this.failuresCountField = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "failures");
        this.testsCountField = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "tests");
        this.timestampField = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "timestamp");
        this.chainNameField = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "reporterChainName");
        this.testSuiteFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "testSuite");
        this.testCases = new ArrayList<>();
    }

    @Override
    public void beforeTest(final IObject testCaseInfo) {
        startTime = System.currentTimeMillis();
        testCases.add(testCaseInfo);
    }

    @Override
    public void afterTest(final Throwable throwable) throws TestReporterException {
        try {
            long delay = System.currentTimeMillis() - startTime;
            final IObject currentTestCase = testCases.get(testCases.size() - 1);
            currentTestCase.setValue(testTimeField, delay);
            currentTestCase.setValue(failureField, throwable);
        } catch (Exception e) {
            throw new TestReporterException(e.getMessage(), e);
        }
    }

    @Override
    public void make(final String featureName, final IObject testSuiteInfo) throws TestReporterException {
        Object chainName;
        try {
            chainName = chainNameField.in(testSuiteInfo);
            if (chainName == null) {
                // It's normal behaviour. User didn't specify the chain for receiving test reports. Do nothing.
                // TODO: write warning???
                System.out.println(String.format("reporterChainName is not defined for feature %s", featureName));
                return;
            }
        } catch (ReadValueException | InvalidArgumentException e) {
            // It's normal behaviour. User didn't specify the chain for receiving test reports. Do nothing.
            // TODO: write warning???
            System.out.println(String.format("reporterChainName is not defined for feature %s", featureName));
            return;
        }

        try {
            final IObject message = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()));
            final IObject suite = buildSuite(featureName);
            message.setValue(testSuiteFieldName, suite);
            MessageBus.send(message, chainName);
        } catch (ReadValueException | InvalidArgumentException | ChangeValueException | ResolutionException e) {
            throw new TestReporterException("Can't build report: " + e.getMessage(), e);
        } catch (SendingMessageException e) {
            throw new TestReporterException("Can't send report to the chain " + chainName.toString() + ": " + e.getMessage(), e);
        }
    }

    private IObject buildSuite(final String featureName) throws ReadValueException, InvalidArgumentException, ChangeValueException, ResolutionException {
        int testsCount = testCases.size();
        int failuresCount = 0;
        long totalTime = 0L;
        for (IObject testCase : testCases) {
            final Object failure = testCase.getValue(failureField);
            if (failure != null) failuresCount++;
            final Long time = (Long)testCase.getValue(testTimeField);
            if (time != null) totalTime += time;
        }
        final IObject testSuite = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()));
        testSuite.setValue(featureNameField, featureName);
        testSuite.setValue(testCasesField, testCases);
        testSuite.setValue(failuresCountField, failuresCount);
        testSuite.setValue(testsCountField, testsCount);
        testSuite.setValue(timestampField, new Date().getTime());
        testSuite.setValue(testTimeField, totalTime);
        return testSuite;
    }
}
