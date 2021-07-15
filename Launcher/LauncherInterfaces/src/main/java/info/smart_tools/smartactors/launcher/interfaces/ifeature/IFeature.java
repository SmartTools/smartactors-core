package info.smart_tools.smartactors.launcher.interfaces.ifeature;

import info.smart_tools.smartactors.launcher.interfaces.IPath;

import java.util.List;

/**
 * Interface for feature model
 */
public interface IFeature {

    /**
     * Get ID of the feature
     * @return ID of the feature
     */
    Object getId();

    /**
     * Get name of the file which contains loaded feature
     * @return file with feature
     */
    String getFileName();

    /**
     * Get path to file with feature
     * @return path to file with feature
     */
    IPath getPath();

    /**
     * Get name of the feature
     * @return name of the feature
     */
    String getName();

    /**
     * Get list of dependencies for the feature
     * @return list of dependencies for the feature
     */
    List<String> getAfterFeatures();

    /**
     * Update list of dependencies for the feature
     * @param afterFeatures updated list of dependencies
     */
    void setAfterFeatures(List<String> afterFeatures);

    /**
     * Get list of plugins for the feature
     * @return list of plugins for the feature
     */
    List<String> getPlugins();
}
