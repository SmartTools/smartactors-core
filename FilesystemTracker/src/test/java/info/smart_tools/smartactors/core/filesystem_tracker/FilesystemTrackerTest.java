package info.smart_tools.smartactors.core.filesystem_tracker;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ipath.IPath;
import info.smart_tools.smartactors.core.ipath.IPathFilter;
import info.smart_tools.smartactors.core.ifilesystem_tracker.exception.FilesystemTrackerStartupException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FilesystemTrackerTest {
    private IPathFilter pathFilterMock;
    private ListeningTaskFactory taskFactoryMock;
    private Runnable runnableMock;
    private IPath directoryMock;
    private Path directoryPathMock;
    private FileSystem fileSystemMock;
    private FileSystemProvider fileSystemProviderMock;
    private BasicFileAttributes directoryPathAttributesMock;

    @Before
    public void setUp()
            throws Exception {
        pathFilterMock = mock(IPathFilter.class);
        taskFactoryMock = mock(ListeningTaskFactory.class);
        runnableMock = mock(Runnable.class);

        directoryMock = mock(IPath.class);
        fileSystemMock = mock(FileSystem.class);

        fileSystemProviderMock = mock(FileSystemProvider.class);
        directoryPathMock = mock(Path.class);
        when(directoryPathMock.getFileSystem()).thenReturn(fileSystemMock);

        when(fileSystemMock.provider()).thenReturn(fileSystemProviderMock);
        when(fileSystemMock.getPath(any())).thenReturn(directoryPathMock);
        when(fileSystemProviderMock.getPath(any())).thenReturn(directoryPathMock);

        directoryPathAttributesMock = mock(BasicFileAttributes.class);
        when(directoryPathAttributesMock.isDirectory()).thenReturn(true);
        when(fileSystemProviderMock.readAttributes(directoryPathMock, BasicFileAttributes.class)).thenReturn(directoryPathAttributesMock);

        when(taskFactoryMock.createRunnable(any(), any())).thenReturn(runnableMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_NullIsPassedAsFilenameFilter()
            throws Exception {
        new FilesystemTracker(null, taskFactoryMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_NullIsPassedAsTaskFactory()
            throws Exception {
        new FilesystemTracker(pathFilterMock, null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throw_When_directoryIsNotDirectory()
            throws Exception {
        when(directoryPathAttributesMock.isDirectory()).thenReturn(false);

        FilesystemTracker tracker = new FilesystemTracker(pathFilterMock, taskFactoryMock, fileSystemMock);

        tracker.start(directoryMock);
    }

    @Test(expected = FilesystemTrackerStartupException.class)
    public void Should_throw_When_startCalledTwice()
            throws Exception {
        FilesystemTracker tracker = new FilesystemTracker(pathFilterMock, taskFactoryMock, fileSystemMock);

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
        FilesystemTracker tracker = new FilesystemTracker(pathFilterMock, taskFactoryMock, fileSystemMock);

        when(taskFactoryMock.createRunnable(any(), any())).thenThrow(new IOException());

        tracker.start(directoryMock);
    }

    @Test
    public void Should_executionOfActionPassedToTaskFactoryCauseExecutionOfAddedActions()
            throws Exception {
        IPath[] fileMocks = new IPath[]{mock(IPath.class),mock(IPath.class), mock(IPath.class)};
        IAction<IPath> fileActionMock = mock(IAction.class);

        when(pathFilterMock.accept(any())).thenReturn(true);

        FilesystemTracker tracker = new FilesystemTracker(pathFilterMock, taskFactoryMock, fileSystemMock);
        tracker.start(directoryMock);

        ArgumentCaptor<IAction> actionCaptor = ArgumentCaptor.forClass(IAction.class);

        verify(taskFactoryMock).createRunnable(same(directoryMock), actionCaptor.capture());

        IAction<IPath> capturedAction = actionCaptor.getValue();

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
        IPath fileMock = mock(IPath.class);
        IAction<IPath> fileActionMock = mock(IAction.class);

        when(pathFilterMock.accept(any())).thenReturn(false);

        FilesystemTracker tracker = new FilesystemTracker(pathFilterMock, taskFactoryMock, fileSystemMock);
        tracker.start(directoryMock);

        ArgumentCaptor<IAction> actionCaptor = ArgumentCaptor.forClass(IAction.class);

        verify(taskFactoryMock).createRunnable(same(directoryMock), actionCaptor.capture());

        IAction<IPath> capturedAction = actionCaptor.getValue();

        capturedAction.execute(fileMock);
        tracker.addFileHandler(fileActionMock);
        verifyZeroInteractions(fileActionMock);

        capturedAction.execute(fileMock);
        verifyZeroInteractions(fileActionMock);
    }

    @Test
    public void Should_exceptionInFileActionCauseInvocationOfErrorAction()
            throws Exception {
        IPath fileMock = mock(IPath.class);
        IAction<IPath> fileActionMock = mock(IAction.class);
        IAction<Throwable> errorActionMock = mock(IAction.class);
        ActionExecuteException exceptionMock = mock(ActionExecuteException.class);

        when(pathFilterMock.accept(any())).thenReturn(true);
        doThrow(exceptionMock).when(fileActionMock).execute(same(fileMock));

        FilesystemTracker tracker = new FilesystemTracker(pathFilterMock, taskFactoryMock, fileSystemMock);
        tracker.start(directoryMock);

        ArgumentCaptor<IAction> actionCaptor = ArgumentCaptor.forClass(IAction.class);

        verify(taskFactoryMock).createRunnable(same(directoryMock), actionCaptor.capture());

        IAction<IPath> capturedAction = actionCaptor.getValue();

        tracker.addErrorHandler(errorActionMock);
        tracker.addFileHandler(fileActionMock);
        capturedAction.execute(fileMock);
        verify(errorActionMock).execute(same(exceptionMock));
    }
}
