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
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulingStrategyExecutionException;

import java.time.Duration;

/**
 * Strategy that re-sends the message with fixed intervals fixed number of times.
 *
 * <p> Configuration example: </p>
 * <pre>
 * {
 *   "strategy": "checkpoint repeat strategy",
 *   "interval": "PT3H",                    //Interval in ISO-8601 format
 *   "times": 3,                            // How many times to re-send the message
 *
 *   "postRestoreDelay": "PT2M",            // (optional) delay before first re-send of the message after the entry is restored from remote
 *                                          // storage. By default "interval" is used.
 *   "postCompletionDelay": "PT5M"          // (optional) delay before deletion of the entry when feedback successfully received. By default
 *                                          // "interval" is used.
 * }
 * </pre>
 */
public class CheckpointRegularRepeatStrategy extends CheckpointRepeatStrategy {
    private final IFieldName intervalFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public CheckpointRegularRepeatStrategy() throws ResolutionException {
        intervalFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "interval");
    }

    @Override
    protected long calculateNextInterval(final ISchedulerEntry entry)
            throws ReadValueException, InvalidArgumentException, ChangeValueException {
        return Duration.parse((String) entry.getState().getValue(intervalFieldName)).toMillis();
    }

    @Override
    protected Duration defaultPostRestoreDelay(final ISchedulerEntry entry) throws ReadValueException, InvalidArgumentException {
        return Duration.parse((String) entry.getState().getValue(intervalFieldName));
    }

    @Override
    protected Duration defaultPostCompletionDelay(final ISchedulerEntry entry) throws ReadValueException, InvalidArgumentException {
        return Duration.parse((String) entry.getState().getValue(intervalFieldName));
    }

    @Override
    public void init(final ISchedulerEntry entry, final IObject args) throws SchedulingStrategyExecutionException {
        try {
            Duration interval = Duration.parse((String) args.getValue(intervalFieldName));
            entry.getState().setValue(intervalFieldName, interval.toString());
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new SchedulingStrategyExecutionException("Error occurred initializing scheduling strategy.", e);
        }

        super.init(entry, args);
    }
}
