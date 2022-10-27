package info.smart_tools.smartactors.feature_management.interfaces.ifeature;

import info.smart_tools.smartactors.base.interfaces.ipath.IPath;

import java.util.Set;

/**
 * IFeature interface
 */
public interface IFeature {

    /**
     * Gets feature name
     * @return the feature name
     */
    String getName();

    /**
     * Gets set of the feature dependencies
     * @return the set of feature dependencies
     */
    Set<String> getDependencies();

    /**
     * Gets true if feature loading has been failed
     * @return {@code true} if feature loading has been failed, {@code false} otherwise
     */
    boolean isFailed();

    /**
     * Gets feature group ID
     * @return the feature group ID
     */
    String getGroupId();

    /**
     * Gets feature version
     * @return the feature version
     */
    String getVersion();

    /**
     * Gets feature location
     * @return the feature location
     */
    IPath getLocation();

    /**
     * Gets feature directory
     * @return the feature directory
     */
    IPath getDirectory();

    /**
     * Gets feature ID
     * @return the feature ID
     */
    Object getId();

    /**
     * Gets feature package type (zip, jar, etc)
     * @return the feature package type
     */
    String getPackageType();

    /**
     * Gets full feature name
     * @return the full feature name
     */
    String getDisplayName();

    /**
     * Sets feature name
     * @param featureName the feature name
     */
    void setName(String featureName);

    /**
     * Sets feature dependencies
     * @param dependencies the feature dependencies
     */
    void setDependencies(Set<String> dependencies);

    /**
     * Sets true if feature loading has been failed
     * @param failed {@code true} if feature loading has been failed, {@code false} otherwise
     */
    void setFailed(boolean failed);

    /**
     * Sets feature location
     * @param location the feature location
     */
    void setLocation(IPath location);

    /**
     * Sets feature directory
     * @param location the feature directory
     */
    void setDirectory(IPath location);

    /**
     * Sets feature group ID
     * @param groupId the feature group ID
     */
    void setGroupId(String groupId);

    /**
     * Sets feature version
     * @param version the feature version
     */
    void setVersion(String version);

    /**
     * Sets feature package type
     * @param packageType the feature package type
     */
    void setPackageType(String packageType);

    /**
     * Creates a copy of the feature instance with same Id and other fields
     * @return new feature copy
     */
    IFeature clone();

    /**
     * Update feature content from its clone
     * @param clone the clone of the feature
     * @return true if update successful and false otherwise
     */
    boolean updateFromClone(IFeature clone);
}
