package info.smart_tools.smartactors.downloader.features;

import info.smart_tools.smartactors.downloader.ProjectRepository;

import java.util.List;
import java.util.stream.Collectors;

public class Project {
    private List<ProjectFeature> features;
    private List<ProjectRepository> repositories;

    public Project() {
    }

    public List<ProjectFeature> getFeatures() {
        return features;
    }

    public void setFeatures(final List<ProjectFeature> features) {
        this.features = features;
    }

    public List<ProjectRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<ProjectRepository> repositories) {
        this.repositories = repositories;
    }

    public Feature toFeature() {
        Feature feature = new Feature();
        feature.setAfterFeatures(
                this.features.stream().map(ProjectFeature::toFeatureName).collect(Collectors.toList())
        );
        feature.setDependencyRepositories(
                this.repositories.stream().map(ProjectRepository::toRepository).collect(Collectors.toList())
        );

        return feature;
    }
}
