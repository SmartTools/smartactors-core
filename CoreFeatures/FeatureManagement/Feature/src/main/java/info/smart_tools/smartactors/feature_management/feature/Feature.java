package info.smart_tools.smartactors.feature_management.feature;

import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;

import java.util.Set;

/**
 * Implementation of {@link IFeature}
 */
public class Feature implements IFeature {

    private String name;
    private String version;
    private IPath location;
    private IPath directory;
    private String groupId;
    private Set<String> dependencies;
    private boolean failed;
    private Object id;
    private String packageType;

    private static final String FEATURE_NAME_DELIMITER = ":";

    /**
     * Creates instance of {@link IFeature} by specific arguments
     * @param name the feature name
     * @param groupId the feature group id
     * @param version the feature version
     * @param dependencies the feature dependencies
     * @param location the feature location
     * @param directory the feature directory
     * @param packageType the feature package type
     */
    public Feature(
            final String groupId,
            final String name,
            final String version,
            final Set<String> dependencies,
            final IPath location,
            final IPath directory,
            final String packageType
    ) {
        this.name = name;
        this.groupId = groupId;
        this.version = version;
        this.dependencies = dependencies;
        this.location = location;
        this.directory = directory;
        this.packageType = packageType;
        this.id = java.util.UUID.randomUUID();
        this.failed = false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getGroupId() {
        return this.groupId;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public Set<String> getDependencies() {
        return this.dependencies;
    }

    @Override
    public IPath getLocation() {
        return this.location;
    }

    @Override
    public IPath getDirectory() {
        return this.directory;
    }

    @Override
    public Object getId() {
        return this.id;
    }

    @Override
    public String getPackageType() {
        return this.packageType;
    }

    @Override
    public String getDisplayName() {
        return  (this.groupId == null || this.groupId.equals("") ? "*unknown*" : this.groupId) +
                FEATURE_NAME_DELIMITER +
                (this.name == null || this.name.equals("") ? "*unknown*" : this.name) +
                FEATURE_NAME_DELIMITER +
                (this.version == null || this.version.equals("") ? "*unknown*" : this.version);
    }

    @Override
    public boolean isFailed() {
        return this.failed;
    }

    @Override
    public void setFailed(final boolean failed) {
        this.failed = failed;
    }

    @Override
    public void setName(final String featureName) {
        this.name = featureName;
    }

    @Override
    public void setDependencies(final Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public void setLocation(final IPath location) {
        this.location = location;
    }

    @Override
    public void setDirectory(final IPath directory) {
        this.directory = directory;
    }

    @Override
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    @Override
    public void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public void setPackageType(final String packageType) {
        this.packageType = packageType;
    }

    @Override
    public IFeature clone() {
        Feature copy = new Feature(
                this.groupId,
                this.name,
                this.version,
                this.dependencies, // changeable object
                this.location,
                this.directory,
                this.packageType
        );
        copy.id = this.id;
        copy.failed = this.failed;
        return copy;
    }

    @Override
    public boolean updateFromClone(final IFeature featureClone) {
        if (featureClone == null ||
            !(featureClone instanceof Feature ||
            !featureClone.getId().equals(this.id))
        ) {
            return false;
        }
        Feature clone = (Feature) featureClone;
        this.groupId = clone.groupId;
        this.name = clone.name;
        this.version = clone.version;
        this.dependencies = clone.dependencies; // changeable object
        this.location = clone.location;
        this.directory = clone.directory;
        this.packageType = clone.packageType;
        this.failed = clone.failed;
        return true;
    }
}
