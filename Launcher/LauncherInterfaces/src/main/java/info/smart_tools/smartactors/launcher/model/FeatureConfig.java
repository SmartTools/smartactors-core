package info.smart_tools.smartactors.launcher.model;

import java.util.List;
import java.util.Objects;

public class FeatureConfig {
    private String featureName;
    private List<String> afterFeatures;
    private List<String> plugins;

    public FeatureConfig() {
        super();
    }

    public FeatureConfig(
            final String featureName,
            final List<String> afterFeatures,
            final List<String> plugins
    ) {
        this.featureName = featureName;
        this.afterFeatures = afterFeatures;
        this.plugins = plugins;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(
            final String featureName
    ) {
        this.featureName = featureName;
    }

    public List<String> getAfterFeatures() {
        return afterFeatures;
    }

    public void setAfterFeatures(
            final List<String> afterFeatures
    ) {
        this.afterFeatures = afterFeatures;
    }

    public List<String> getPlugins() {
        return plugins;
    }

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
