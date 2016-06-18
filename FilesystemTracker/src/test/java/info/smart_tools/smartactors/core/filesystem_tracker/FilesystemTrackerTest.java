package info.smart_tools.smartactors.core.filesystem_tracker;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ifilesystem_tracker.exception.FilesystemTrackerStartupException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FilesystemTrackerTest {
    private FilenameFilter filenameFilterMock;
    private ListeningTaskFactory taskFactoryMock;
    private Runnable runnableMock;
    private File directoryMock;

    @Before
    public void setUp()
            throws Exception {
        filenameFilterMock = mock(FilenameFilter.class);
        taskFactoryMock = mock(ListeningTaskFactory.class);
        runnableMock = mock(Runnable.class);
        directoryMock = mock(File.class);

        when(taskFactoryMock.createRunnable(any(), any())).thenReturn(runnableMock);
        when(directoryMock.isDirectory()).thenReturn(true);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_NullIsPassedAsFilenameFilter()
            throws Exception {
        new FilesystemTracker(null, taskFactoryMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_NullIsPassedAsTaskFactory()
            throws Exception {
        new FilesystemTracker(filenameFilterMock, null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throw_When_directoryIsNotDirectory()
            throws Exception {
        when(directoryMock.isDirectory()).thenReturn(false);

        FilesystemTracker tracker = new FilesystemTracker(filenameFilterMock, taskFactoryMock);

        tracker.start(directoryMock);
    }

    @Test(expected = FilesystemTrackerStartupException.class)
    public void Should_throw_When_startCalledTwice()
            throws Exception {
        FilesystemTracker tracker = new FilesystemTracker(filenameFilterMock, taskFactoryMock);

        try {
            tracker.start(directoryMock);

            verify(runnableMock, timeout(1000)).run();
        } catch (FilesystemTrackerStartupException e) {
            fail();
        }

        tracker.start(directoryMock);
    }

    @Test(expected = FilesystemTrackerStartupException.class)
    public void Should_throw_When_taskFactoryThrows()
            throws Exception {
        FilesystemTracker tracker = new FilesystemTracker(filenameFilterMock, taskFactoryMock);

        when(taskFactoryMock.createRunnable(any(), any())).thenThrow(new IOException());

        tracker.start(directoryMock);
    }

    @Test
    public void Should_executionOfActionPassedToTaskFactoryCauseExecutionOfAddedActions()
            throws Exception {
        File[] fileMocks = new File[]{mock(File.class),mock(File.class), mock(File.class)};
        IAction<File> fileActionMock = mock(IAction.class);

        when(filenameFilterMock.accept(any(),any())).thenReturn(true);

        FilesystemTracker tracker = new FilesystemTracker(filenameFilterMock, taskFactoryMock);
        tracker.start(directoryMock);

        ArgumentCaptor<IAction> actionCaptor = ArgumentCaptor.forClass(IAction.class);

        verify(taskFactoryMock).createRunnable(same(directoryMock), actionCaptor.capture());

        IAction<File> capturedAction = actionCaptor.getValue();

        capturedAction.execute(fileMocks[0]);
        tracker.addFileHandler(fileActionMock);
        verify(fileActionMock).execute(same(fileMocks[0]));

        capturedAction.execute(fileMocks[1]);
        capturedAction.execute(fileMocks[1]);
        verify(fileActionMock, times(1)).execute(same(fileMocks[1]));

        tracker.removeFileHandler(fileActionMock);
        capturedAction.execute(fileMocks[2]);
        verify(fileActionMock, never()).execute(same(fileMocks[2]));
    }

    @Test
    public void Should_notBeTrackedFileThatDoesNotMatchFilter()
            throws Exception {
        File fileMock = mock(File.class);
        IAction<File> fileActionMock = mock(IAction.class);

        when(filenameFilterMock.accept(any(),any())).thenReturn(false);

        FilesystemTracker tracker = new FilesystemTracker(filenameFilterMock, taskFactoryMock);
        tracker.start(directoryMock);

        ArgumentCaptor<IAction> actionCaptor = ArgumentCaptor.forClass(IAction.class);

        verify(taskFactoryMock).createRunnable(same(directoryMock), actionCaptor.capture());

        IAction<File> capturedAction = actionCaptor.getValue();

        capturedAction.execute(fileMock);
        tracker.addFileHandler(fileActionMock);
        verifyZeroInteractions(fileActionMock);

        capturedAction.execute(fileMock);
        verifyZeroInteractions(fileActionMock);
    }

    @Test
    public void Should_exceptionInFileActionCauseInvocationOfErrorAction()
            throws Exception {
        File fileMock = mock(File.class);
        IAction<File> fileActionMock = mock(IAction.class);
        IAction<Throwable> errorActionMock = mock(IAction.class);
        ActionExecuteException exceptionMock = mock(ActionExecuteException.class);

        when(filenameFilterMock.accept(any(),any())).thenReturn(true);
        doThrow(exceptionMock).when(fileActionMock).execute(same(fileMock));

        FilesystemTracker tracker = new FilesystemTracker(filenameFilterMock, taskFactoryMock);
        tracker.start(directoryMock);

        ArgumentCaptor<IAction> actionCaptor = ArgumentCaptor.forClass(IAction.class);

        verify(taskFactoryMock).createRunnable(same(directoryMock), actionCaptor.capture());

        IAction<File> capturedAction = actionCaptor.getValue();

        tracker.addErrorHandler(errorActionMock);
        tracker.addFileHandler(fileActionMock);
        capturedAction.execute(fileMock);
        verify(errorActionMock).execute(same(exceptionMock));
    }
}
