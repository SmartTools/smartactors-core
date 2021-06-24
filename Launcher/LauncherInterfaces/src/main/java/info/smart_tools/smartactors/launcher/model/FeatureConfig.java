package info.smart_tools.smartactors.launcher.model;

import java.util.List;
import java.util.Objects;

public class FeatureConfig {
    private String featureName;
    private List<String> afterFeatures;

    public FeatureConfig() {
        super();
    }

    public FeatureConfig(
            final String featureName,
            final List<String> afterFeatures
    ) {
        this.featureName = featureName;
        this.afterFeatures = afterFeatures;
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

    @Override
    public boolean equals(
            final Object o
    ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureConfig featureConfig = (FeatureConfig) o;
        return Objects.equals(featureName, featureConfig.featureName) && Objects.equals(afterFeatures, featureConfig.afterFeatures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureName, afterFeatures);
    }
}
