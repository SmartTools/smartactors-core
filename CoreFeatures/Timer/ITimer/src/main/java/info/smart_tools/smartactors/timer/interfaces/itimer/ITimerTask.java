package info.smart_tools.smartactors.timer.interfaces.itimer;

import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;

/**
 * Object associated with a {@link ITask} scheduled on a {@link ITimer}.
 */
public interface ITimerTask {
    /**
     * Cancel the task.
     *
     * @return {@code true} if the task was successful cancelled
     */
    boolean cancel();

    /**
     * Reschedule this task to given time.
     *
     * <p>
     *     If the task was not executed at the moment of call of this method it will be cancelled and scheduled again.
     * </p>
     *
     * <p>
     *     If the task was executed at the moment of call of this method it will be scheduled again.
     * </p>
     *
     * @param time    the time to schedule task on
     * @throws TaskScheduleException if any error occurs scheduling the task
     */
    void reschedule(long time) throws TaskScheduleException;
}
