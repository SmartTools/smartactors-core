package info.smart_tools.smartactors.uploader.repositories;

public class RepositoryConfig {

    private final String repositoryId;
    private final String repositoryUrl;

    public RepositoryConfig(final String repositoryId, final String repositoryUrl) {
        this.repositoryId = repositoryId;
        this.repositoryUrl = repositoryUrl;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }
}
