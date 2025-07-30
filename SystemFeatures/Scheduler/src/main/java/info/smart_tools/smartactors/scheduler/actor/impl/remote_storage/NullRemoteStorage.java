package info.smart_tools.smartactors.scheduler.actor.impl.remote_storage;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link IRemoteEntryStorage remote entry storage} that does not actually store entries.
 */
public final class NullRemoteStorage implements IRemoteEntryStorage {
    private NullRemoteStorage() {
    }

    @Override
    public void saveEntry(final ISchedulerEntry entry) throws EntryStorageAccessException {
        throw new EntryStorageAccessException("Entry saving not supported by this entry storage.");
    }

    @Override
    public void deleteEntry(final ISchedulerEntry entry) throws EntryStorageAccessException {
        //do nothing
    }

    @Override
    public IObject querySingleEntry(final String id) throws EntryStorageAccessException {
        throw new EntryStorageAccessException("Entry not found.");
    }

    @Override
    public List<IObject> downloadEntries(final long untilTime, final IObject lastSkip, int pageSize) throws EntryStorageAccessException {
        return Collections.emptyList();
    }

    @Override
    public void weakSaveEntry(final ISchedulerEntry entry) throws EntryStorageAccessException {
        //do nothing
    }

    /**
     * The single instance.
     */
    public static final NullRemoteStorage INSTANCE = new NullRemoteStorage();
}
