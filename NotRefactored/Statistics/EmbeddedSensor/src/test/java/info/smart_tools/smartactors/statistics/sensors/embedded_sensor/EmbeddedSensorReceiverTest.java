package info.smart_tools.smartactors.statistics.sensors.embedded_sensor;

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
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorObservationPeriod;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITime;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link EmbeddedSensorReceiver}.
 */
public class EmbeddedSensorReceiverTest extends PluginsLoadingTestBase {
    private ITime timeMock;
    private ITimer timerMock;
    private IEmbeddedSensorStrategy<?> sensorStrategyMock;
    private IStrategy periodStrategyMock;
    private IEmbeddedSensorObservationPeriod periods[];
    private IMessageProcessor processors[];
    private IMessageBusHandler messageBusHandlerMock;
    private ArgumentCaptor<ITask> taskCaptor;

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
        timeMock = mock(ITime.class);
        timerMock = mock(ITimer.class);

        IOC.register(Keys.getKeyByName("time"), new SingletonStrategy(timeMock));
        IOC.register(Keys.getKeyByName("timer"), new SingletonStrategy(timerMock));

        sensorStrategyMock = mock(IEmbeddedSensorStrategy.class);
        IOC.register(Keys.getKeyByName("the sensor strategy"), new SingletonStrategy(sensorStrategyMock));

        periods = new IEmbeddedSensorObservationPeriod[] {
                mock(IEmbeddedSensorObservationPeriod.class),
                mock(IEmbeddedSensorObservationPeriod.class),
        };

        periodStrategyMock = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName(IEmbeddedSensorObservationPeriod.class.getCanonicalName()), periodStrategyMock);
        when(periodStrategyMock.resolve(any(), any(), any(), any())).thenReturn(periods[0]);

        processors = new IMessageProcessor[] {
            mock(IMessageProcessor.class),
            mock(IMessageProcessor.class),
        };

        messageBusHandlerMock = mock(IMessageBusHandler.class);
        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), messageBusHandlerMock);

        IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) String.valueOf(args[0]).concat("__0");
            }
        });

        taskCaptor = ArgumentCaptor.forClass(ITask.class);
    }

    @Test
    public void Should_work()
            throws Exception {
        when(timeMock.currentTimeMillis()).thenReturn(1000L);

        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'period':'PT1M'," +
                        "'strategy':'the sensor strategy'," +
                        "'statisticsChain':'theStatisticsChain'," +
                        "'limit':100" +
                        "}").replace('\'','"'));

        EmbeddedSensorReceiver r = new EmbeddedSensorReceiver(args);

        verify(periodStrategyMock).resolve(eq(1000L), eq(61000L), eq(100L), same(sensorStrategyMock));

        when(timeMock.currentTimeMillis()).thenReturn(2000L);

        r.receive(processors[0]);

        verify(periods[0]).recordProcessor(same(processors[0]), eq(2000L));
        verify(periods[0]).isTimeCompleted(2000L);
        verifyNoMoreInteractions(periods[0]);
        reset(periods[0]);

        when(timeMock.currentTimeMillis()).thenReturn(62000L);
        when(periods[0].isTimeCompleted(62000L)).thenReturn(true);
        when(periods[0].nextPeriod(62000L)).thenReturn(periods[1]);

        r.receive(processors[1]);

        verify(periods[1]).recordProcessor(processors[1], 62000L);
        verify(timerMock).schedule(taskCaptor.capture(), eq(62000L + 1000L));
        verify(messageBusHandlerMock, times(0)).handle(any(), any(), eq(true));
        when(periods[0].createMessage()).thenReturn(mock(IObject.class));

        taskCaptor.getValue().execute();

        verify(messageBusHandlerMock).handle(same(periods[0].createMessage()), eq("theStatisticsChain"), eq(true));

        r.dispose();
    }
}
