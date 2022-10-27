package info.smart_tools.smartactors.downloader.features;

import info.smart_tools.smartactors.downloader.Repository;

import java.util.List;

public class FeatureList {
    private List<FeatureNamespace> features;
    private List<Repository> repositories;

    public List<FeatureNamespace> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureNamespace> features) {
        this.features = features;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }
}
