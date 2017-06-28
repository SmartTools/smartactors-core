package info.smart_tools.smartactors.scheduler.actor.impl.timer;

import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITime;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimerTask;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link SchedulerTimer}.
 */
public class SchedulerTimerTest extends PluginsLoadingTestBase {
    private ITime timeMock;
    private ITimer timerMock;
    private ITask[] taskMock;
    private ITimerTask[] timerTaskMock;

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
        timeMock = mock(ITime.class);
        IOC.register(Keys.getOrAdd("time"), new SingletonStrategy(timeMock));

        timerMock = mock(ITimer.class);

        taskMock = new ITask[4];
        for (int i = 0; i < taskMock.length; i++) {
            taskMock[i] = mock(ITask.class);
        }

        timerTaskMock = new ITimerTask[4];
        for (int i = 0; i < taskMock.length; i++) {
            timerTaskMock[i] = mock(ITimerTask.class);
        }
    }

    @Test
    public void Should_notScheduleTasksWhenStopped()
            throws Exception {
        SchedulerTimer timer = new SchedulerTimer(timerMock);

        timer.schedule(taskMock[0], 100500);
        verifyNoMoreInteractions(timerMock);

        timer.startAfter(1337);

        timer.schedule(taskMock[1], 666);
        verifyNoMoreInteractions(timerMock);

        timer.stopAfter(20000);

        timer.schedule(taskMock[2], 20001);
        verifyNoMoreInteractions(timerMock);

        timer.stop();
        timer.start();

        timer.schedule(taskMock[3], 100);
        verifyNoMoreInteractions(timerMock);
    }

    @Test
    public void Should_cancelUnderlyingEntry()
            throws Exception {
        SchedulerTimer timer = new SchedulerTimer(timerMock);
        timer.start();

        when(timerMock.schedule(any(), eq(100500L))).thenReturn(timerTaskMock[0]);

        timer.schedule(taskMock[0], 100500L).cancel();

        verify(timerTaskMock[0]).cancel();
    }

    @Test
    public void Should_rescheduleUnderlyingEntry()
            throws Exception {
        SchedulerTimer timer = new SchedulerTimer(timerMock);
        timer.start();

        when(timerMock.schedule(any(), eq(100500L))).thenReturn(timerTaskMock[0]);

        timer.schedule(taskMock[0], 100500L).reschedule(100600L);

        verify(timerTaskMock[0]).reschedule(eq(100600L));
    }

    @Test
    public void Should_cancelUnderlyingEntryWhenItIsRescheduledToTimeAfterStop()
            throws Exception {
        SchedulerTimer timer = new SchedulerTimer(timerMock);
        timer.start();

        when(timerMock.schedule(any(), eq(100500L))).thenReturn(timerTaskMock[0]);

        ITimerTask tt = timer.schedule(taskMock[0], 100500L);

        timer.stopAfter(100550L);

        tt.reschedule(100600L);

        verify(timerTaskMock[0]).cancel();
    }

    @Test
    public void Should_executeUnderlyingTask()
            throws Exception {
        SchedulerTimer timer = new SchedulerTimer(timerMock);
        timer.start();

        ArgumentCaptor<ITask> taskArgumentCaptor = ArgumentCaptor.forClass(ITask.class);

        when(timerMock.schedule(taskArgumentCaptor.capture(), eq(100500L))).thenReturn(timerTaskMock[0]);

        timer.schedule(taskMock[0], 100500L);

        when(timeMock.currentTimeMillis()).thenReturn(100500L);

        taskArgumentCaptor.getValue().execute();

        verify(taskMock[0]).execute();
    }

    @Test
    public void Should_notExecuteUnderlyingTaskWhenTaskIsExecutedAfterStopTime()
            throws Exception {
        SchedulerTimer timer = new SchedulerTimer(timerMock);
        timer.start();

        ArgumentCaptor<ITask> taskArgumentCaptor = ArgumentCaptor.forClass(ITask.class);

        when(timerMock.schedule(taskArgumentCaptor.capture(), eq(100600L))).thenReturn(timerTaskMock[0]);

        timer.schedule(taskMock[0], 100600L);

        timer.stopAfter(100500L);

        when(timeMock.currentTimeMillis()).thenReturn(100610L);

        taskArgumentCaptor.getValue().execute();

        verifyNoMoreInteractions(taskMock[0]);
    }
}
