package info.smart_tools.smartactors.testing.test_report_text_builder;

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
import info.smart_tools.smartactors.testing.test_report_text_builder.wrapper.TestSuiteWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class TestReportTextBuilderTestActor extends PluginsLoadingTestBase {
    private TestReportTextBuilderActor reportBuilder;

    @Before
    public void setup() throws ResolutionException {
        reportBuilder = new TestReportTextBuilderActor();
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
    public void Should_BuildText() throws Exception {
        final TestSuiteWrapper mock = buildTestSuite();
        reportBuilder.build(mock);
        ArgumentCaptor<IObject> captor = ArgumentCaptor.forClass(IObject.class);
        verify(mock).setReport(captor.capture());
        IObject report = captor.getValue();
        String reportName = (String) report.getValue(new FieldName("name"));
        System.out.println(reportName);
        assertNotNull(reportName);
    }

    private TestSuiteWrapper buildTestSuite() throws Exception {
        IObject wrapper = mock(IObject.class);
        final List<IObject> testCases = buildTestCases();
        when(wrapper.getValue(new FieldName("featureName"))).thenReturn("TestReportTextBuilderTestActor");
        when(wrapper.getValue(new FieldName("timestamp"))).thenReturn(new Date().getTime());
        when(wrapper.getValue(new FieldName("time"))).thenReturn(10L);
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
}
