package info.smart_tools.smartactors.launcher.model;

import java.util.List;
import java.util.Objects;

/**
 * Model for feature config without any additional sections
 */
public class FeatureConfig {
    private String featureName;
    private List<String> afterFeatures;
    private List<String> plugins;

    /**
     * Empty constructor
     */
    public FeatureConfig() {
        super();
    }

    /**
     * Construction with all fields
     *
     * @param featureName name of the feature
     * @param afterFeatures list of dependencies for the feature
     * @param plugins list of plugins for the feature
     */
    public FeatureConfig(
            final String featureName,
            final List<String> afterFeatures,
            final List<String> plugins
    ) {
        this.featureName = featureName;
        this.afterFeatures = afterFeatures;
        this.plugins = plugins;
    }

    /**
     * Get feature's name
     *
     * @return feature's name
     */
    public String getFeatureName() {
        return featureName;
    }

    /**
     * Set feature's name
     *
     * @param featureName feature's new name
     */
    public void setFeatureName(
            final String featureName
    ) {
        this.featureName = featureName;
    }

    /**
     * Get feature's dependencies
     *
     * @return feature's dependencies
     */
    public List<String> getAfterFeatures() {
        return afterFeatures;
    }

    /**
     * Set feature's dependencies
     *
     * @param afterFeatures feature's new dependencies
     */
    public void setAfterFeatures(
            final List<String> afterFeatures
    ) {
        this.afterFeatures = afterFeatures;
    }

    /**
     * Get feature's plugins
     *
     * @return feature's plugins
     */
    public List<String> getPlugins() {
        return plugins;
    }

    /**
     * Set feature's plugins
     *
     * @param plugins feature's new plugins
     */
    public void setPlugins(
            final List<String> plugins
    ) {
        this.plugins = plugins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureConfig that = (FeatureConfig) o;
        return Objects.equals(featureName, that.featureName) && Objects.equals(afterFeatures, that.afterFeatures) && Objects.equals(plugins, that.plugins);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureName, afterFeatures, plugins);
    }
}
