package info.smart_tools.smartactors.core.chain_testing.section_strategy;

import info.smart_tools.smartactors.core.chain_testing.TestRunner;
import info.smart_tools.smartactors.core.chain_testing.exceptions.TestStartupException;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.plugin.dsobject.PluginDSObject;
import info.smart_tools.smartactors.plugin.ifieldname.IFieldNamePlugin;
import info.smart_tools.smartactors.plugin.ioc_keys.PluginIOCKeys;
import info.smart_tools.smartactors.plugin.scope_provider.PluginScopeProvider;
import info.smart_tools.smartactors.plugin.scoped_ioc.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for {@link TestsSectionStrategy}.
 */
public class TestsSectionStrategyTest extends PluginsLoadingTestBase {
    private TestRunner testRunnerMock;
    private ArgumentCaptor cbCaptor;

    @Override
    protected void loadPlugins()
            throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks()
            throws Exception {
        testRunnerMock = mock(TestRunner.class);

        IOC.register(Keys.getOrAdd(TestRunner.class.getCanonicalName()),
                new SingletonStrategy(testRunnerMock));

        cbCaptor = ArgumentCaptor.forClass(IAction.class);
    }

    @Test
    public void Should_storeFieldName()
            throws Exception {
        TestsSectionStrategy strategy = new TestsSectionStrategy();

        assertEquals(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "tests"), strategy.getSectionName());
    }

    @Test
    public void Should_runTestWhenItPasses()
            throws Exception {
        IObject td1 = mock(IObject.class);
        IObject config = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        config.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "tests"),
                Collections.singletonList(td1));

        doAnswer(invocation -> {
            new Thread() {
                @Override
                public void run() {
                    try {
                        invocation.getArgumentAt(1, IAction.class).execute(null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }.start();
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
        IObject config = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        config.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "tests"),
                Arrays.asList(td1, td2));

        doAnswer(invocation -> {
            new Thread() {
                @Override
                public void run() {
                    try {
                        invocation.getArgumentAt(1, IAction.class).execute(null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }.start();
            return null;
        }).
        doAnswer(invocation -> {
            new Thread() {
                @Override
                public void run() {
                    try {
                        invocation.getArgumentAt(1, IAction.class).execute(new Exception());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }.start();
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
        IObject config = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        config.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "tests"),
                Arrays.asList(td1, td2));

        doThrow(TestStartupException.class).when(testRunnerMock).runTest(same(td1), any());

        new TestsSectionStrategy().onLoadConfig(config);
    }
}
