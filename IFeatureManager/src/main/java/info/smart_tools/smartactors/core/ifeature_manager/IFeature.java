package info.smart_tools.smartactors.core.ifeature_manager;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.ifeature_manager.exception.FeatureManagementException;
import info.smart_tools.smartactors.core.ipath.IPath;

import java.util.Collection;

/**
 * Interface for a feature. A feature is a set of files and actions that should be performed when all those files are
 * present.
 */
public interface IFeature {
    /**
     * Add a file name to list of names of files required for this feature.
     * The file name should not contain any preceding path.
     * @param fileName    name of the file
     * @throws FeatureManagementException if {@link #listen()} already has been called on this {@link IFeature}.
     */
    void requireFile(String fileName) throws FeatureManagementException;

    /**
     * Define the action to run when all files required for this feature are present.
     *
     * @param action    action to execute on list of files matching required names specified using {@link
     *                  #requireFile(String)} when all those files are present.
     * @throws FeatureManagementException if any error occurs.
     */
    void whenPresent(IAction<Collection<IPath>> action) throws FeatureManagementException;

    /**
     * Start look for required files (exist or appearing later).
     *
     * @throws FeatureManagementException   if there is no file names specified for this feature using {@link
     *                                      #requireFile(String)}.
     * @throws FeatureManagementException   if there is no actions specified to run when all required files are present.
     */
    void listen() throws FeatureManagementException;

    /**
     * Get the name of this feature.
     *
     * @return name of this feature
     */
    String getName();
}
