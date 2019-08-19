package info.smart_tools.smartactors.scheduler.strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulingStrategyExecutionException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;


/**
 * {@link ISchedulingStrategy Scheduling strategy} that schedules entry with some fixed interval.
 *
 * <p>
 * Expected configuration arguments are:
 * </p>
 *
 * <ul>
 *     <li>{@code "start"} - date and time (ISO 8601) when the entry should/could be executed first time</li>
 *     <li>{@code "interval"} - interval in ISO-8601 format (e.g. {@code "PT8H10M42.36S"})</li>
 *     <li>{@code "save"} - (optional, defaults to {@code true}) {@code true} if the entry should be saved in remote storage</li>
 * </ul>
 */
public class ContinuouslyRepeatScheduleStrategy implements ISchedulingStrategy {
    private final IFieldName startFieldName;
    private final IFieldName intervalFieldName;
    private final IFieldName saveFieldName;

    private long nextTime(final LocalDateTime startTime, final TemporalAmount period, final long now) {
        long lStartTime = datetimeToMillis(startTime);

        if (lStartTime >= now) {
            return lStartTime;
        }

        if (period instanceof Duration) {
            long lPeriod = ((Duration) period).toMillis();
            long lNextTime = lStartTime + lPeriod * ((now - lStartTime) / lPeriod);
            return (lNextTime >= now) ? lNextTime : (lNextTime + lPeriod);
        }

        long lNextTime = 0;
        LocalDateTime start = millisToDatetime(lStartTime);
        while (lNextTime < now) {
            start = start.plus(period);
            lNextTime = datetimeToMillis(start);
        }

        return lNextTime;
    }

    private long datetimeToMillis(final LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    private LocalDateTime millisToDatetime(final long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
    }

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails resolving dependencies
     */
    public ContinuouslyRepeatScheduleStrategy()
            throws ResolutionException {
        startFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "start");
        intervalFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "interval");
        saveFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "save");
    }

    @Override
    public void init(final ISchedulerEntry entry, final IObject args) throws SchedulingStrategyExecutionException {
        try {
            String start = (String) args.getValue(startFieldName);
            LocalDateTime startTime;
            TemporalAmount interval = parseInterval((String) args.getValue(intervalFieldName));
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

            if (start == null) {
                startTime = now;
            } else {
                startTime = LocalDateTime.parse(start);
            }

            entry.getState().setValue(startFieldName, startTime.toString());
            entry.getState().setValue(intervalFieldName, interval.toString());

            if (args.getValue(saveFieldName) == null || (Boolean) args.getValue(saveFieldName)) {
                entry.save();
            }

            entry.scheduleNext(nextTime(startTime, interval, datetimeToMillis(now)));
        } catch (ReadValueException | InvalidArgumentException | EntryScheduleException | EntryStorageAccessException
                | ChangeValueException e) {
            throw new SchedulingStrategyExecutionException("Error occurred initializing scheduler entry.", e);
        }
    }

    @Override
    public void postProcess(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
        try {
            TemporalAmount interval = parseInterval((String) entry.getState().getValue(intervalFieldName));
            if (interval instanceof Duration) {
                entry.scheduleNext(entry.getLastTime() + ((Duration) interval).toMillis());
            }
            if (interval instanceof Period) {
                LocalDateTime end = millisToDatetime(entry.getLastTime()).plus(interval);
                entry.scheduleNext(datetimeToMillis(end));
            }
        } catch (ReadValueException | InvalidArgumentException | EntryScheduleException e) {
            throw new SchedulingStrategyExecutionException("Error occurred rescheduling scheduler entry.", e);
        }
    }

    @Override
    public void restore(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
        try {
            TemporalAmount interval = parseInterval((String) entry.getState().getValue(intervalFieldName));
            LocalDateTime startTime = LocalDateTime.parse((String) entry.getState().getValue(startFieldName));

            long nextTime = nextTime(startTime, interval, System.currentTimeMillis());

            entry.scheduleNext(nextTime);
        } catch (ReadValueException | InvalidArgumentException | EntryScheduleException e) {
            throw new SchedulingStrategyExecutionException("Error occurred restoring scheduler entry.", e);
        }
    }

    @Override
    public void processException(final ISchedulerEntry entry, final Throwable e) throws SchedulingStrategyExecutionException {
        try {
            entry.cancel();
        } catch (EntryStorageAccessException | EntryScheduleException ee) {
            throw new SchedulingStrategyExecutionException("Error occurred cancelling failed scheduler entry.", ee);
        }
    }

    @Override
    public void notifyPaused(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
    }

    @Override
    public void notifyUnPaused(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
        restore(entry);
    }

    @Override
    public void processPausedExecution(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
    }

    private TemporalAmount parseInterval(final String intervalString) {
        TemporalAmount interval;
        try {
            interval = Duration.parse(intervalString);
        } catch (DateTimeParseException e) {
            interval = Period.parse(intervalString);
        }
        return interval;
    }
}
