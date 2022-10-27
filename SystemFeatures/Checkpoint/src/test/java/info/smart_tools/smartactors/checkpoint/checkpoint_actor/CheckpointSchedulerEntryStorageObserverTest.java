package info.smart_tools.smartactors.checkpoint.checkpoint_actor;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * Test for {@link CheckpointSchedulerEntryStorageObserver}.
 */
public class CheckpointSchedulerEntryStorageObserverTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    private ISchedulerEntry em(final String id) throws Exception {
        IObject state = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                String.format("{'prevCheckpointEntryId':'%s'}", id).replace('\'','"'));

        ISchedulerEntry entry = mock(ISchedulerEntry.class);

        when(entry.getState()).thenReturn(state);

        return entry;
    }

    @Test
    public void Should_updateTableWhenEntryUpdated()
            throws Exception {
        CheckpointSchedulerEntryStorageObserver observer = new CheckpointSchedulerEntryStorageObserver();

        ISchedulerEntry e1 = em("1");

        observer.onUpdateEntry(e1);

        assertSame(e1, observer.getPresentEntry("1"));
    }

    @Test
    public void Should_cancelDuplicateEntries()
            throws Exception {
        CheckpointSchedulerEntryStorageObserver observer = new CheckpointSchedulerEntryStorageObserver();

        ISchedulerEntry e11 = em("1");
        ISchedulerEntry e12 = em("1");

        observer.onUpdateEntry(e11);
        observer.onUpdateEntry(e12);

        assertSame(e12, observer.getPresentEntry("1"));
        verify(e11).cancel();
    }

    @Test
    public void Should_dropEntryFromTableWhenItIsCancelled()
            throws Exception {
        CheckpointSchedulerEntryStorageObserver observer = new CheckpointSchedulerEntryStorageObserver();

        ISchedulerEntry e1 = em("1");

        observer.onUpdateEntry(e1);
        observer.onCancelEntry(e1);

        assertNull(observer.getPresentEntry("1"));
    }
}
