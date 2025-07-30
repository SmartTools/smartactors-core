package info.smart_tools.smartactors.downloader;

import java.net.URL;

public class Repository {
    private String id;
    private URL url;
    private String type;
    private String username;
    private String password;

    public Repository() {
    }

    public Repository(final String id, final URL url) {
        this.id = id;
        this.url = url;
    }

    public Repository(final String repositoryId, final URL url, final String type, final String username, final String password) {
        this.id = repositoryId;
        this.url = url;
        this.type = type;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
