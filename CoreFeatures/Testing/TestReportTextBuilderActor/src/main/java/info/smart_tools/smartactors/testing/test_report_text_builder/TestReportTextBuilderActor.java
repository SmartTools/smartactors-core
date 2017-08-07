package info.smart_tools.smartactors.testing.test_report_text_builder;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.testing.test_report_text_builder.exception.BuildTextReportActorException;
import info.smart_tools.smartactors.testing.test_report_text_builder.wrapper.TestSuiteWrapper;

import java.util.List;

public class TestReportTextBuilderActor {
    private final IField featureNameField;
    private final IField testsField;
    private final IField failuresField;
    private final IField timeField;
    private final IField testCasesField;
    private final IField nameField;
    private final IField failureField;
    private final IField bodyField;

    public TestReportTextBuilderActor() throws ResolutionException {
        featureNameField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "featureName");
        testsField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "tests");
        failuresField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "failures");
        timeField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "testTime");
        testCasesField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "testCases");
        nameField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "name");
        failureField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "failure");
        bodyField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "body");
    }

    public void build(final TestSuiteWrapper suiteWrapper) throws BuildTextReportActorException {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            final String featureName = featureNameField.in(suiteWrapper.getTestSuite());
            stringBuilder.append(String.format(
                    "Test set: %s\nTest run: %s, Failures: %s, Time elapsed: %s sec\n",
                    featureName,
                    testsField.in(suiteWrapper.getTestSuite()),
                    failuresField.in(suiteWrapper.getTestSuite()),
                    timeField.in(suiteWrapper.getTestSuite())
            ));

            final List<IObject> testCases = testCasesField.in(suiteWrapper.getTestSuite());
            if (testCases != null) {
                for (IObject tc : testCases) {
                    if (failureField.in(tc) != null) {
                        stringBuilder.append(String.format(
                                "\tFAIL!! TestCase %s. Time elapsed: %s sec. Throwable: %s.\n",
                                nameField.in(tc),
                                timeField.in(tc),
                                failureField.<Throwable>in(tc).getMessage()
                        ));
                    } else {
                        stringBuilder.append(String.format(
                                "\tSUCCESS!! TestCase %s. Time elapsed: %s sec.\n",
                                nameField.in(tc),
                                timeField.in(tc)
                        ));
                    }
                }
            }

            IObject report = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()));
            bodyField.out(report, stringBuilder.toString());
            nameField.out(report, featureName);
            suiteWrapper.setReport(report);
        } catch (ReadValueException e) {
            throw new BuildTextReportActorException("Can't read values from wrapper: " + e.getMessage(), e);
        } catch (ChangeValueException e) {
            throw new BuildTextReportActorException("Can't write report into wrapper: " + e.getMessage(), e);
        } catch (InvalidArgumentException e) {
            throw new BuildTextReportActorException(e.getMessage(), e);
        } catch (ResolutionException e) {
            e.printStackTrace();
        }
    }
}
