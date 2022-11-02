package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.strategies;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link CountStrategy}.
 */
public class CountStrategyTest {
    @Test
    public void Should_countMessages()
            throws Exception {
        IMessageProcessor messageProcessorM = mock(IMessageProcessor.class);

        IEmbeddedSensorStrategy<AtomicLong> strategy = new CountStrategy();

        AtomicLong period = strategy.initPeriod();
        strategy.updatePeriod(period, messageProcessorM, System.currentTimeMillis());
        strategy.updatePeriod(period, messageProcessorM, System.currentTimeMillis());
        strategy.updatePeriod(period, messageProcessorM, System.currentTimeMillis());

        assertEquals(Collections.singletonList(3L), strategy.extractPeriod(period));
    }
}
