package info.smart_tools.smartactors.scheduler.actor.impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.actor.impl.exceptions.CancelledLocalEntryRequestException;
import info.smart_tools.smartactors.scheduler.actor.impl.filter.AllPassEntryFilter;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.IRemoteEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorageObserver;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.*;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of {@link ISchedulerEntryStorage}.
 */
public class EntryStorage implements ISchedulerEntryStorage {
    /**
     * Subclass of {@link WeakReference weak reference} to {@link ISchedulerEntry scheduler entry} that stores entry identifier.
     */
    private static class WeakEntryReference extends WeakReference<ISchedulerEntry> {
        private final String id;

        /**
         * The constructor.
         *
         * @param referent    the entry
         * @param q           the reference queue
         */
        WeakEntryReference(final ISchedulerEntry referent, final ReferenceQueue<? super ISchedulerEntry> q) {
            super(referent, q);
            this.id = referent.getId();
        }

        String getId() {
            return id;
        }
    }

    private final ITimer timer;

    private final IRemoteEntryStorage remoteEntryStorage;
    private final ISchedulerEntryStorageObserver observer;

    private final Map<String, ISchedulerEntry> activeEntries;
    private final Map<String, ISchedulerEntry> strongSuspendEntries;
    private final Map<String, WeakEntryReference> weakSuspendEntries;
    private final ReferenceQueue<ISchedulerEntry> weakSuspendReferenceQueue = new ReferenceQueue<>();

    private final List<ISchedulerEntry> refreshAwakeList;
    private final List<ISchedulerEntry> refreshSuspendList;
    private final HashSet<String>[] recentlyDeletedIdSets;
    private int refreshIterationCounter;

    private ISchedulerEntryFilter filter = AllPassEntryFilter.INSTANCE;

    private final Lock localStorageLock;
    private final Object refreshLock;

    private boolean isEntryCancelledRecently(final String id) {
        return recentlyDeletedIdSets[0].contains(id) || recentlyDeletedIdSets[1].contains(id);
    }

    private void cleanupWeakSuspendedEntries() {
        WeakEntryReference reference;

        while (null != (reference = (WeakEntryReference) weakSuspendReferenceQueue.poll())) {
            weakSuspendEntries.remove(reference.getId());
        }
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
        weakSuspendEntries = new HashMap<>();

        refreshAwakeList = new ArrayList<>();
        refreshSuspendList = new ArrayList<>();
        recentlyDeletedIdSets = new HashSet[] {new HashSet<>(), new HashSet<>()};
        refreshIterationCounter = 0;

        localStorageLock = new ReentrantLock();
        refreshLock = new Object();
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
        this(remoteEntryStorage, observer, IOC.resolve(Keys.getKeyByName("timer")));
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
        boolean keepLock = true;

        localStorageLock.lock();
        try {
            cleanupWeakSuspendedEntries();

            if (isEntryCancelledRecently(entryId)) {
                localStorageLock.unlock();
                keepLock = false;
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

            if (null != oldEntry && entry != oldEntry) {
                try {
                    localStorageLock.unlock();
                    keepLock = false;
                    oldEntry.cancel();
                } catch (EntryScheduleException e) {
                    throw new EntryStorageAccessException("Error cancelling duplicate entry.", e);
                }
            }
        } finally {
            if (keepLock) {
                localStorageLock.unlock();
            }
        }

        try {
            observer.onUpdateEntry(entry);
        } catch (SchedulerEntryStorageObserverException e) {
            throw new EntryStorageAccessException("Error occurred notifying observer on updated entry.", e);
        }

        remoteEntryStorage.weakSaveEntry(entry);
    }

    @Override
    public void notifyInactive(final ISchedulerEntry entry, final boolean keepReference) throws EntryStorageAccessException {
        localStorageLock.lock();
        try {
            activeEntries.remove(entry.getId(), entry);

            if (keepReference) {
                strongSuspendEntries.put(entry.getId(), entry);
            } else {
                weakSuspendEntries.put(entry.getId(), new WeakEntryReference(entry, weakSuspendReferenceQueue));
            }

            cleanupWeakSuspendedEntries();
        } finally {
            localStorageLock.unlock();
        }
    }

    @Override
    public void delete(final ISchedulerEntry entry)
            throws EntryStorageAccessException, EntryNotFoundException {
        localStorageLock.lock();
        try {
            cleanupWeakSuspendedEntries();

            recentlyDeletedIdSets[refreshIterationCounter & 1].add(entry.getId());

            activeEntries.remove(entry.getId(), entry);
            strongSuspendEntries.remove(entry.getId(), entry);
            weakSuspendEntries.remove(entry.getId(), entry);
        } finally {
            localStorageLock.unlock();
        }

        try {
            observer.onCancelEntry(entry);
        } catch (SchedulerEntryStorageObserverException e) {
            throw new EntryStorageAccessException("Error occurred notifying observer on deleted entry.");
        }

        // It's safe to delete entry from database outside of critical section as we remember that
        // the entry was cancelled and will not let it get re-created.
        remoteEntryStorage.deleteEntry(entry);
    }

    @Override
    public List<ISchedulerEntry> listLocalEntries()
            throws EntryStorageAccessException {
        localStorageLock.lock();
        try {
            List<ISchedulerEntry> localEntries = new ArrayList<>(activeEntries.size() + strongSuspendEntries.size());
            localEntries.addAll(activeEntries.values());
            localEntries.addAll(strongSuspendEntries.values());
            // TODO:: Enumerate remote entries (?)
            return localEntries;
        } finally {
            localStorageLock.unlock();
        }
    }

    @Override
    public int countLocalEntries() throws EntryStorageAccessException {
        return activeEntries.size() + strongSuspendEntries.size() + weakSuspendEntries.size();
    }

    @Override
    public ISchedulerEntry getEntry(final String id)
            throws EntryStorageAccessException, EntryNotFoundException {
        try {
            ISchedulerEntry localEntry = getLocalEntry(id);

            if (null != localEntry) {
                return localEntry;
            }

            IObject savedEntryState = remoteEntryStorage.querySingleEntry(id);

            return IOC.resolve(Keys.getKeyByName("restore scheduler entry"), savedEntryState, this);
        } catch (ResolutionException e) {
            throw new EntryStorageAccessException("Error occurred restoring required entry from state saved in remote storage.");
        } catch (CancelledLocalEntryRequestException e) {
            throw new EntryNotFoundException("The entry was not found as it was cancelled recently.");
        }
    }

    @Override
    public ITimer getTimer() {
        return this.timer;
    }

    @Override
    public ISchedulerEntryFilter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(final ISchedulerEntryFilter filter) throws InvalidArgumentException {
        if (null == filter) {
            throw new InvalidArgumentException("Filter should not be null.");
        }

        this.filter = filter;
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
     * @throws SchedulerEntryFilterException if error occurs interacting {@link ISchedulerEntryFilter entry filter}
     */
    public void refresh(final long awakeUntil, final long suspendAfter)
            throws EntryStorageAccessException, EntryScheduleException, SchedulerEntryFilterException {
        synchronized (refreshLock) {
            localStorageLock.lock();

            cleanupWeakSuspendedEntries();

            try {
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
            } finally {
                localStorageLock.unlock();
            }

            for (ISchedulerEntry entry : refreshAwakeList) {
                if (filter.testAwake(entry)) {
                    entry.awake();
                }
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
        localStorageLock.lock();
        try {
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
        } finally {
            localStorageLock.unlock();
        }
    }
}
