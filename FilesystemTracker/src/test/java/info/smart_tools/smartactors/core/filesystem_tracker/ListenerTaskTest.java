package info.smart_tools.smartactors.core.filesystem_tracker;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ipath.IPath;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.*;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ListenerTaskTest {
    private IAction<IPath> actionMock;
    private IPath directoryMock;
    private FileSystem fileSystemMock;
    private FileSystemProvider fileSystemProviderMock;
    private WatchService watchServiceMock;
    private WatchKey watchKeyMock;
    private WatchEvent event1Mock, event2Mock;
    private java.nio.file.Path directoryPathMock;

    @Before
    public void setUp()
            throws Exception {
        actionMock = mock(IAction.class);

        directoryMock = mock(IPath.class);
        directoryPathMock = mock(java.nio.file.Path.class);
        fileSystemMock = mock(FileSystem.class);
        fileSystemProviderMock = mock(FileSystemProvider.class);
        watchServiceMock = mock(WatchService.class);
        watchKeyMock = mock(WatchKey.class);
        event1Mock = mock(WatchEvent.class);
        event2Mock = mock(WatchEvent.class);

        when(directoryMock.getPath()).thenReturn("dir");
        when(directoryPathMock.getFileSystem()).thenReturn(fileSystemMock);
        when(fileSystemMock.getPath("dir")).thenReturn(directoryPathMock);
        when(fileSystemMock.provider()).thenReturn(fileSystemProviderMock);
        when(fileSystemMock.newWatchService()).thenReturn(watchServiceMock);
    }

    @Test
    public void Should_setupWatchServiceOnCreation()
            throws Exception {
        ListenerTask task = new ListenerTask(directoryMock, actionMock, fileSystemMock);

        verify(directoryPathMock).register(same(watchServiceMock), same(StandardWatchEventKinds.ENTRY_CREATE));
    }

    @Test
    public void Should_queryListOfExistFilesAndPollWatchServiceEvents()
            throws Exception {
        DirectoryStream directoryStreamMock = mock(DirectoryStream.class);
        Iterator directoryStreamIteratorMock = mock(Iterator.class);
        java.nio.file.Path[] filesMock = new java.nio.file.Path[] {
                mock(java.nio.file.Path.class),
                mock(java.nio.file.Path.class)
        };
        when(filesMock[0].toString()).thenReturn("dir/one");
        when(filesMock[1].toString()).thenReturn("dir/two");

        when(directoryStreamIteratorMock.hasNext()).thenReturn(true, true, false);
        when(directoryStreamIteratorMock.next()).thenReturn(filesMock[0], filesMock[1]);
        when(directoryStreamMock.iterator()).thenReturn(directoryStreamIteratorMock);
        when(fileSystemProviderMock.newDirectoryStream(any(), any())).thenReturn(directoryStreamMock);

        java.nio.file.Path newFileEventPathMock = mock(java.nio.file.Path.class);
        when(newFileEventPathMock.toString()).thenReturn("new");
        java.nio.file.Path newFileActualPathMock = mock(java.nio.file.Path.class);
        when(newFileActualPathMock.toString()).thenReturn("dir/new");
        when(directoryPathMock.resolve(newFileEventPathMock)).thenReturn(newFileActualPathMock);

        when(event1Mock.kind()).thenReturn((WatchEvent.Kind)StandardWatchEventKinds.ENTRY_CREATE);
        when(event1Mock.context()).thenReturn(newFileEventPathMock);
        when(event2Mock.kind()).thenReturn((WatchEvent.Kind)StandardWatchEventKinds.ENTRY_DELETE);
        when(watchKeyMock.pollEvents())
                .thenReturn(Arrays.asList(event1Mock, event2Mock))
                .thenReturn(Collections.emptyList());

        when(watchServiceMock.take()).thenReturn(watchKeyMock);
        when(watchKeyMock.reset()).thenReturn(true).thenReturn(false);

        ListenerTask task = new ListenerTask(directoryMock, actionMock, fileSystemMock);

        task.run();

        verify(actionMock, times(1)).execute(eq(new info.smart_tools.smartactors.core.path.Path("dir/one")));
        verify(actionMock, times(1)).execute(eq(new info.smart_tools.smartactors.core.path.Path("dir/two")));
        verify(actionMock, times(1)).execute(eq(new info.smart_tools.smartactors.core.path.Path("dir/new")));
        verify(actionMock, times(3)).execute(any());
    }

    @Test
    public void Should_handleThreadInterruption()
            throws Exception {
        DirectoryStream directoryStreamMock = mock(DirectoryStream.class);
        Iterator directoryStreamIteratorMock = mock(Iterator.class);

        when(directoryStreamIteratorMock.hasNext()).thenReturn(false);
        when(directoryStreamMock.iterator()).thenReturn(directoryStreamIteratorMock);
        when(fileSystemProviderMock.newDirectoryStream(any(), any())).thenReturn(directoryStreamMock);

        when(watchServiceMock.take()).thenThrow(new InterruptedException());

        ListenerTask task = new ListenerTask(directoryMock, actionMock, fileSystemMock);

        task.run();

        assertTrue(Thread.interrupted());
    }

    @Test
    public void Should_wrapActionExceptionsIntoRuntimeExceptions()
            throws Exception {
        DirectoryStream directoryStreamMock = mock(DirectoryStream.class);
        Iterator directoryStreamIteratorMock = mock(Iterator.class);
        java.nio.file.Path[] filesMock = new java.nio.file.Path[] {
                mock(java.nio.file.Path.class)
        };

        when(directoryStreamIteratorMock.hasNext()).thenReturn(true, false);
        when(directoryStreamIteratorMock.next()).thenReturn(filesMock[0]);
        when(directoryStreamMock.iterator()).thenReturn(directoryStreamIteratorMock);
        when(fileSystemProviderMock.newDirectoryStream(any(), any())).thenReturn(directoryStreamMock);

        ListenerTask task = new ListenerTask(directoryMock, actionMock, fileSystemMock);

        doThrow(new ActionExecuteException("")).when(actionMock).execute(any(IPath.class));

        try {
            task.run();
            fail();
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof ActionExecuteException);
        }
    }

}
