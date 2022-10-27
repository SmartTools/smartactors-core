package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.exceptions.EmbeddedSensorStrategyException;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of embedded sensor strategy that records difference between current time and time saved in specified field of message
 * context for first fixed number of messages received.
 */
public class TimeDeltaForLimitedCountStrategy implements IEmbeddedSensorStrategy<TimeDeltaForLimitedCountStrategy.PeriodState> {
    /**
     * State of a single observation period. Implemented as thread-safe lock-free fixed size vector.
     */
    static final class PeriodState {
        private final AtomicBoolean locked;
        private volatile int count;
        private final Long[] data;

        /**
         * The constructor.
         *
         * @param maxCount    maximum count of measurements in period
         */
        PeriodState(final int maxCount) {
            this.locked = new AtomicBoolean(false);
            this.data = new Long[maxCount];
            this.count = 0;
        }

        private void lock() {
            boolean done;

            do {
                done = locked.compareAndSet(false, true);
            } while (!done);
        }

        private void unlock() {
            locked.set(false);
        }

        /**
         * Record a single measurement value.
         *
         * @param delta    the value
         */
        void recordDelta(final long delta) {
            if (count >= data.length) {
                return;
            }

            lock();

            try {
                int index = count;
                if (index < data.length) {
                    data[index] = delta;
                    count = index + 1;
                }
            } finally {
                unlock();
            }
        }

        /**
         * Get collection of currently recorded measurements.
         *
         * @return collection of all recorded measurements
         */
        Collection<Long> getData() {
            int currentCount;

            lock();

            try {
                currentCount = count;
            } finally {
                unlock();
            }

            return Arrays.asList((currentCount >= data.length) ? data : Arrays.copyOf(data, currentCount));
        }
    }

    private final int maxCount;
    private final IFieldName timeFieldName;

    /**
     * The constructor.
     *
     * @param maxCount         maximum count of measurements
     * @param timeFieldName    name of context field where start time is stored
     */
    public TimeDeltaForLimitedCountStrategy(final int maxCount, final IFieldName timeFieldName) {
        this.maxCount = maxCount;
        this.timeFieldName = timeFieldName;
    }

    @Override
    public PeriodState initPeriod() {
        return new PeriodState(maxCount);
    }

    @Override
    public void updatePeriod(final PeriodState period, final IMessageProcessor processor, final long time)
            throws EmbeddedSensorStrategyException {
        try {
            long delta = time - ((Long) processor.getContext().getValue(timeFieldName));
            period.recordDelta(delta);
        } catch (ReadValueException | InvalidArgumentException | ClassCastException e) {
            throw new EmbeddedSensorStrategyException(e);
        }
    }

    @Override
    public Collection<? extends Number> extractPeriod(final PeriodState period) {
        return period.getData();
    }
}
