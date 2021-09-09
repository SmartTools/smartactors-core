package info.smart_tools.smartactors.uploader.jcommander;

import com.beust.jcommander.Parameter;

public class Arguments {

    @Parameter(
            names = {"-featurePath", "featurePath", "-fp"},
            description = "Path to the feature archive for upload, can be .zip or .jar"
    )
    private String featurePath;

    @Parameter(
            names = {"-u", "-username", "username"},
            description = "Username in remote repository"
    )
    private String username;

    @Parameter(
            names = {"-p", "-password", "password"},
            description = "Password for user in remote repository",
            password = true
    )
    private String password;

    @Parameter(
            names = {"-rUrl", "-repositoryUrl", "repositoryUrl"},
            description = "URL of remote repository"
    )
    private String repositoryUrl;

    @Parameter(
            names = {"-rId", "-repositoryId", "repositoryId"},
            description = "ID of remote repository"
    )
    private String repositoryId;

    public String getFeaturePath() {
        return featurePath;
    }

    public void setFeaturePath(String featurePath) {
        this.featurePath = featurePath;
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

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }
}
