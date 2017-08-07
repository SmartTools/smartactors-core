package info.smart_tools.smartactors.testing.test_report_xml_builder;

import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.test_report_xml_builder.wrapper.TestSuiteWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TestReportXmlBuilderActorTest extends PluginsLoadingTestBase {
    private TestReportXmlBuilderActor reportBuilder;

    @Before
    public void setup() throws ResolutionException {
        reportBuilder = new TestReportXmlBuilderActor();
    }

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(IFieldNamePlugin.class);
        load(IFieldPlugin.class);
        load(PluginDSObject.class);
    }

    @Test
    public void Should_BuildXML() throws Exception {
        ArgumentCaptor<IObject> captor = ArgumentCaptor.forClass(IObject.class);
        TestSuiteWrapper suite = buildTestSuite();
        reportBuilder.build(suite);
        verify(suite).setReport(captor.capture());
        IObject reportObj = captor.getValue();
        String report = (String) reportObj.getValue(new FieldName("body"));
        System.out.println(report);
        InputStream xml = new ByteArrayInputStream(report.getBytes());
        InputStream xsd = getClass().getResourceAsStream("/surefire-test-report.xsd");
        assertTrue(validateAgainstXSD(xml, xsd));
    }

    private TestSuiteWrapper buildTestSuite() throws Exception {
        IObject wrapper = mock(IObject.class);
        final List<IObject> testCases = buildTestCases();
        when(wrapper.getValue(new FieldName("featureName"))).thenReturn("TestReportXmlBuilderActorTest");
        when(wrapper.getValue(new FieldName("timestamp"))).thenReturn(new Date().getTime());
        when(wrapper.getValue(new FieldName("testTime"))).thenReturn(10L);
        when(wrapper.getValue(new FieldName("tests"))).thenReturn(2);
        when(wrapper.getValue(new FieldName("failures"))).thenReturn(1);
        when(wrapper.getValue(new FieldName("testCases"))).thenReturn(testCases);
        TestSuiteWrapper suite = mock(TestSuiteWrapper.class);
        when(suite.getTestSuite()).thenReturn(wrapper);
        return suite;
    }

    private List<IObject> buildTestCases() throws Exception {
        List<IObject> testCases = new ArrayList<>();

        testCases.add(buildSuccessfulTestCase());
        testCases.add(buildFailedTestCase());

        return testCases;
    }

    private IObject buildSuccessfulTestCase() throws Exception {
        IObject testCase = mock(IObject.class);
        when(testCase.getValue(new FieldName("name"))).thenReturn("Should_BuildXML");
        when(testCase.getValue(new FieldName("testTime"))).thenReturn(4L);
        return testCase;
    }

    private IObject buildFailedTestCase() throws Exception {
        IObject testCase = mock(IObject.class);
        when(testCase.getValue(new FieldName("name"))).thenReturn("Should_Fail");
        when(testCase.getValue(new FieldName("testTime"))).thenReturn(6L);
        when(testCase.getValue(new FieldName("failure"))).thenReturn(new Exception("It's a failure"));
        return testCase;
    }

    private boolean validateAgainstXSD(InputStream xml, InputStream xsd) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
