package info.smart_tools.smartactors.checkpoint.checkpoint_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorageObserver;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerEntryStorageObserverException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * {@link ISchedulerEntryStorageObserver Entry storage observer} that updates map from id of previous checkpoint entry id to id of current
 * checkpoint's entry.
 */
public class CheckpointSchedulerEntryStorageObserver implements ISchedulerEntryStorageObserver {
    private final Map<String, ISchedulerEntry> receivedMessageEntries = new ConcurrentHashMap<>();

    private final IFieldName prevCheckpointEntryIdFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if resolution of any dependency fails
     */
    public CheckpointSchedulerEntryStorageObserver()
            throws ResolutionException {
        prevCheckpointEntryIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointEntryId");
    }

    @Override
    public void onUpdateEntry(final ISchedulerEntry entry)
            throws SchedulerEntryStorageObserverException {
        try {
            Object prevEntryId = entry.getState().getValue(prevCheckpointEntryIdFieldName);

            if (prevEntryId != null) {
                ISchedulerEntry prev = receivedMessageEntries.put(prevEntryId.toString(), entry);

                if (prev != null && prev != entry) {
                    prev.cancel();
                }
            }
        } catch (ReadValueException | InvalidArgumentException | EntryStorageAccessException | EntryScheduleException e) {
            throw new SchedulerEntryStorageObserverException("Error occurred updating received messages index.", e);
        }
    }

    @Override
    public void onCancelEntry(final ISchedulerEntry entry) throws SchedulerEntryStorageObserverException {
        try {
            Object prevEntryId = entry.getState().getValue(prevCheckpointEntryIdFieldName);

            if (prevEntryId != null) {
                receivedMessageEntries.remove(prevEntryId, entry);
            }
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new SchedulerEntryStorageObserverException("Error occurred updating received messages index.", e);
        }
    }

    /**
     * Get present entry of current checkpoint by id of entry of previous checkpoint.
     * @param prevCpEntryId    identifier of entry of previous checkpoint
     * @return the entry or {@code null} if there is no such entry
     */
    public ISchedulerEntry getPresentEntry(final String prevCpEntryId) {
        return receivedMessageEntries.get(prevCpEntryId);
    }
}
