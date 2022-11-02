package info.smart_tools.smartactors.statistics.sensors.embedded_sensor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorObservationPeriod;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.exceptions.EmbeddedSensorStrategyException;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default implementation of {@link
 * IEmbeddedSensorObservationPeriod}.
 *
 * @param <TState> type of strategy's period data
 */
public class EmbeddedSensorObservationPeriod<TState> implements IEmbeddedSensorObservationPeriod {
    private final long periodStart;
    private final long periodEnd;
    private final long maxPeriodItems;
    private final AtomicLong curItems;
    private final TState state;
    private final IEmbeddedSensorStrategy<TState> strategy;

    private final IFieldName periodStartFieldName;
    private final IFieldName periodEndFieldName;
    private final IFieldName dataFieldName;

    /**
     * The constructor.
     *
     * @param start       period start time
     * @param end         period end time
     * @param nItems      maximum period measurements
     * @param strategy    sensor strategy to use
     * @throws EmbeddedSensorStrategyException if error occurs creating strategy-specific state
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public EmbeddedSensorObservationPeriod(final long start, final long end, final long nItems, final IEmbeddedSensorStrategy<TState> strategy)
            throws EmbeddedSensorStrategyException, ResolutionException {
        this.periodStart = start;
        this.periodEnd = end;
        this.maxPeriodItems = nItems;
        this.curItems = new AtomicLong(0);
        this.state = strategy.initPeriod();
        this.strategy = strategy;

        periodStartFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "periodStart");
        periodEndFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "periodEnd");
        dataFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "data");
    }

    @Override
    public EmbeddedSensorObservationPeriod<TState> nextPeriod(final long inclTime)
            throws EmbeddedSensorStrategyException, ResolutionException {
        long duration = periodEnd - periodStart;
        // Skip some periods where we had no messages
        long skip = (inclTime - periodEnd) / duration;
        skip = (skip > 0) ? skip : 0;
        return new EmbeddedSensorObservationPeriod<>(periodEnd + skip * duration, periodEnd + (skip + 1) * duration, maxPeriodItems, strategy);
    }

    private boolean isCountLimited() {
        return maxPeriodItems > 0;
    }

    private boolean isStartedAt(final long time) {
        return periodStart <= time;
    }

    @Override
    public boolean isTimeCompleted(final long time) {
        return time > periodEnd;
    }

    private Collection<? extends Number> extractData()
            throws EmbeddedSensorStrategyException, InvalidArgumentException {
        return strategy.extractPeriod(state);
    }

    /**
     * @return {@code true} if items limit exceeded
     */
    private boolean incrementCount() {
        return maxPeriodItems > 0 && curItems.incrementAndGet() > maxPeriodItems;
    }

    @Override
    public boolean recordProcessor(final IMessageProcessor mp, final long time)
            throws EmbeddedSensorStrategyException, InvalidArgumentException {
        if (isCountLimited() && !isStartedAt(time)) {
            return false;
        }

        if (!incrementCount()) {
            strategy.updatePeriod(state, mp, time);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public IObject createMessage()
            throws ResolutionException, ChangeValueException, InvalidArgumentException, EmbeddedSensorStrategyException {
        IObject message = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        message.setValue(periodStartFieldName, periodStart);
        message.setValue(periodEndFieldName, periodEnd);
        message.setValue(dataFieldName, extractData());
        return message;
    }
}
