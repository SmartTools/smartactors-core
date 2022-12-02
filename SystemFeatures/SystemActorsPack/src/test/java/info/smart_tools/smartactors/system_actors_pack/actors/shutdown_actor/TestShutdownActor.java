package info.smart_tools.smartactors.system_actors_pack.actors.shutdown_actor;

import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestShutdownActor extends PluginsLoadingTestBase {

    private IUpCounter upCounterMock;

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks() throws Exception {
        upCounterMock = mock(IUpCounter.class);

        IOC.register(Keys.getKeyByName("a upcounter"), new SingletonStrategy(upCounterMock));
    }

    @Test
    public void Should_performShutdownRequest()
        throws Exception {
        new ShutdownActor().shutdown(new ShutdownRequestMessage() {
            @Override
            public Object getShutdownMode() throws ReadValueException {
                return "the mode";
            }

            @Override
            public Object getUpCounterName() throws ReadValueException {
                return "a upcounter";
            }
        });

        verify(upCounterMock).shutdown(eq("the mode"));
    }

    @Test
    public void Should_performForceShutdownRequest()
        throws Exception {
        new ShutdownActor().forceShutdown(new ForceShutdownRequestMessage() {
            @Override
            public Object getUpCounterName() throws ReadValueException {
                return "a upcounter";
            }
        });

        verify(upCounterMock).forceShutdown();
    }
}
