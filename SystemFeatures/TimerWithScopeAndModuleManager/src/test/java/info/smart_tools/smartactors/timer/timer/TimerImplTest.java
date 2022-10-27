package info.smart_tools.smartactors.timer.timer;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test for {@link TimerImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class TimerImplTest {
    private static Timer realTimer;
    private Timer timerMock;
    private ITask taskMock;
    private IQueue<ITask> taskQueueMock;
    private IKey taskQueueKey;

    @BeforeClass
    public static void setUpClass()
            throws Exception {
        realTimer = new Timer(true);
    }

    @AfterClass
    public static void shutDownClass()
            throws Exception {
        realTimer.cancel();
    }

    @Before
    public void setUp()
            throws Exception {
        mockStatic(IOC.class, Keys.class);

        timerMock = mock(Timer.class);
        taskMock = mock(ITask.class);
        taskQueueMock = mock(IQueue.class);
        taskQueueKey =mock(IKey.class);

        ScopeProvider.setCurrentScope(mock(IScope.class));
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));

        when(Keys.getKeyByName(eq("task_queue"))).thenReturn(taskQueueKey);
        when(IOC.resolve(same(taskQueueKey))).thenReturn(taskQueueMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenTimerIsNull()
            throws Exception {
        assertNotNull(new TimerImpl(null));
    }

    @Test
    public void Should_resolveQueueSchedulingTask()
            throws Exception {
        ITimer timer = new TimerImpl(timerMock);

        long tm = System.currentTimeMillis() + 1337;

        timer.schedule(taskMock, tm);

        verifyStatic();
        IOC.resolve(same(taskQueueKey));

        ArgumentCaptor<TimerTask> timerTaskArgumentCaptor = ArgumentCaptor.forClass(TimerTask.class);
        ArgumentCaptor<Long> timeArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(timerMock).schedule(timerTaskArgumentCaptor.capture(), timeArgumentCaptor.capture());
        assertTrue(1000L > Math.abs(1337 - timeArgumentCaptor.getValue()));

        timerTaskArgumentCaptor.getValue().run();

        verify(taskQueueMock).put(same(taskMock));
    }

    @Test
    public void Should_javaTimerTaskImplementationHandleInterruptedException()
            throws Exception {
        doThrow(InterruptedException.class).when(taskQueueMock).put(same(taskMock));
        ITimer timer = new TimerImpl(timerMock);

        timer.schedule(taskMock, 0);

        ArgumentCaptor<TimerTask> timerTaskArgumentCaptor = ArgumentCaptor.forClass(TimerTask.class);
        verify(timerMock).schedule(timerTaskArgumentCaptor.capture(), anyLong());

        assertFalse(Thread.interrupted());
        timerTaskArgumentCaptor.getValue().run();
        assertTrue(Thread.interrupted());
    }

    @Test(expected = TaskScheduleException.class)
    public void Should_throwWhenCannotResolveQueue()
            throws Exception {
        when(IOC.resolve(same(taskQueueKey))).thenThrow(ResolutionException.class);
        ITimer timer = new TimerImpl(timerMock);

        timer.schedule(taskMock, 0);
    }

    @Test
    public void Should_cancelCancelTheTask()
            throws Exception {
        ITimer timer = new TimerImpl(realTimer);
        timer.schedule(taskMock, System.currentTimeMillis() + 500).cancel();
        Thread.sleep(500);
        verify(taskQueueMock, times(0)).put(any());
    }
}
