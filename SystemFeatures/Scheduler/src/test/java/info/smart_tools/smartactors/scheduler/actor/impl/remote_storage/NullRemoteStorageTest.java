package info.smart_tools.smartactors.scheduler.actor.impl.remote_storage;

import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Test for {@link NullRemoteStorage}.
 */
public class NullRemoteStorageTest {
    @Test(expected = EntryStorageAccessException.class)
    public void Should_throwWhenSaveMethodCalled()
            throws Exception {
        NullRemoteStorage.INSTANCE.saveEntry(mock(ISchedulerEntry.class));
    }

    @Test
    public void Should_doNothingWhenDeleteOrDownloadMethodCalled()
            throws Exception {
        ISchedulerEntry entry = mock(ISchedulerEntry.class);
        ISchedulerEntryStorage storage = mock(ISchedulerEntryStorage.class);

        NullRemoteStorage.INSTANCE.deleteEntry(entry);

        verifyNoMoreInteractions(entry, storage);
    }
}
