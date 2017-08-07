package info.smart_tools.smartactors.testing.chain_testing.section_strategy;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.ITestRunner;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.exception.TestExecutionException;
import info.smart_tools.smartactors.testing_plugins.test_reporter_plugin.PluginTestReporter;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for {@link TestsSectionStrategy}.
 */
public class TestsSectionStrategyTest extends PluginsLoadingTestBase {
    private ITestRunner testRunnerMock;
    private IMessageBusHandler handler;

    @Before
    public void setUp() throws Exception {
        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), handler);
    }

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
        load(IFieldPlugin.class);
        load(PluginTestReporter.class);
    }

    @Override
    protected void registerMocks()
            throws Exception {
        testRunnerMock = mock(ITestRunner.class);
        handler = mock(IMessageBusHandler.class);

        IOC.register(Keys.getOrAdd(ITestRunner.class.getCanonicalName() + "#assert"),
                new SingletonStrategy(testRunnerMock));
//        IResolveDependencyStrategy strategy = mock(IResolveDependencyStrategy.class);
//        IOC.register(
//                IOC.resolve(IOC.getKeyForKeyStorage(), ITestRunner.class.getCanonicalName() + "#" + "assert"),
//                strategy
//        );
//        when(strategy.resolve()).thenReturn(this.testRunnerMock);
    }

    @Test
    public void Should_storeFieldName()
            throws Exception {
        TestsSectionStrategy strategy = new TestsSectionStrategy();

        assertEquals(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "test"), strategy.getSectionName());
    }

    @Test
    public void Should_runTestWhenItPasses()
            throws Exception {
        IObject config = mock(IObject.class);
        IObject testSection = mock(IObject.class);
        IObject td1 = mock(IObject.class);
        when(td1.getValue(new FieldName("name"))).thenReturn("Should_runTestWhenItPasses");
        when(td1.getValue(new FieldName("entryPoint"))).thenReturn("assert");
        when(testSection.getValue(new FieldName("tests"))).thenReturn(Collections.singletonList(td1));
        when(testSection.getValue(new FieldName("reporterChainName"))).thenReturn("someChainId");
        when(config.getValue(new FieldName("test"))).thenReturn(testSection);
        when(config.getValue(new FieldName("featureName"))).thenReturn("TestSectionStrategyTest");

        doAnswer(invocation -> {
            new Thread(() -> {
                try {
                    invocation.getArgumentAt(1, IAction.class).execute(null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
            return null;
        }).when(testRunnerMock).runTest(any(), any());

        new TestsSectionStrategy().onLoadConfig(config);

        verify(testRunnerMock, times(1)).runTest(any(), any());
    }

    @Test
    public void Should_runTestsWhenOneFails()
            throws Exception {
        IObject td1 = mock(IObject.class);
        IObject td2 = mock(IObject.class);
        IObject testSection = mock(IObject.class);
        IObject config = mock(IObject.class);
        when(td1.getValue(new FieldName("entryPoint"))).thenReturn("assert");
        when(td2.getValue(new FieldName("entryPoint"))).thenReturn("assert");
        when(td2.getValue(new FieldName("name"))).thenReturn("td2");
        when(td1.getValue(new FieldName("name"))).thenReturn("td1");
        when(testSection.getValue(new FieldName("tests"))).thenReturn(Arrays.asList(td1, td2));
        when(config.getValue(new FieldName("test"))).thenReturn(testSection);
        when(config.getValue(new FieldName("featureName"))).thenReturn("TestsSectionStrategy");

        doAnswer(invocation -> {
            new Thread(() -> {
                try {
                    invocation.getArgumentAt(1, IAction.class).execute(null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
            return null;
        }).
                doAnswer(invocation -> {
                    new Thread(() -> {
                        try {
                            invocation.getArgumentAt(1, IAction.class).execute(new Exception());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                    return null;
                }).when(testRunnerMock).runTest(any(), any());

        try {
            new TestsSectionStrategy().onLoadConfig(config);
            fail();
        } catch (ConfigurationProcessingException e) {
            assertSame(Exception.class, e.getCause().getClass());
        }

        verify(testRunnerMock, times(2)).runTest(any(), any());
    }

    @Test(expected = ConfigurationProcessingException.class)
    public void Should_wrapExceptionWhenFailsToStartTest()
            throws Exception {
        IObject td1 = mock(IObject.class);
        IObject td2 = mock(IObject.class);
        IObject config = mock(IObject.class);
        IObject testSection = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        testSection.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "tests"),
                Arrays.asList(td1, td2));

        doThrow(TestExecutionException.class).when(testRunnerMock).runTest(same(td1), any());
        when(config.getValue(new FieldName("test"))).thenReturn(testSection);

        new TestsSectionStrategy().onLoadConfig(config);
    }
}
