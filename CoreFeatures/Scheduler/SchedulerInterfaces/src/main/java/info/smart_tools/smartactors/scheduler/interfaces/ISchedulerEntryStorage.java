package info.smart_tools.smartactors.scheduler.interfaces;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;

import java.util.List;

/**
 * Provides local (in-memory) and remote (database) storage for {@link ISchedulerEntry scheduler entries}.
 */
public interface ISchedulerEntryStorage {
    /**
     * Save the entry to both local and remote storage.
     *
     * @param entry    the entry to save
     * @throws EntryStorageAccessException if error occurs accessing remote storage
     */
    void save(ISchedulerEntry entry) throws EntryStorageAccessException;

    /**
     * Notify the storage that the entry is active now.
     *
     * @param entry    the entry to save
     * @throws EntryStorageAccessException if error occurs
     */
    void notifyActive(ISchedulerEntry entry) throws EntryStorageAccessException;

    /**
     * Notify the storage that the entry is not active now.
     *
     * @param entry            the entry
     * @param keepReference    {@code true} if the storage should keep a strong reference to the entry and awake it when necessary (when the
     *                                     entry is not saved in remote storage)
     * @throws EntryStorageAccessException if error occurs
     */
    void notifyInactive(ISchedulerEntry entry, boolean keepReference) throws EntryStorageAccessException;

    /**
     * Delete the given entry from both local and remote storage.
     *
     * <p>
     * <b>IMPORTANT:</b> this method should be called only by entry implementation's {@link ISchedulerEntry#cancel() #cancel()} method. When
     * called from outside the result is undefined: the entry may remain active (if it saves itself on next execution) or remain active only
     * until server shutdown (when it does not). To delete and cancel the entry call {@link ISchedulerEntry#cancel() #cancel()} method of
     * the entry itself.
     * </p>
     *
     * @param entry    the entry
     * @throws EntryStorageAccessException if error occurs accessing remote storage
     */
    void delete(ISchedulerEntry entry) throws EntryStorageAccessException;

    /**
     * Get list of all locally saved entries.
     *
     * @return list of all locally saved entries
     * @throws EntryStorageAccessException if any error occurs
     */
    List<ISchedulerEntry> listLocalEntries() throws EntryStorageAccessException;

    /**
     * Get (a locally saved) entry with given identifier.
     *
     * @param id    the entry identifier
     * @return the entry saved with given identifier
     * @throws EntryStorageAccessException if there is no entry with given identifier
     */
    ISchedulerEntry getEntry(String id) throws EntryStorageAccessException;

    /**
     * @return a timer that should be used by entries stored in this storage
     */
    ITimer getTimer();

    /**
     * @return {@link ISchedulerEntryFilter filter} that should be applied to all entries
     */
    ISchedulerEntryFilter getFilter();

    /**
     * @param filter    the new {@link ISchedulerEntryFilter filter} to use
     * @throws InvalidArgumentException if {@code filter} is {@code null}
     */
    void setFilter(ISchedulerEntryFilter filter) throws InvalidArgumentException;
}
