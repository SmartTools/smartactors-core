package info.smart_tools.smartactors.scheduler.interfaces;

import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerEntryStorageObserverException;

/**
 * Interface for object that will be notified on some events occurring within {@link ISchedulerEntryStorage entry storage}.
 */
public interface ISchedulerEntryStorageObserver {
    /**
     * Called by {@link ISchedulerEntryStorage entry storage} when an entry is rescheduled.
     *
     * @param entry    the rescheduled entry
     * @throws SchedulerEntryStorageObserverException if any error occurs
     */
    void onUpdateEntry(ISchedulerEntry entry) throws SchedulerEntryStorageObserverException;

    /**
     * Called by {@link ISchedulerEntryStorage entry storage} when an entry is cancelled by scheduling strategy or because of direct call of
     * {@link ISchedulerEntry#cancel()} method.
     *
     * @param entry    the cancelled entry
     * @throws SchedulerEntryStorageObserverException if any error occurs
     */
    void onCancelEntry(ISchedulerEntry entry) throws SchedulerEntryStorageObserverException;
}
