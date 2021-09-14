package info.smart_tools.smartactors.downloader.features;

import info.smart_tools.smartactors.downloader.Repository;

import java.util.List;

public class Feature {
    private String featureName;
    private List<String> afterFeatures;
    private Repository repository;
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

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }
}
