package info.smart_tools.smartactors.timer.timer;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimerTask;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;

import java.util.Timer;

/**
 * Implementation of {@link ITimer} that uses standard java {@link Timer}.
 */
public class TimerImpl implements ITimer {
    private final Timer timer;

    /**
     * The constructor.
     *
     * @param timer    the standard timer to use
     * @throws InvalidArgumentException if {@code timer} is {@code null}
     */
    public TimerImpl(final Timer timer)
            throws InvalidArgumentException {
        if (null == timer) {
            throw new InvalidArgumentException("Timer should not be null.");
        }

        this.timer = timer;
    }

    @Override
    public ITimerTask schedule(final ITask task, final long time) throws TaskScheduleException {
        return new TimerTaskImpl(task, timer, time);
    }
}
