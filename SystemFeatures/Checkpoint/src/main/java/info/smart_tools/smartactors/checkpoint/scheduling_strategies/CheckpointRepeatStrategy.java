package info.smart_tools.smartactors.checkpoint.scheduling_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulingStrategyExecutionException;

import java.time.Duration;
import java.time.format.DateTimeParseException;

/**
 * Base class for checkpoint scheduling strategies.
 */
public abstract class CheckpointRepeatStrategy implements ISchedulingStrategy {
    private final IFieldName timesFieldName;
    private final IFieldName remainingTimesFieldName;
    private final IFieldName postRestoreDelayFieldName;
    private final IFieldName postCompletionDelayFieldName;
    private final IFieldName completedFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    protected CheckpointRepeatStrategy()
            throws ResolutionException {
        timesFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "times");
        remainingTimesFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "remainingTimes");
        postRestoreDelayFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "postRestoreDelay");
        postCompletionDelayFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "postCompletionDelay");
        completedFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "completed");
    }

    /**
     * Calculate interval before re-sending the message next time.
     *
     * @param entry    the entry
     * @return interval in milliseconds
     * @throws ReadValueException if error occurs reading values from entry state
     * @throws InvalidArgumentException if error occurs reading values from entry state
     * @throws ChangeValueException if error occurs changing entry state to update data about next interval
     */
    protected abstract long calculateNextInterval(ISchedulerEntry entry)
            throws ReadValueException, InvalidArgumentException, ChangeValueException;

    /**
     * Get default post-restore delay.
     *
     * @param entry the entry
     * @return default post-restore delay
     * @throws ReadValueException if error occurs reading values from entry state
     * @throws InvalidArgumentException if error occurs reading values from entry state
     */
    protected abstract Duration defaultPostRestoreDelay(ISchedulerEntry entry)
            throws ReadValueException, InvalidArgumentException;

    /**
     * Get default post-completion delay.
     *
     * @param entry the entry
     * @return default post-completion delay
     * @throws ReadValueException if error occurs reading values from entry state
     * @throws InvalidArgumentException if error occurs reading values from entry state
     */
    protected abstract Duration defaultPostCompletionDelay(ISchedulerEntry entry)
            throws ReadValueException, InvalidArgumentException;

    @Override
    public void init(final ISchedulerEntry entry, final IObject args) throws SchedulingStrategyExecutionException {
        try {
            int times = ((Number) args.getValue(timesFieldName)).intValue();

            if (times == 0) {
                return;
            }

            Duration postRestoreDelay = (args.getValue(postRestoreDelayFieldName) != null)
                    ? Duration.parse((String) args.getValue(postRestoreDelayFieldName)) : defaultPostRestoreDelay(entry);
            Duration postCompletionDelay = (args.getValue(postCompletionDelayFieldName) != null)
                    ? Duration.parse((String) args.getValue(postCompletionDelayFieldName)) : defaultPostCompletionDelay(entry);

            IObject state = entry.getState();

            state.setValue(postRestoreDelayFieldName, postRestoreDelay.toString());
            state.setValue(postCompletionDelayFieldName, postCompletionDelay.toString());
            state.setValue(timesFieldName, times);
            state.setValue(remainingTimesFieldName, times - 1);

            entry.save();

            entry.scheduleNext(System.currentTimeMillis() + calculateNextInterval(entry));
        } catch (ReadValueException | InvalidArgumentException | ChangeValueException | DateTimeParseException
                | EntryStorageAccessException | EntryScheduleException e) {
            throw new SchedulingStrategyExecutionException("Error occurred initializing scheduling strategy.", e);
        }
    }

    @Override
    public void postProcess(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
        try {
            int remainingTimes = ((Number) entry.getState().getValue(remainingTimesFieldName)).intValue();
            long scheduleTime = entry.getLastTime();

            if (null != entry.getState().getValue(completedFieldName)) {
                if (0 == remainingTimes) {
                    entry.cancel();
                    return;
                } else {
                    // There were trials remaining but checkpoint actor marked the entry as completed because of received feedback message
                    entry.getState().setValue(remainingTimesFieldName, 0);
                    Duration delay = Duration.parse((String) entry.getState().getValue(postCompletionDelayFieldName));
                    scheduleTime += delay.toMillis();
                }
            } else {
                if (0 == remainingTimes) {
                    entry.getState().setValue(completedFieldName, true);
                    Duration delay = Duration.parse((String) entry.getState().getValue(postCompletionDelayFieldName));
                    scheduleTime += delay.toMillis();
                } else {
                    long interval = calculateNextInterval(entry);
                    entry.getState().setValue(remainingTimesFieldName, remainingTimes - 1);
                    scheduleTime += interval;
                }
            }

            entry.save();
            entry.scheduleNext(scheduleTime);
        } catch (ReadValueException | InvalidArgumentException | ChangeValueException | EntryScheduleException
                | EntryStorageAccessException e) {
            throw new SchedulingStrategyExecutionException("Error occurred post processing checkpoint scheduler entry.", e);
        }
    }

    @Override
    public void restore(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
        try {
            long scheduleTime = System.currentTimeMillis();

            if (null != entry.getState().getValue(completedFieldName)) {
                entry.getState().setValue(remainingTimesFieldName, 0);
                Duration delay = Duration.parse((String) entry.getState().getValue(postCompletionDelayFieldName));
                scheduleTime += delay.toMillis();
            } else {
                Duration delay = Duration.parse((String) entry.getState().getValue(postRestoreDelayFieldName));
                scheduleTime += delay.toMillis();
            }

            entry.scheduleNext(scheduleTime);
        } catch (ReadValueException | InvalidArgumentException | ChangeValueException | EntryScheduleException e) {
            throw new SchedulingStrategyExecutionException("Error occurred restoring checkpoint scheduler entry.", e);
        }
    }

    @Override
    public void processException(final ISchedulerEntry entry, final Throwable e) throws SchedulingStrategyExecutionException {
        if (e instanceof SchedulerActionExecutionException) {
            // Action execution failed. This may happen because of errors the checkpoint is created to avoid (chain not fond, message bus or
            // other components are not initialized properly, etc.)
            // OK, let's retry next time.
            return;
        }

        try {
            // The exception occurred in scheduling strategy itself.
            // TODO: Handle another way (?)
            entry.cancel();
        } catch (EntryScheduleException | EntryStorageAccessException e1) {
            e1.addSuppressed(e);
            throw new SchedulingStrategyExecutionException("Error occurred cancelling failed checkpoint scheduler entry.", e1);
        }
    }

    @Override
    public void notifyPaused(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {

    }

    @Override
    public void notifyUnPaused(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
        try {
            entry.awake();
        } catch (EntryStorageAccessException | EntryScheduleException e) {
            throw new SchedulingStrategyExecutionException(e);
        }
    }

    @Override
    public void processPausedExecution(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
    }
}
