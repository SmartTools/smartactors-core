package info.smart_tools.smartactors.uploader.features;

import java.util.List;

public class Feature {
    private String featureName;
    private List<String> afterFeatures;
    private FeatureRepository repository;
    private List<String> plugins;

    public Feature() {
    }

    public Feature(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public List<String> getAfterFeatures() {
        return afterFeatures;
    }

    public void setAfterFeatures(List<String> afterFeatures) {
        this.afterFeatures = afterFeatures;
    }

    public FeatureRepository getRepository() {
        return repository;
    }

    public void setRepository(FeatureRepository repository) {
        this.repository = repository;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }
}
