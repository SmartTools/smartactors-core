package info.smart_tools.smartactors.scheduler.interfaces;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulingStrategyExecutionException;

/**
 * Interface for a strategy defining when to execute tasks associated with scheduler entries.
 *
 * <h3>About pause handling</h3>
 *
 * <p>
 * Some strategies may provide non-empty implementation of some of {@link #processPausedExecution(ISchedulerEntry)},
 * {@link #notifyPaused(ISchedulerEntry)} and {@link #notifyUnPaused(ISchedulerEntry)} methods to handle entry pausing in a specific way.
 * By default (when all of those methods are not implemented) the entry is not guaranteed to be executed after it is paused and unpaused
 * if it was scheduled on time between pause and unpause events i.e. if {@link #processPausedExecution(ISchedulerEntry)} was called. Such a
 * situation may cause memory leak if there is no external task that will awake or cancel the entry.
 * </p>
 */
public interface ISchedulingStrategy {
    /**
     * Called by scheduler just after a entry was created with this strategy.
     *
     * @param entry    the entry
     * @param args     the arguments the entry was created with
     * @throws SchedulingStrategyExecutionException if any error occurs
     */
    void init(ISchedulerEntry entry, IObject args) throws SchedulingStrategyExecutionException;

    /**
     * Called by scheduler after the message associated with the entry was sent.
     *
     * @param entry    the entry
     * @throws SchedulingStrategyExecutionException if any error occurs
     */
    void postProcess(ISchedulerEntry entry) throws SchedulingStrategyExecutionException;

    /**
     * Called by scheduler when the entry is downloaded from database.
     *
     * @param entry    the entry
     * @throws SchedulingStrategyExecutionException if any error occurs
     */
    void restore(ISchedulerEntry entry) throws SchedulingStrategyExecutionException;

    /**
     * Called by scheduler when an exception occurs sending the message associated with the given entry or executing {@link
     * #postProcess(ISchedulerEntry)} method of this strategy.
     *
     * @param entry    the entry
     * @param e        the exception
     * @throws SchedulingStrategyExecutionException if any error occurs
     */
    void processException(ISchedulerEntry entry, Throwable e) throws SchedulingStrategyExecutionException;

    /**
     * Called by scheduler when entry is paused.
     *
     * @param entry    the entry
     * @throws SchedulingStrategyExecutionException if any error occurs
     */
    void notifyPaused(ISchedulerEntry entry) throws SchedulingStrategyExecutionException;

    /**
     * Called by scheduler when entry is un-paused.
     *
     * @param entry    the entry
     * @throws SchedulingStrategyExecutionException if any error occurs
     */
    void notifyUnPaused(ISchedulerEntry entry) throws SchedulingStrategyExecutionException;

    /**
     * Called by scheduler when entry was scheduled on the time it was paused.
     *
     * @param entry    the entry
     * @throws SchedulingStrategyExecutionException if any error occurs.
     */
    void processPausedExecution(ISchedulerEntry entry) throws SchedulingStrategyExecutionException;
}
