package info.smart_tools.smartactors.task.thread_pool;

import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ThreadImpl}.
 */
public class ThreadImplTest {
    ThreadPool threadPoolMock;
    ThreadImpl thread;
    IModule module;

    @Before
    public void setUp()
            throws Exception {
        threadPoolMock = mock(ThreadPool.class);
        thread = new ThreadImpl(threadPoolMock, "TestThread");
        module = mock(IModule.class);
        IScope scope = mock(IScope.class);

        when(threadPoolMock.getModule()).thenReturn(module);
        when(threadPoolMock.getScope()).thenReturn(scope);
        when(module.getName()).thenReturn("moduleName");
        when(module.getVersion()).thenReturn("moduleVersion");
        ModuleManager.setCurrentModule(module);
    }

    @After
    public void tearDown()
            throws Exception {
        if (null != thread) {
            thread.interrupt();
        }
    }

    @Test
    public void Should_executeTaskInSeparateThread()
            throws Exception {
        ITask taskMock = mock(ITask.class);

        thread.execute(taskMock);

        verify(taskMock, timeout(1000)).execute();
    }

    @Test(expected = TaskExecutionException.class)
    @Ignore("Test execution is depending on server technical characteristics.")
    // ToDo: Need to rewrite.
    public void Should_throwWhenAnotherTaskIsBeingExecuted()
            throws Exception {
        thread.execute(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        });

        thread.execute(mock(ITask.class));
    }

    @Test(expected = TaskExecutionException.class)
    @Ignore("Test execution is depending on server technical characteristics.")
    // ToDo: Need to rewrite.
    public void Should_throwWhenThreadIsNotAlive()
            throws Exception {
        ITask taskMock = mock(ITask.class);
        thread.interrupt();
        thread.execute(taskMock);
        fail();
    }

    @Test
    @Ignore("Test execution is depending on server technical characteristics.")
    // ToDo: Need to rewrite.
    public void Should_ignoreExceptionsFromTask()
            throws Exception {
        ITask taskMock1 = mock(ITask.class), taskMock2 = mock(ITask.class);

        doThrow(new TaskExecutionException("Whoops!")).when(taskMock1).execute();

        thread.execute(taskMock1);
        verify(taskMock1, timeout(100)).execute();
        Thread.sleep(200);

        thread.execute(taskMock2);
        verify(taskMock2, timeout(100)).execute();    }
}
