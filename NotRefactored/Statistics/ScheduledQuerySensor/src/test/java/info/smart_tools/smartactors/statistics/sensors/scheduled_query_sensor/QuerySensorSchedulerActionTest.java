package info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.IQueryExecutor;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.exceptions.QueryExecutionException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link QuerySensorSchedulerAction}.
 */
public class QuerySensorSchedulerActionTest extends PluginsLoadingTestBase {
    private IQueryExecutor queryExecutorMock;
    private ISchedulerEntry schedulerEntryMock;
    private IMessageBusHandler messageBusHandlerMock;

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
        queryExecutorMock = mock(IQueryExecutor.class);
        IOC.register(Keys.getKeyByName("that query executor"), new SingletonStrategy(queryExecutorMock));

        schedulerEntryMock = mock(ISchedulerEntry.class);
        when(schedulerEntryMock.getState()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));

        IOC.register(Keys.getKeyByName("chain_id_from_map_name"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) String.valueOf(args[0]).concat("__0");
            }
        });

        messageBusHandlerMock = mock(IMessageBusHandler.class);
        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), messageBusHandlerMock);
    }

    @Test
    public void Should_initializeEntryState()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                    "'queryExecutor':'that query executor'," +
                    "'statisticsChain':'the_statistics_chain'" +
                    "}").replace('\'','"'));
        new QuerySensorSchedulerAction().init(schedulerEntryMock, args);

        verify(queryExecutorMock).init(same(schedulerEntryMock), same(args));
        assertEquals("that query executor", schedulerEntryMock.getState()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "queryExecutor")));
        assertEquals("the_statistics_chain", schedulerEntryMock.getState()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "statisticsChain")));
    }

    @Test
    public void Should_executeQueryAndSendData()
            throws Exception {
        Collection dataMock = mock(Collection.class);
        when(queryExecutorMock.execute(same(schedulerEntryMock))).thenReturn(dataMock).thenThrow(QueryExecutionException.class);

        IObject state = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'queryExecutor':'that query executor'," +
                        "'statisticsChain':'the_statistics_chain'" +
                        "}").replace('\'','"'));
        when(schedulerEntryMock.getState()).thenReturn(state);
        when(schedulerEntryMock.getLastTime()).thenReturn(100600L);

        new QuerySensorSchedulerAction().execute(schedulerEntryMock);

        ArgumentCaptor<IObject> messageCaptor = ArgumentCaptor.forClass(IObject.class);
        verify(messageBusHandlerMock).handle(messageCaptor.capture(), eq("the_statistics_chain"), eq(true));

        assertSame(dataMock, messageCaptor.getValue().getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "data")));
        assertEquals(100600L, messageCaptor.getValue().getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "periodStart")));
        assertEquals(100600L, messageCaptor.getValue().getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "periodEnd")));
    }
}
