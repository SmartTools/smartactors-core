package info.smart_tools.smartactors.launcher.interfaces.ifeature;

import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureSortingException;

import java.util.List;

/**
 * Interface for feature sorter
 */
public interface IFeatureSorting {

    /**
     * Sort features
     * @param features initial list of features
     * @return sorted list of features
     * @throws FeatureSortingException if failed to sort features
     */
    List<IFeature> sortFeatures(List<IFeature> features) throws FeatureSortingException;
}
