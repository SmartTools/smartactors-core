package info.smart_tools.smartactors.scheduler.actor.impl;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.scheduler.actor.impl.exceptions.CancelledLocalEntryRequestException;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.IRemoteEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorageObserver;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerEntryStorageObserverException;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Implementation of {@link ISchedulerEntryStorage}.
 */
public class EntryStorage implements ISchedulerEntryStorage {
    private final ITimer timer;

    private final IRemoteEntryStorage remoteEntryStorage;
    private final ISchedulerEntryStorageObserver observer;

    private final Map<String, ISchedulerEntry> activeEntries;
    private final Map<String, ISchedulerEntry> strongSuspendEntries;
    private final Map<String, WeakReference<ISchedulerEntry>> weakSuspendEntries;

    private final List<ISchedulerEntry> refreshAwakeList;
    private final List<ISchedulerEntry> refreshSuspendList;
    private final HashSet<String>[] recentlyDeletedIdSets;
    private int refreshIterationCounter;

    private final Object localStorageLock;

    private boolean isEntryCancelledRecently(final String id) {
        return recentlyDeletedIdSets[0].contains(id) || recentlyDeletedIdSets[1].contains(id);
    }

    /**
     * The constructor.
     *
     * @param remoteEntryStorage    remote storage to use
     * @param observer              the observer that should be notified on events occurring within this storage
     * @param timer                 the timer to use
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public EntryStorage(final IRemoteEntryStorage remoteEntryStorage, final ISchedulerEntryStorageObserver observer, final ITimer timer)
            throws ResolutionException {
        this.remoteEntryStorage = remoteEntryStorage;
        this.observer = (observer == null) ? NullEntryStorageObserver.INSTANCE : observer;
        this.timer = timer;

        activeEntries = new HashMap<>();
        strongSuspendEntries = new HashMap<>();
        weakSuspendEntries = new WeakHashMap<>();

        refreshAwakeList = new ArrayList<>();
        refreshSuspendList = new ArrayList<>();
        recentlyDeletedIdSets = new HashSet[] {new HashSet<>(), new HashSet<>()};
        refreshIterationCounter = 0;

        localStorageLock = new Object();
    }

    /**
     * The constructor with default system timer.
     *
     * @param remoteEntryStorage    remote storage to use
     * @param observer              the observer that should be notified on events occurring within this storage
     * @throws ResolutionException if fails to resolve any dependencies (including the timer)
     */
    public EntryStorage(final IRemoteEntryStorage remoteEntryStorage, final ISchedulerEntryStorageObserver observer)
            throws ResolutionException {
        this(remoteEntryStorage, observer, IOC.resolve(Keys.getOrAdd("timer")));
    }

    @Override
    public void save(final ISchedulerEntry entry)
            throws EntryStorageAccessException {
        remoteEntryStorage.saveEntry(entry);
    }

    @Override
    public void notifyActive(final ISchedulerEntry entry)
            throws EntryStorageAccessException {
        final String entryId = entry.getId();

        synchronized (localStorageLock) {
            if (isEntryCancelledRecently(entryId)) {
                try {
                    entry.cancel();
                    return;
                } catch (EntryScheduleException e) {
                    throw new EntryStorageAccessException("Error occurred cancelling duplicate entry.", e);
                }
            }

            ISchedulerEntry oldEntry = activeEntries.put(entryId, entry);

            strongSuspendEntries.remove(entryId, entry);
            weakSuspendEntries.remove(entryId, entry);

            try {
                observer.onUpdateEntry(entry);
            } catch (SchedulerEntryStorageObserverException e) {
                throw new EntryStorageAccessException("Error occurred notifying observer on updated entry.", e);
            }

            if (null != oldEntry && entry != oldEntry) {
                try {
                    oldEntry.cancel();
                } catch (EntryScheduleException e) {
                    throw new EntryStorageAccessException("Error cancelling duplicate entry.", e);
                }
            }
        }

        remoteEntryStorage.weakSaveEntry(entry);
    }

    @Override
    public void notifyInactive(final ISchedulerEntry entry, final boolean keepReference) throws EntryStorageAccessException {
        synchronized (localStorageLock) {
            activeEntries.remove(entry.getId(), entry);

            if (keepReference) {
                strongSuspendEntries.put(entry.getId(), entry);
            } else {
                weakSuspendEntries.put(entry.getId(), new WeakReference<>(entry));
            }
        }
    }

    @Override
    public void delete(final ISchedulerEntry entry)
            throws EntryStorageAccessException {
        synchronized (localStorageLock) {
            try {
                recentlyDeletedIdSets[refreshIterationCounter & 1].add(entry.getId());

                activeEntries.remove(entry.getId(), entry);
                strongSuspendEntries.remove(entry.getId(), entry);
                weakSuspendEntries.remove(entry.getId(), entry);

                observer.onCancelEntry(entry);
            } catch (SchedulerEntryStorageObserverException e) {
                throw new EntryStorageAccessException("Error occurred notifying observer on deleted entry.");
            }
        }

        // It's safe to delete entry from database outside of critical section as we remember that
        // the entry was cancelled and will not let it get re-created.
        remoteEntryStorage.deleteEntry(entry);
    }

    @Override
    public List<ISchedulerEntry> listLocalEntries()
            throws EntryStorageAccessException {
        synchronized (localStorageLock) {
            List<ISchedulerEntry> localEntries = new ArrayList<>(activeEntries.size() + strongSuspendEntries.size());
            localEntries.addAll(activeEntries.values());
            localEntries.addAll(strongSuspendEntries.values());
            // TODO:: Enumerate remote entries (?)
            return localEntries;
        }
    }

    @Override
    public ISchedulerEntry getEntry(final String id)
            throws EntryStorageAccessException {
        try {
            ISchedulerEntry localEntry = getLocalEntry(id);

            if (null != localEntry) {
                return localEntry;
            }

            IObject savedEntryState = remoteEntryStorage.querySingleEntry(id);

            return IOC.resolve(Keys.getOrAdd("restore scheduler entry"), savedEntryState, this);
        } catch (ResolutionException e) {
            throw new EntryStorageAccessException("Error occurred restoring required entry from state saved in remote storage.");
        } catch (CancelledLocalEntryRequestException e) {
            throw new EntryStorageAccessException("The entry was not found as it was cancelled recently.");
        }
    }

    @Override
    public ITimer getTimer() {
        return this.timer;
    }

    /**
     * Suspend active entries that are scheduled on too late time and awake suspended entries scheduled for not-so late time.
     *
     * <pre>
     *     | . . . . . . . . . . . . . . | . . . . . . . . . . . . . . | . . . . . . . . . (time) >
     *     |now                          |awakeUntil                   |suspendAfter
     *     | (awake everything here)     | (do nothing)                | (suspend everything here)
     * </pre>
     *
     * @param awakeUntil      the time entries scheduled until should be awaken
     * @param suspendAfter    the time entries scheduled after should be suspended
     * @throws EntryStorageAccessException if error occurs accessing entry storage while awakening/suspending some entry
     * @throws EntryScheduleException if error occurs rescheduling some entry to awake it
     */
    public void refresh(final long awakeUntil, final long suspendAfter)
            throws EntryStorageAccessException, EntryScheduleException {
        synchronized (localStorageLock) {
            // Keep references in separate list to avoid ConcurrentModificationException (awake/suspend methods may remove the entry from the
            // map we are iterating over)
            refreshAwakeList.clear();
            refreshSuspendList.clear();

            for (ISchedulerEntry suspendedEntry : strongSuspendEntries.values()) {
                if (suspendedEntry.getLastTime() < awakeUntil) {
                    refreshAwakeList.add(suspendedEntry);
                }
            }

            for (ISchedulerEntry activeEntry : activeEntries.values()) {
                if (activeEntry.getLastTime() > suspendAfter) {
                    refreshSuspendList.add(activeEntry);
                } else if (activeEntry.getLastTime() < awakeUntil && !activeEntry.isAwake()) {
                    // Awake entries loaded by refresher and "zombie" entries -- cancelled but remaining in list of active (they should
                    // delete themselves)
                    refreshAwakeList.add(activeEntry);
                }
            }

            for (ISchedulerEntry entry : refreshAwakeList) {
                entry.awake();
            }

            for (ISchedulerEntry entry : refreshSuspendList) {
                entry.suspend();
            }

            recentlyDeletedIdSets[(refreshIterationCounter + 1) & 1].clear();

            ++refreshIterationCounter;
        }
    }

    /**
     * Get entry saved locally (active or suspended).
     *
     * @param id    identifier of the entry
     * @return the entry or {@code null} if there is no entry with given identifier in local storage
     * @throws CancelledLocalEntryRequestException if the required entry was cancelled recently
     */
    public ISchedulerEntry getLocalEntry(final String id) throws CancelledLocalEntryRequestException {
        synchronized (localStorageLock) {
            if (isEntryCancelledRecently(id)) {
                throw new CancelledLocalEntryRequestException();
            }

            if (weakSuspendEntries.containsKey(id)) {
                ISchedulerEntry e = weakSuspendEntries.get(id).get();

                if (null == e) {
                    weakSuspendEntries.remove(id);
                } else {
                    return e;
                }
            }

            if (activeEntries.containsKey(id)) {
                return activeEntries.get(id);
            }

            if (strongSuspendEntries.containsKey(id)) {
                return strongSuspendEntries.get(id);
            }

            return null;
        }
    }
}
