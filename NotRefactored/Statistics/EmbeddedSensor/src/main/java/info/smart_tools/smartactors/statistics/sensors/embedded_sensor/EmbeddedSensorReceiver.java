package info.smart_tools.smartactors.statistics.sensors.embedded_sensor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorObservationPeriod;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.exceptions.EmbeddedSensorStrategyException;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITime;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class EmbeddedSensorReceiver implements IMessageReceiver {
    private static final long DEFAULT_COMMIT_DELAY = 1000;

    private long observationStart;
    private long observationPeriod;
    private long maxPeriodItems;
    private IEmbeddedSensorStrategy<?> strategy;
    private final AtomicReference<IEmbeddedSensorObservationPeriod> currentPeriod = new AtomicReference<>();
    private String statisticsChainName;

    private final ITimer timer;
    private final ITime systemTime;
    private long commitDelay = DEFAULT_COMMIT_DELAY;

    private final IFieldName periodFieldName;
    private final IFieldName startFieldName;
    private final IFieldName limitFieldName;
    private final IFieldName strategyFieldName;
    private final IFieldName statisticsChainFieldName;

    /**
     * The constructor.
     *
     * @param args    description of the sensor to create
     * @throws ResolutionException if error occurs resolving any dependencies
     * @throws ReadValueException if error occurs reading arguments object
     * @throws InvalidArgumentException if something unexpected occurs suddenly
     * @throws EmbeddedSensorStrategyException if error occurs in sensor strategy while initializing first observation period
     */
    public EmbeddedSensorReceiver(final IObject args)
            throws ResolutionException, ReadValueException, InvalidArgumentException, EmbeddedSensorStrategyException {
        periodFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "period");
        startFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "start");
        limitFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "limit");
        strategyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "strategy");
        statisticsChainFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "statisticsChain");

        timer = IOC.resolve(Keys.getKeyByName("timer"));
        systemTime = IOC.resolve(Keys.getKeyByName("time"));

        observationPeriod = Duration.parse((String) args.getValue(periodFieldName)).toMillis();

        String start = (String) args.getValue(startFieldName);
        observationStart = (start == null)
                ? systemTime.currentTimeMillis()
                : LocalDateTime.parse(start).atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

        Number maxItems = (Number) args.getValue(limitFieldName);
        maxPeriodItems = (maxItems == null) ? -1 : maxItems.longValue();

        strategy = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), args.getValue(strategyFieldName)), args);
        statisticsChainName = (String)args.getValue(statisticsChainFieldName);

        currentPeriod.set(IOC.resolve(Keys.getKeyByName(IEmbeddedSensorObservationPeriod.class.getCanonicalName()),
                observationStart, observationStart + observationPeriod, maxPeriodItems, strategy));
    }

    private void commitPeriod(final IEmbeddedSensorObservationPeriod period, final IEmbeddedSensorObservationPeriod nextPeriod)
            throws TaskScheduleException {
        if (currentPeriod.compareAndSet(period, nextPeriod)) {
            // Message is created and sent with some delay to let all threads write data to the old period
            timer.schedule(() -> {
                try {
                    MessageBus.send(period.createMessage(), statisticsChainName);
                } catch (SendingMessageException | ResolutionException | InvalidArgumentException | ChangeValueException
                        | EmbeddedSensorStrategyException e) {
                    throw new TaskExecutionException(e);
                }
            }, systemTime.currentTimeMillis() + commitDelay);
        }
    }

    private IEmbeddedSensorObservationPeriod getCurrentPeriod(final long time)
            throws TaskScheduleException, EmbeddedSensorStrategyException, ResolutionException {
        while (true) {
            IEmbeddedSensorObservationPeriod period = currentPeriod.get();

            if (period.isTimeCompleted(time)) {
                commitPeriod(period, period.nextPeriod(time));
                continue;
            }

            return period;
        }
    }

    @Override
    public void receive(final IMessageProcessor processor)
            throws MessageReceiveException, AsynchronousOperationException {
        try {
            final long cTime = systemTime.currentTimeMillis();
            final IEmbeddedSensorObservationPeriod period = getCurrentPeriod(cTime);
            boolean commit = period.recordProcessor(processor, cTime);

            if (commit) {
                commitPeriod(period, period.nextPeriod(cTime));
            }
        } catch (TaskScheduleException | EmbeddedSensorStrategyException | InvalidArgumentException | ResolutionException e) {
            throw new MessageReceiveException(e);
        }
    }

    @Override
    public void dispose() {
    }
}
