package info.smart_tools.smartactors.scheduler.actor.impl.timer;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartupException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.IDelayedSynchronousService;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITime;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimerTask;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;

/**
 * Service that provides a timer that can be started and stopped.
 */
public class SchedulerTimer implements IDelayedSynchronousService, ITimer {
    private volatile long myStartTime;
    private volatile long myStopTime;
    private final ITime sysTime;
    private final ITimer underlyingTimer;

    /**
     * Wrapper for {@link ITimerTask timer task} that schedules and executes tasks only if the service is running.
     */
    private class SchedulerTimerTask implements ITimerTask, ITask {
        private ITimerTask underlyingTimerTask;
        private final ITask underlyingTask;

        SchedulerTimerTask(final ITask task, final long initialTime) throws TaskScheduleException {
            underlyingTask = task;
            reschedule(initialTime);
        }

        @Override
        public boolean cancel() {
            return underlyingTimerTask.cancel();
        }

        @Override
        public void reschedule(final long time) throws TaskScheduleException {
            if (!isTimeAcceptable(time)) {
                if (null != underlyingTimerTask) {
                    underlyingTimerTask.cancel();
                }

                return;
            }

            if (null == underlyingTimerTask) {
                underlyingTimerTask = underlyingTimer.schedule(this, time);
            } else {
                underlyingTimerTask.reschedule(time);
            }
        }

        @Override
        public void execute() throws TaskExecutionException {
            if (isTimeAcceptable(sysTime.currentTimeMillis())) {
                underlyingTask.execute();
            }
        }
    }

    private boolean isTimeAcceptable(final long time) {
        return time > myStartTime && time < myStopTime;
    }

    /**
     * The constructor.
     *
     * @param underlyingTimer    the real timer
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public SchedulerTimer(final ITimer underlyingTimer) throws ResolutionException {
        this.myStartTime = Long.MAX_VALUE;
        this.myStopTime = Long.MAX_VALUE;

        this.sysTime = IOC.resolve(Keys.getOrAdd("time"));
        this.underlyingTimer = underlyingTimer;
    }

    @Override
    public void start()
            throws IllegalServiceStateException, ServiceStartupException {
        this.myStartTime = 0;
    }

    @Override
    public void stop()
            throws IllegalServiceStateException, ServiceStopException {
        this.myStopTime = 0;
    }

    @Override
    public void startAfter(final long startTime)
            throws ServiceStartupException, IllegalServiceStateException, InvalidArgumentException {
        this.myStartTime = startTime;
        this.myStopTime = Long.MAX_VALUE;
    }

    @Override
    public void stopAfter(final long stopTime)
            throws ServiceStopException, IllegalServiceStateException, InvalidArgumentException {
        this.myStopTime = stopTime;
    }

    @Override
    public ITimerTask schedule(final ITask task, final long scheduleTime) throws TaskScheduleException {
        return new SchedulerTimerTask(task, scheduleTime);
    }
}
