package info.smart_tools.smartactors.feature_management.all_in_direcory_feature_tracker.wrapper;

import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.Collection;

/**
 * Interface of wrapper for {@link info.smart_tools.smartactors.feature_management.all_in_direcory_feature_tracker.AllInDirectoryFeatureTracker}
 */
public interface FeatureTrackerWrapper {

    /**
     * Gets location with features descriptions
     * @return the location
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    String getPath()
            throws ReadValueException;

    /**
     * Sets created features
     * @param features created features by zip files, specific json files for post processing
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setFeatures(Collection<IFeature> features)
            throws ChangeValueException;
}
