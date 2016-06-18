package info.smart_tools.smartactors.core.feature_manager;

import info.smart_tools.smartactors.core.ifeature_manager.IFeature;
import info.smart_tools.smartactors.core.ifeature_manager.IFeatureManager;
import info.smart_tools.smartactors.core.ifeature_manager.exception.FeatureManagementException;
import info.smart_tools.smartactors.core.ifilesystem_tracker.IFilesystemTracker;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;

/**
 * Implementation of {@link IFeatureManager}.
 */
public class FeatureManager implements IFeatureManager {
    private IFilesystemTracker filesystemTracker;

    /**
     * The constructor.
     *
     * @param filesystemTracker the {@link IFilesystemTracker} instance to use
     * @throws InvalidArgumentException if {@code filesystemTracker} is {@code null}
     */
    public FeatureManager(final IFilesystemTracker filesystemTracker)
            throws InvalidArgumentException {
        if (null == filesystemTracker) {
            throw new InvalidArgumentException("Filesystem tracker should not be null.");
        }

        this.filesystemTracker = filesystemTracker;
    }

    @Override
    public IFeature newFeature(final String name) throws FeatureManagementException {
        try {
            return new Feature(name, this.filesystemTracker);
        } catch (InvalidArgumentException e) {
            throw new FeatureManagementException("Error creating new feature.", e);
        }
    }
}
