package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.strategies;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Strategy for embedded sensor that counts number of messages.
 */
public class CountStrategy implements IEmbeddedSensorStrategy<AtomicLong> {
    @Override
    public AtomicLong initPeriod() {
        return new AtomicLong(0);
    }

    @Override
    public void updatePeriod(final AtomicLong period, final IMessageProcessor processor, final long time) {
        period.incrementAndGet();
    }

    @Override
    public Collection<? extends Number> extractPeriod(final AtomicLong period) {
        return Collections.singletonList(period.longValue());
    }
}
