package info.smart_tools.smartactors.scheduler.actor.impl;

import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorageObserver;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerEntryStorageObserverException;

/**
 * Null-object implementing {@link ISchedulerEntryStorageObserver}.
 */
public final class NullEntryStorageObserver implements ISchedulerEntryStorageObserver {
    private NullEntryStorageObserver() {
    }

    @Override
    public void onUpdateEntry(final ISchedulerEntry entry) throws SchedulerEntryStorageObserverException {

    }

    @Override
    public void onCancelEntry(final ISchedulerEntry entry) throws SchedulerEntryStorageObserverException {

    }

    /** Single instance of {@link NullEntryStorageObserver} */
    public static final ISchedulerEntryStorageObserver INSTANCE = new NullEntryStorageObserver();
}
