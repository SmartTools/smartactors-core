package info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.statistics.sensors.interfaces.ISensorHandle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link QuerySensorCreationStrategy}.
 */
public class QuerySensorCreationStrategyTest extends PluginsLoadingTestBase {
    private Object entryStorage = new Object();
    private ISchedulerEntry entryMock;
    private IStrategy newEntryStrategyMock;

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
        IOC.register(Keys.getKeyByName("query sensors scheduler storage"), new SingletonStrategy(entryStorage));

        newEntryStrategyMock = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("new scheduler entry"), newEntryStrategyMock);
    }

    @Test
    public void Should_createSchedulerEntryAndSensorHandle()
            throws Exception {
        IObject conf = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

        when(newEntryStrategyMock.resolve(same(conf), same(entryStorage))).thenAnswer(invocation -> {
            assertEquals("stat_chain",
                    conf.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "statisticsChain")));
            assertEquals("query sensor scheduler action",
                    conf.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "action")));
            return entryMock;
        });

        ISensorHandle handle = new QuerySensorCreationStrategy().resolve(
                "stat_chain",
                conf
        );

        assertNotNull(handle);
    }
}
