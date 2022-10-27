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
 * Strategy that re-sends message with intervals proportional to Fibonacci sequence numbers.
 *
 * <p> Configuration example: </p>
 * <pre>
 *     {
 *       "strategy": "checkpoint fibonacci repeat strategy",
 *       "baseInterval": "PT30M",                               // Interval that will be multiplied by the next number of Fibonacci
 *                                                              // sequence every time
 *       "times": 4                                             // How many times to re-send the message
 *     }
 * </pre>
 */
public class CheckpointFibonacciRepeatStrategy extends CheckpointRepeatStrategy {
    private final IFieldName baseIntervalFieldName;
    private final IFieldName number1FieldName;
    private final IFieldName number2FieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public CheckpointFibonacciRepeatStrategy() throws ResolutionException {
        super();

        baseIntervalFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "baseInterval");
        number1FieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "number1");
        number2FieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "number2");
    }

    @Override
    protected long calculateNextInterval(final ISchedulerEntry entry) throws ReadValueException, InvalidArgumentException, ChangeValueException {
        int number1 = ((Number) entry.getState().getValue(number1FieldName)).intValue();
        int number2 = ((Number) entry.getState().getValue(number2FieldName)).intValue();
        int number3 = number1 + number2;

        entry.getState().setValue(number1FieldName, number2);
        entry.getState().setValue(number2FieldName, number3);

        long baseInt = Duration.parse((String) entry.getState().getValue(baseIntervalFieldName)).toMillis();

        return baseInt * (long) number3;
    }

    @Override
    protected Duration defaultPostRestoreDelay(final ISchedulerEntry entry) throws ReadValueException, InvalidArgumentException {
        return Duration.parse((String) entry.getState().getValue(baseIntervalFieldName));
    }

    @Override
    protected Duration defaultPostCompletionDelay(final ISchedulerEntry entry) throws ReadValueException, InvalidArgumentException {
        return Duration.parse((String) entry.getState().getValue(baseIntervalFieldName));
    }

    @Override
    public void init(final ISchedulerEntry entry, final IObject args) throws SchedulingStrategyExecutionException {
        try {
            Duration interval = Duration.parse((String) args.getValue(baseIntervalFieldName));
            entry.getState().setValue(baseIntervalFieldName, interval.toString());

            entry.getState().setValue(number1FieldName, 0);
            entry.getState().setValue(number2FieldName, 1);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new SchedulingStrategyExecutionException("Error occurred initializing scheduling strategy.", e);
        }

        super.init(entry, args);
    }
}
