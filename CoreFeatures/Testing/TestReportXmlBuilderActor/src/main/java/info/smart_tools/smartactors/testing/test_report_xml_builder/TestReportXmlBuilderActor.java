package info.smart_tools.smartactors.testing.test_report_xml_builder;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.testing.test_report_xml_builder.exception.BuildXmlReportActorException;
import info.smart_tools.smartactors.testing.test_report_xml_builder.wrapper.TestSuiteWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * MVN surefire plugin compatible XML report {@see https://github.com/apache/maven-surefire/blob/master/maven-surefire-plugin/src/site/resources/xsd/surefire-test-report.xsd}
 */
public final class TestReportXmlBuilderActor {
    private final IField featureNameField;
    private final IField testsField;
    private final IField failuresField;
    private final IField timeField;

    private final IField testCasesField;
    private final IField nameField;
    private final IField failureField;
    private final IField bodyField;
    private final IField timestampField;

    public TestReportXmlBuilderActor() throws ResolutionException {
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
        timestampField = IOC.resolve(IOC.resolve(
                IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "timestamp");
    }

    public void build(final TestSuiteWrapper suiteWrapper) throws BuildXmlReportActorException {
        try {
            final TestSuiteModel suiteModel = convertSuite(suiteWrapper.getTestSuite());
            final String featureName = featureNameField.in(suiteWrapper.getTestSuite());

            final StringWriter writer = new StringWriter();
            final JAXBContext jc = JAXBContext.newInstance(TestSuiteModel.class);
            final Marshaller marshaller = jc.createMarshaller();
            marshaller.marshal(suiteModel, writer);

            IObject report = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()));
            bodyField.out(report, writer.toString());
            nameField.out(report, featureName);
            suiteWrapper.setReport(report);
        } catch (ReadValueException e) {
            throw new BuildXmlReportActorException("Can't read values from wrapper: " + e.getMessage(), e);
        } catch (JAXBException e) {
            throw new BuildXmlReportActorException("Can't build xml: " + e.getMessage(), e);
        } catch (ChangeValueException | InvalidArgumentException e) {
            throw new BuildXmlReportActorException("Can't write report into wrapper: " + e.getMessage(), e);
        } catch (ResolutionException e) {
            throw new BuildXmlReportActorException("Can't resolve dependency: " + e.getMessage(), e);
        }
    }

    private TestSuiteModel convertSuite(IObject suiteWrapper) throws ReadValueException, InvalidArgumentException {
        final TestSuiteModel suiteModel = new TestSuiteModel();

        suiteModel.setName(featureNameField.in(suiteWrapper));
        suiteModel.setTests(testsField.in(suiteWrapper));
        suiteModel.setFailures(failuresField.in(suiteWrapper));
        suiteModel.setErrors(0);
        suiteModel.setSkipped(0);
        suiteModel.setTimestamp(timestampField.in(suiteWrapper));
        suiteModel.setTime(timeField.in(suiteWrapper));
        suiteModel.setTestCaseModels(convertCases(testCasesField.in(suiteWrapper)));

        return suiteModel;
    }

    private List<TestCaseModel> convertCases(List<IObject> casesWrappers) throws ReadValueException, InvalidArgumentException {
        final List<TestCaseModel> models = new ArrayList<>();

        for (final IObject caseWrapper : casesWrappers) {
            final TestCaseModel caseModel = new TestCaseModel();
            caseModel.setTime(timeField.in(caseWrapper));
            caseModel.setName(nameField.in(caseWrapper));
            caseModel.setFailure(convertFailure(failureField.in(caseWrapper)));

            models.add(caseModel);
        }

        return models;
    }

    private TestFailureModel convertFailure(final Throwable throwable) {
        if (throwable == null) return null;

        final TestFailureModel failureModel = new TestFailureModel();
        failureModel.setType(throwable.getClass().getCanonicalName());
        failureModel.setMessage(throwable.getMessage());
        failureModel.setBody(getStackTrace(throwable));
        return failureModel;
    }

    private String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        throwable.printStackTrace(writer);
        return sw.toString();
    }
}
