package info.smart_tools.smartactors.scheduler.actor.impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scheduler.actor.impl.refresher.EntryStorageRefresher;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.IRemoteEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITime;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimerTask;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test for {@link EntryStorageRefresher}.
 */
public class EntryStorageRefresherTest extends PluginsLoadingTestBase {
    private IQueue<ITask> taskQueueMock;
    private ITimer timerMock;
    private ITime timeMock;
    private ITimerTask timerTaskMock;
    private EntryStorage storageMock;
    private IRemoteEntryStorage remoteStorageMock;
    private IResolveDependencyStrategy restoreEntryStrategy;
    private ArgumentCaptor<ITask> taskArgumentCaptor;
    private ArgumentCaptor<Long> timeArgumentCaptor;

    private ISchedulerEntry entries[];
    private IObject states[];

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks() throws Exception {
        taskQueueMock = mock(IQueue.class);
        timerMock = mock(ITimer.class);
        timeMock = mock(ITime.class);
        timerTaskMock = mock(ITimerTask.class);
        storageMock = mock(EntryStorage.class);
        remoteStorageMock = mock(IRemoteEntryStorage.class);
        restoreEntryStrategy = mock(IResolveDependencyStrategy.class);

        taskArgumentCaptor = ArgumentCaptor.forClass(ITask.class);
        timeArgumentCaptor = ArgumentCaptor.forClass(long.class);

        when(timerMock.schedule(any(), anyLong())).thenReturn(timerTaskMock).thenThrow(TaskScheduleException.class);

        entries = new ISchedulerEntry[5];
        states = new IObject[entries.length];

        for (int i = 0; i < entries.length; i++) {
            entries[i] = mock(ISchedulerEntry.class);
            states[i] = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                    String.format("{'entryId':'entry-%010d'}".replace('\'', '"'), i));
        }

        IOC.register(Keys.getOrAdd("timer"), new SingletonStrategy(timerMock));
        IOC.register(Keys.getOrAdd("time"), new SingletonStrategy(timeMock));
        IOC.register(Keys.getOrAdd("task_queue"), new SingletonStrategy(taskQueueMock));
        IOC.register(Keys.getOrAdd("restore scheduler entry"), restoreEntryStrategy);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowIfStorageIsNull()
            throws Exception {
        assertNull(new EntryStorageRefresher(null, remoteStorageMock, 1, 2, 3, 4));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowIfRemoteStorageIsNull()
            throws Exception {
        assertNull(new EntryStorageRefresher(storageMock, null, 1, 2, 3, 4));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowIfPageSizeIsNonPositive()
            throws Exception {
        assertNull(new EntryStorageRefresher(storageMock, remoteStorageMock, 1, 2, 3, 0));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowIfIntervalsAreInvalid_1()
            throws Exception {
        assertNull(new EntryStorageRefresher(storageMock, remoteStorageMock, 0, 2, 3, 100));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowIfIntervalsAreInvalid_2()
            throws Exception {
        assertNull(new EntryStorageRefresher(storageMock, remoteStorageMock, 2, 1, 3, 100));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowIfIntervalsAreInvalid_3()
            throws Exception {
        assertNull(new EntryStorageRefresher(storageMock, remoteStorageMock, 1, 3, 1, 100));
    }

    private ITask executeEnqueuedTask() throws Exception {
        taskArgumentCaptor.getAllValues().clear();
        verify(taskQueueMock).put(taskArgumentCaptor.capture());
        reset(taskQueueMock);
        taskArgumentCaptor.getValue().execute();
        return taskArgumentCaptor.getValue();
    }

    @Test
    public void Should_refreshLocalStorageStatePeriodically()
            throws Exception {
        when(timeMock.currentTimeMillis()).thenReturn(1337L);

        new EntryStorageRefresher(storageMock, remoteStorageMock, 1000, 1001, 1500, 100).start();

        verify(timerMock).schedule(taskArgumentCaptor.capture(), eq(1337L));

        ITask initTask = taskArgumentCaptor.getValue();

        initTask.execute();

        when(remoteStorageMock.downloadEntries(1337L + 1000, null, 100)).thenReturn(Arrays.asList(states));
        when(remoteStorageMock.downloadEntries(1337L + 1000, states[states.length - 1], 100)).thenReturn(Collections.emptyList());

        for (int i = 1; i < entries.length; i++) {
            when(storageMock.getLocalEntry(eq(String.format("entry-%010d", i)))).thenReturn(entries[i]);
        }

        when(restoreEntryStrategy.resolve(states[0], storageMock)).thenReturn(entries[0]);

        ITask downloadTask = executeEnqueuedTask();

        verify(restoreEntryStrategy).resolve(states[0], storageMock);
        verifyNoMoreInteractions(restoreEntryStrategy);
        verify(remoteStorageMock).weakSaveEntry(entries[0]);

        assertSame(downloadTask, executeEnqueuedTask());

        assertNotSame(downloadTask, executeEnqueuedTask());

        verify(storageMock).refresh(1337L + 1001, 1337L + 1500);
        verify(timerTaskMock).reschedule(1337L + 1000);
    }

    @Test
    public void Should_stopRefreshing()
            throws Exception {
        when(timeMock.currentTimeMillis()).thenReturn(1337L);

        EntryStorageRefresher refresher = new EntryStorageRefresher(storageMock, remoteStorageMock, 1000, 1001, 1500, 100);
        refresher.startAfter(1338);
        refresher.stopAfter(1449);

        verify(timerMock).schedule(taskArgumentCaptor.capture(), eq(1338L));
        taskArgumentCaptor.getValue().execute();
        taskArgumentCaptor.getAllValues().clear();

        executeEnqueuedTask();

        verify(remoteStorageMock).downloadEntries(eq(1449L), any(), anyInt());

        executeEnqueuedTask();
    }
}
