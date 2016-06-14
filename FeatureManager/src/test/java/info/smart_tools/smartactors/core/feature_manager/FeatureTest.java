package info.smart_tools.smartactors.core.feature_manager;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ifeature_manager.exception.FeatureManagementException;
import info.smart_tools.smartactors.core.ifilesystem_tracker.IFilesystemTracker;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link Feature}.
 */
public class FeatureTest {
    private IFilesystemTracker filesystemTrackerMock;
    private ArgumentCaptor<IAction> trackerActionCaptor;

    @Before
    public void setUp() {
        filesystemTrackerMock = mock(IFilesystemTracker.class);
        trackerActionCaptor = ArgumentCaptor.forClass(IAction.class);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_theNameIsNull()
            throws Exception {
        new Feature(null, filesystemTrackerMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_theFilesystemTrackerIsNull()
            throws Exception {
        new Feature("bug", null);
    }

    @Test(expected = FeatureManagementException.class)
    public void Should_listenThrow_When_NoFilesSpecified()
            throws Exception {
        Feature feature = new Feature("bug", filesystemTrackerMock);

        feature.listen();
    }

    @Test(expected = FeatureManagementException.class)
    public void Should_listenThrow_When_CalledTwice()
            throws Exception {
        Feature feature = new Feature("bug", filesystemTrackerMock);

        feature.requireFile("bug-feature.jar");

        try {
            feature.listen();
        } catch (FeatureManagementException e) {
            fail("Should not throw yet.");
        }

        feature.listen();
    }

    @Test(expected = FeatureManagementException.class)
    public void Should_requireFileThrow_When_CalledAfter_listen()
            throws Exception {
        Feature feature = new Feature("bug", filesystemTrackerMock);

        feature.requireFile("bug-feature.jar");

        try {
            feature.listen();
        } catch (FeatureManagementException e) {
            fail("Should not throw yet.");
        }

        feature.requireFile("nice-feature.txt");
    }

    @Test
    public void Should_getNameReturnNameGivenOnInitialization()
            throws Exception {
        Feature feature = new Feature("bug", filesystemTrackerMock);

        assertEquals("bug", feature.getName());
    }

    @Test
    public void Should_subscribeForNotificationsFromFileSystemTracker_When_listenCalled()
            throws Exception {
        IAction actionMock1 = mock(IAction.class), actionMock2 = mock(IAction.class), actionMock3 = mock(IAction.class);
        ActionExecuteException exceptionMock1 = mock(ActionExecuteException.class), exceptionMock2 = mock(ActionExecuteException.class);
        File fileMock1 = mock(File.class), fileMock2 = mock(File.class), fileMock3 = mock(File.class);

        doThrow(exceptionMock1).when(actionMock1).execute(any());
        doThrow(exceptionMock2).when(actionMock2).execute(any());
        when(fileMock1.getName()).thenReturn("bug-feature-1.jar");
        when(fileMock2.getName()).thenReturn("bug-feature-2.jar");
        when(fileMock3.getName()).thenReturn("nice-feature.txt");

        Feature feature = new Feature("bug", filesystemTrackerMock);

        feature.requireFile("bug-feature-1.jar");
        feature.requireFile("bug-feature-2.jar");

        feature.whenPresent(actionMock1);
        feature.whenPresent(actionMock2);
        feature.whenPresent(actionMock3);

        feature.listen();

        verify(filesystemTrackerMock).addFileHandler(trackerActionCaptor.capture());

        verifyZeroInteractions(actionMock1, actionMock2, actionMock3);

        trackerActionCaptor.getValue().execute(fileMock1);
        trackerActionCaptor.getValue().execute(fileMock3);

        try {
            trackerActionCaptor.getValue().execute(fileMock2);
            fail();
        } catch (ActionExecuteException e) {
            assertSame(exceptionMock1, e.getCause());
            assertEquals(e.getSuppressed().length, 1);
            assertSame(exceptionMock2, e.getSuppressed()[0]);
        }

        verify(actionMock1).execute(any());
        verify(actionMock2).execute(any());
        verify(actionMock3).execute(any());
        verify(filesystemTrackerMock).removeFileHandler(same(trackerActionCaptor.getValue()));
    }

    @Test
    public void Should_executeActionImmediately_When_filesAreAlreadyPresent()
            throws Exception {
        File fileMock = mock(File.class);
        IAction actionMock = mock(IAction.class);

        when(fileMock.getName()).thenReturn("bug-feature.jar");

        Feature feature = new Feature("bug", filesystemTrackerMock);

        feature.requireFile("bug-feature.jar");

        feature.listen();

        verify(filesystemTrackerMock).addFileHandler(trackerActionCaptor.capture());
        trackerActionCaptor.getValue().execute(fileMock);

        feature.whenPresent(actionMock);

        verify(actionMock).execute(any());
    }

    @Test
    public void Should_wrapException_When_filesAreAlreadyPresent_And_ImmediatelyExecutedActionThrows()
            throws Exception {
        File fileMock = mock(File.class);
        IAction actionMock = mock(IAction.class);
        ActionExecuteException exceptionMock = mock(ActionExecuteException.class);

        when(fileMock.getName()).thenReturn("bug-feature.jar");
        doThrow(exceptionMock).when(actionMock).execute(any());

        Feature feature = new Feature("bug", filesystemTrackerMock);

        feature.requireFile("bug-feature.jar");

        feature.listen();

        verify(filesystemTrackerMock).addFileHandler(trackerActionCaptor.capture());
        trackerActionCaptor.getValue().execute(fileMock);

        try {
            feature.whenPresent(actionMock);
            fail();
        } catch (FeatureManagementException e) {
            assertSame(exceptionMock, e.getCause());
        }

        verify(actionMock).execute(any());
    }
}
