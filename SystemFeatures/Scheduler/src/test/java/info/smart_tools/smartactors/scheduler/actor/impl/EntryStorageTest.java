package info.smart_tools.smartactors.scheduler.actor.impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.ipool_guard.IPoolGuard;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.pool_guard.PoolGuard;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.actor.impl.filter.AllPassEntryFilter;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.DatabaseRemoteStorage;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.IRemoteEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryNotFoundException;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for {@link EntryStorage}.
 */
@Ignore("Need rewrite tests w/o loading other plugins")
public class EntryStorageTest extends IOCInitializer {
    private IPool connectionPool;
    private IStrategy restoreEntryStrategy;
    private IObject[] saved;
    private ISchedulerEntry[] entries;
    private IRemoteEntryStorage remoteEntryStorage;
    private ITimer timerMock;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

//        load(PluginInMemoryDatabase.class);
//        load(PluginInMemoryDBTasks.class);

    @Override
    protected void registerMocks() throws Exception {
//        connectionPool = IOC.resolve(Keys.getKeyByName("DatabaseConnectionPool"));
        connectionPool = mock(IPool.class);

        try (IPoolGuard guard = new PoolGuard(connectionPool)) {
            ITask createTask = IOC.resolve(
                    Keys.getKeyByName("db.collection.create"),
                    guard.getObject(),
                    "scheduler_collection",
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));

            createTask.execute();

            saved = new IObject[] {
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                            "{'strategy':'strategy1','entryId':'0'}".replace('\'', '"')),
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                            "{'strategy':'strategy2','entryId':'1'}".replace('\'', '"')),
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                            "{'strategy':'strategy3','entryId':'2'}".replace('\'', '"')),
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                            "{'strategy':'strategy4','entryId':'3'}".replace('\'', '"')),
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                            "{'strategy':'strategy5','entryId':'4'}".replace('\'', '"')),
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                            "{'strategy':'strategy6','entryId':'5'}".replace('\'', '"')),
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                            "{'strategy':'strategy7','entryId':'6'}".replace('\'', '"')),
            };

            entries = new ISchedulerEntry[saved.length + 2];

            for (int i = 0; i < entries.length; i++) {
                entries[i] = mock(ISchedulerEntry.class);
                when(entries[i].getId()).thenReturn(String.valueOf(i));
                when(entries[i].getState()).thenReturn(i < saved.length ? saved[i] : mock(IObject.class));
                when(entries[i].isAwake()).thenReturn(true);
            }

            for (IObject obj : saved) {
                ITask task = IOC.resolve(
                        Keys.getKeyByName("db.collection.insert"),
                        guard.getObject(),
                        "scheduler_collection",
                        obj);

                task.execute();
            }

            remoteEntryStorage = new DatabaseRemoteStorage(connectionPool, "scheduler_collection");
        }

        restoreEntryStrategy = mock(IStrategy.class);
        when(restoreEntryStrategy.resolve(any(), any())).thenReturn(entries[0], Arrays.copyOfRange(entries, 1, entries.length));
        IOC.register(Keys.getKeyByName("restore scheduler entry"), restoreEntryStrategy);

        timerMock = mock(ITimer.class);
        IOC.register(Keys.getKeyByName("timer"), new SingletonStrategy(timerMock));
    }

    private int countDBEntries() throws Exception {
        int[] res = new int[] {-1};

        try (IPoolGuard guard = new PoolGuard(connectionPool)) {
            ITask createTask = IOC.resolve(
                    Keys.getKeyByName("db.collection.count"),
                    guard.getObject(),
                    "scheduler_collection",
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{'filter':{},'sort':[]}".replace('\'','"')),
                    (IAction<Long>) c -> res[0] = c.intValue());

            createTask.execute();
        }

        return res[0];
    }

    @Test
    public void Should_locallyStoreEntries()
            throws Exception {
        EntryStorage storage = new EntryStorage(remoteEntryStorage, null);

        storage.notifyActive(entries[0]);
        storage.notifyActive(entries[1]);
        storage.notifyActive(entries[2]);

        assertSame(entries[1], storage.getEntry("1"));
    }

    @Test
    public void Should_cancelOldEntryWhenItIsOverriddenByNewOne()
            throws Exception {
        when(entries[1].getId()).thenReturn("0");

        EntryStorage storage = new EntryStorage(remoteEntryStorage, null);

        storage.notifyActive(entries[0]);
        storage.notifyActive(entries[1]);

        verify(entries[0]).cancel();
        assertSame(entries[1], storage.getEntry("0"));
    }

    @Test
    public void Should_saveEntryInDatabase()
            throws Exception {
        int iCnt = countDBEntries();

        EntryStorage storage = new EntryStorage(remoteEntryStorage, null);

        storage.save(entries[saved.length]);

        assertEquals(1, countDBEntries() - iCnt);
    }

    @Test
    public void Should_deleteEntryFromBothDBAndMemory()
            throws Exception {
        int iCnt = countDBEntries();

        EntryStorage storage = new EntryStorage(remoteEntryStorage, null);

        storage.notifyActive(entries[1]);

        assertEquals(entries[1], storage.getEntry("1"));

        storage.delete(entries[1]);

        try {
            assertNull(storage.getEntry("1"));
            fail();
        } catch (EntryNotFoundException e) {}

        assertEquals(-1, countDBEntries()-iCnt);
    }

    @Test
    public void Should_enumerateAllEntries()
            throws Exception {
        EntryStorage storage = new EntryStorage(remoteEntryStorage, null);

        storage.notifyActive(entries[1]);
        storage.notifyActive(entries[2]);
        storage.notifyActive(entries[3]);
        storage.notifyActive(entries[4]);

        storage.notifyInactive(entries[3], true);
        storage.notifyInactive(entries[4], false);

        assertEquals(new HashSet<>(Arrays.asList(entries[1], entries[2], entries[3])), new HashSet<>(storage.listLocalEntries()));
    }

    @Test
    public void Should_refreshEntryActivity()
            throws Exception {
        when(entries[0].getLastTime()).thenReturn(0L);
        when(entries[1].getLastTime()).thenReturn(100L);
        when(entries[2].getLastTime()).thenReturn(200L);
        when(entries[3].getLastTime()).thenReturn(300L);
        when(entries[4].getLastTime()).thenReturn(400L);
        when(entries[5].getLastTime()).thenReturn(500L);
        when(entries[6].getLastTime()).thenReturn(600L);
        when(entries[7].getLastTime()).thenReturn(0L);

        when(entries[7].isAwake()).thenReturn(false);

        EntryStorage storage = new EntryStorage(remoteEntryStorage, null);

        storage.notifyActive(entries[7]);
        storage.notifyActive(entries[0]);
        storage.notifyInactive(entries[1], true);
        storage.notifyActive(entries[2]);
        storage.notifyInactive(entries[3], true);
        storage.notifyActive(entries[4]);
        storage.notifyInactive(entries[5], true);
        storage.notifyInactive(entries[6], false);

        storage.refresh(150L, 350L);

        verify(entries[7], times(1)).awake();
        verify(entries[7], times(0)).suspend();

        verify(entries[0], times(0)).awake();
        verify(entries[0], times(0)).suspend();

        verify(entries[1], times(1)).awake();
        verify(entries[1], times(0)).suspend();

        verify(entries[2], times(0)).awake();
        verify(entries[2], times(0)).suspend();

        verify(entries[3], times(0)).awake();
        verify(entries[3], times(0)).suspend();

        verify(entries[4], times(0)).awake();
        verify(entries[4], times(1)).suspend();

        verify(entries[5], times(0)).awake();
        verify(entries[5], times(0)).suspend();

        verify(entries[6], times(0)).awake();
        verify(entries[6], times(0)).suspend();
        verify(entries[6], times(0)).getLastTime();
    }

    @Test
    public void Should_restoreRemoteEntryWhenItIsRequired()
            throws Exception {
        EntryStorage storage = new EntryStorage(remoteEntryStorage, null);

        assertSame(entries[0], storage.getEntry("0"));
    }

    @Test(expected = EntryNotFoundException.class)
    public void Should_throwWhenRequiredEntryIsNotFoundInBothRemoteAndLocalStorage()
            throws Exception {
        EntryStorage storage = new EntryStorage(remoteEntryStorage, null);

        assertNull(storage.getEntry("666"));
    }

    @Test
    public void Should_storeTimer()
            throws Exception {
        assertSame(timerMock, new EntryStorage(remoteEntryStorage, null).getTimer());
    }

    @Test
    public void Should_storeFilterReference()
            throws Exception {
        EntryStorage storage = new EntryStorage(remoteEntryStorage, null);

        assertTrue(storage.getFilter() instanceof AllPassEntryFilter);

        ISchedulerEntryFilter filterMock = mock(ISchedulerEntryFilter.class);

        storage.setFilter(filterMock);

        assertSame(filterMock, storage.getFilter());

        try {
            storage.setFilter(null);
            fail();
        } catch (InvalidArgumentException ignore) { }

        assertSame(filterMock, storage.getFilter());
    }
}
