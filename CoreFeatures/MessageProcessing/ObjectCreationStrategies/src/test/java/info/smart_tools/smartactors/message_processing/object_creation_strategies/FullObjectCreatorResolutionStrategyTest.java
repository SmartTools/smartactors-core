package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link FullObjectCreatorResolutionStrategy}.
 */
public class FullObjectCreatorResolutionStrategyTest extends PluginsLoadingTestBase {
    private IResolveDependencyStrategy creator1ResolutionStrategyMock;
    private IResolveDependencyStrategy creator2ResolutionStrategyMock;
    private IReceiverObjectCreator creator1Mock;
    private IReceiverObjectCreator creator2Mock;

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
        creator1ResolutionStrategyMock = mock(IResolveDependencyStrategy.class);
        creator2ResolutionStrategyMock = mock(IResolveDependencyStrategy.class);
        creator1Mock = mock(IReceiverObjectCreator.class);
        creator2Mock = mock(IReceiverObjectCreator.class);

        when(creator1ResolutionStrategyMock.resolve(isNull(), any(), any())).thenReturn(creator1Mock);
        when(creator2ResolutionStrategyMock.resolve(same(creator1Mock), any(), any())).thenReturn(creator2Mock);

        IOC.register(Keys.getOrAdd("filter 1 dependency"), creator1ResolutionStrategyMock);
        IOC.register(Keys.getOrAdd("filter 2 dependency"), creator2ResolutionStrategyMock);
    }

    @Test
    public void Should_resolveCreatorsPipeline()
            throws Exception {
        IObject objectConfig = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                ("{" +
                 "  'filters':[" +
                 "      {" +
                        "   'dependency':'filter 1 dependency'" +
                 "      }," +
                        "{" +
                        "   'dependency':'filter 2 dependency'" +
                        "}" +
                 "  ]" +
                 "}").replace('\'','"'));

        IReceiverObjectCreator result = new FullObjectCreatorResolutionStrategy().resolve(objectConfig);

        assertSame(creator2Mock, result);
    }
}
