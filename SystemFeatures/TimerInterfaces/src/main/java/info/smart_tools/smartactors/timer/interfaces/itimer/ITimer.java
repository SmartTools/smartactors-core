package info.smart_tools.smartactors.timer.interfaces.itimer;

import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;

/**
 * Interface for a timer.
 */
public interface ITimer {
    /**
     * Schedule the task.
     *
     * <p>
     *     At the moment of time the task was scheduled on the task will be added to task queue.
     * </p>
     *
     * @param task    the task to schedule
     * @param time    the time (in milliseconds since the epoch) to schedule task on
     * @return a {@link ITimerTask} instance associated with the task scheduled on this timer
     * @throws TaskScheduleException if eny error occurs scheduling the task
     */
    ITimerTask schedule(final ITask task, final long time) throws TaskScheduleException;
}
