package info.smart_tools.smartactors.downloader;

import java.net.URL;

public class Repository {
    private String id;
    private URL url;

    public Repository() {
    }

    public Repository(final String id, final URL url) {
        this.id = id;
        this.url = url;
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
}
