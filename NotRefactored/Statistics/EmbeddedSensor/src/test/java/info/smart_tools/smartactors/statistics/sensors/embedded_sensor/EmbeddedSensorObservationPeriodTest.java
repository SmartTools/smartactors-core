package info.smart_tools.smartactors.statistics.sensors.embedded_sensor;

import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorObservationPeriod;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.exceptions.EmbeddedSensorStrategyException;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link EmbeddedSensorObservationPeriod}.
 */
public class EmbeddedSensorObservationPeriodTest extends PluginsLoadingTestBase {
    private IEmbeddedSensorStrategy<Object> strategyMock;
    private Object states[];
    private IMessageProcessor processors[];

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
        states = new Object[] {
                new Object(),
                new Object(),
                new Object(),
        };
        processors = new IMessageProcessor[] {
                mock(IMessageProcessor.class),
                mock(IMessageProcessor.class),
                mock(IMessageProcessor.class),
        };
        strategyMock = mock(IEmbeddedSensorStrategy.class);
        when(strategyMock.initPeriod())
                .thenReturn(states[0])
                .thenReturn(states[1])
                .thenReturn(states[2])
                .thenThrow(EmbeddedSensorStrategyException.class);
    }

    @Test
    public void Should_checkTimeCompleteness()
            throws Exception {
        IEmbeddedSensorObservationPeriod esop = new EmbeddedSensorObservationPeriod<>(100, 200, -1, strategyMock);

        assertFalse(esop.isTimeCompleted(200));
        assertTrue(esop.isTimeCompleted(201));
    }

    @Test
    public void Should_notRecordDataBeforePeriodStartIfRecordsCountIsLimited()
            throws Exception {
        IEmbeddedSensorObservationPeriod esop = new EmbeddedSensorObservationPeriod<>(100, 200, 4, strategyMock);

        assertFalse(esop.recordProcessor(processors[0], 50));

        verify(strategyMock, times(0)).updatePeriod(any(), any(), anyLong());
    }

    @Test
    public void Should_recordDataAfterPeriodStart()
            throws Exception {
        IEmbeddedSensorObservationPeriod esop = new EmbeddedSensorObservationPeriod<>(100, 200, 4, strategyMock);

        assertFalse(esop.recordProcessor(processors[0], 150));

        verify(strategyMock).updatePeriod(same(states[0]), same(processors[0]), eq(150L));
    }

    @Test
    public void Should_limitCountOfRecords()
            throws Exception {
        IEmbeddedSensorObservationPeriod esop = new EmbeddedSensorObservationPeriod<>(100, 200, 2, strategyMock);

        assertFalse(esop.recordProcessor(processors[0], 150));
        assertFalse(esop.recordProcessor(processors[1], 150));
        assertTrue(esop.recordProcessor(processors[2], 150));

        verify(strategyMock).updatePeriod(same(states[0]), same(processors[0]), eq(150L));
        verify(strategyMock).updatePeriod(same(states[0]), same(processors[1]), eq(150L));
        verify(strategyMock, times(0)).updatePeriod(same(states[0]), same(processors[2]), eq(150L));
    }

    @Test
    public void Should_createMessage()
            throws Exception {
        IEmbeddedSensorObservationPeriod esop = new EmbeddedSensorObservationPeriod<>(100, 200, 4, strategyMock);

        Collection res = mock(Collection.class);
        when(strategyMock.extractPeriod(same(states[0]))).thenReturn(res);

        IObject msg = esop.createMessage();

        assertEquals(100L, ((Long) msg.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "periodStart"))).longValue());
        assertEquals(200L, ((Long) msg.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "periodEnd"))).longValue());
        assertSame(res, msg.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "data")));
    }

    @Test
    public void Should_createNextPeriods()
            throws Exception {
        IEmbeddedSensorObservationPeriod esop = new EmbeddedSensorObservationPeriod<>(100, 200, 4, strategyMock);

        // 201 -> 300
        IEmbeddedSensorObservationPeriod esop1 = esop.nextPeriod(250);
        assertFalse(esop1.isTimeCompleted(300));
        assertTrue(esop1.isTimeCompleted(301));

        // 501 -> 600
        IEmbeddedSensorObservationPeriod esop2 = esop.nextPeriod(503);
        assertFalse(esop2.isTimeCompleted(600));
        assertTrue(esop2.isTimeCompleted(601));
    }
}
