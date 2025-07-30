package info.smart_tools.smartactors.downloader;

import java.net.URL;

public class ProjectRepository {
    private String repositoryId;
    private URL url;
    private String type;
    private String username;
    private String password;

    public ProjectRepository() {
    }

    public ProjectRepository(final String id, final URL url) {
        this.repositoryId = id;
        this.url = url;
    }

    public ProjectRepository(final String repositoryId, final URL url, final String type, final String username, final String password) {
        this.repositoryId = repositoryId;
        this.url = url;
        this.type = type;
        this.username = username;
        this.password = password;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Repository toRepository() {
        return new Repository(
                this.repositoryId,
                this.url,
                this.type,
                this.username,
                this.password
        );
    }
}
