package info.smart_tools.smartactors.core.feature_manager;

import info.smart_tools.smartactors.core.ifeature_manager.IFeature;
import info.smart_tools.smartactors.core.ifeature_manager.exception.FeatureManagementException;
import info.smart_tools.smartactors.core.ifilesystem_tracker.IFilesystemTracker;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link FeatureManager}.
 */
public class FeatureManagerTest {
    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_filesystemTrackerIsNull()
            throws Exception {
        new FeatureManager(null);
    }

    @Test(expected = FeatureManagementException.class)
    public void Should_newFeatureThrow_When_FeatureConstructorThrows()
            throws Exception {
        new FeatureManager(mock(IFilesystemTracker.class)).newFeature(null);
    }

    @Test
    public void Should_createNewFeatures()
            throws Exception {
        IFeature feature = new FeatureManager(mock(IFilesystemTracker.class)).newFeature("bug");

        assertNotNull(feature);
        assertTrue(feature instanceof Feature);
    }
}