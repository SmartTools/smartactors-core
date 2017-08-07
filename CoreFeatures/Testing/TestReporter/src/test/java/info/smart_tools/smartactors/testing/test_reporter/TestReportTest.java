package info.smart_tools.smartactors.testing.test_reporter;

import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.interfaces.itest_reporter.ITestReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestReportTest extends PluginsLoadingTestBase {
    IMessageBusHandler handler = mock(IMessageBusHandler.class);

    @Before
    public void setUp() throws Exception {
        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), handler);
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
    public void Should_AddTestCases() throws Exception {
        ITestReporter reporter = new TestReporter();
        IObject testCaseInfo = buildTestCaseInfo();
        reporter.beforeTest(testCaseInfo);
        // SOME STUFF IN REAL CODE IS HERE
        reporter.afterTest(null);
        // NEXT TEST
        reporter.beforeTest(testCaseInfo);
        // SOME STUFF IN REAL CODE IS HERE
        reporter.afterTest(new Exception("It should fall"));

        IObject testInfo = buildTestInfo();

        reporter.make("TestReportTest", testInfo);
        ArgumentCaptor<IObject> message = ArgumentCaptor.forClass(IObject.class);
        ArgumentCaptor<Object> chainName = ArgumentCaptor.forClass(Object.class);
        verify(handler, times(1)).handle(message.capture(), chainName.capture());
        assertEquals("reporterChainId", chainName.getValue());
    }

    private IObject buildTestCaseInfo() {
        IObject testCaseInfo = mock(IObject.class);
        return testCaseInfo;
    }

    private IObject buildTestInfo() throws Exception {
        IObject testInfo = mock(IObject.class);
        when(testInfo.getValue(new FieldName("reporterChainName"))).thenReturn("reporterChainId");
        return testInfo;
    }
}
