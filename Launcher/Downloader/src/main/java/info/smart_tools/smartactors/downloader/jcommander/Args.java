package info.smart_tools.smartactors.downloader.jcommander;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(
            names = {"file", "f", "-file", "-f"},
            description = "Name of feature config file. Default value is 'config.json'."
    )
    private String fileName = "config.json";

    @Parameter(
            names = {"command", "cmd", "c", "-command", "-cmd", "-c"},
            description = "Command. Available values: 'launcher', 'features', 'feature', 'config', configs. Default value is 'features'."
    )
    private String command = "features";

    @Parameter(
            names = {"dependencies", "deps", "d", "-dependencies", "-deps", "-d"},
            description = "Include dependencies. Default value: true."
    )
    private boolean withDependencies = true;

    @Parameter(
            names = {"feature_type", "type", "ft", "-feature_type", "-type", "-ft"},
            description = "Feature type. Available values: jar, zip, json. Default value: jar."
    )
    private String featureType = "jar";

    @Parameter(
            names = {"target_directory", "td", "destination", "-target_directory", "-td", "-destination"},
            description = "Name of destination directory. Default value is 'server'."
    )
    private String destination = "server";

    @Parameter(
            names = {"recursive", "r", "-recursive", "-r"},
            description = "Recursive downloading. Default value: true."
    )
    private boolean recursiveDownloading = true;

    @Parameter(names = {"help", "man", "?", "h", "-help", "-man", "-?", "-h", "--help"}, help = true)
    private boolean help;

    @Parameter(names = "")
    private String main;

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isWithDependencies() {
        return withDependencies;
    }

    public void setWithDependencies(boolean withDependencies) {
        this.withDependencies = withDependencies;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isRecursiveDownloading() {
        return recursiveDownloading;
    }

    public void setRecursiveDownloading(boolean recursiveDownloading) {
        this.recursiveDownloading = recursiveDownloading;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }
}
