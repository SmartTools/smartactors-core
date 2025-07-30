package info.smart_tools.smartactors.shutdown.ishutdown_aware_task;

import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.exceptions.ShutdownAwareTaskNotificationException;

/**
 * Interface for managed task representation that allows to notify a task about pending shutdown request.
 */
public interface IShutdownAwareTask {
    /**
     * Notify this task that there is a shutdown request.
     *
     * @throws ShutdownAwareTaskNotificationException if any error occurs processing notification
     */
    void notifyShuttingDown() throws ShutdownAwareTaskNotificationException;

    /**
     * Notify this task that there is a shutdown request and shutdown strategy decided not to execute this task.
     *
     * @throws ShutdownAwareTaskNotificationException if any error occurs processing notification
     */
    void notifyIgnored() throws ShutdownAwareTaskNotificationException;

    /**
     * Store a shutdown status object (required for some shutdown strategies).
     *
     * @param status    new shutdown status
     */
    void setShutdownStatus(Object status);

    /**
     * @return object stored by last call of {@link #setShutdownStatus(Object)}.
     */
    Object getShutdownStatus();
}
