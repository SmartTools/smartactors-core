package info.smart_tools.smartactors.core.ifeature_manager;

import info.smart_tools.smartactors.core.ifeature_manager.exception.FeatureManagementException;

/**
 *
 */
public interface IFeatureManager {
    /**
     * Create new {@link IFeature} instance managed by this manager.
     *
     * @param name the name of new feature
     * @return the new {@link IFeature} instance.
     * @throws FeatureManagementException if any error occurs.
     */
    IFeature newFeature(String name) throws FeatureManagementException;
}
