package info.smart_tools.smartactors.downloader.commons;

import java.util.List;

public class MethodParameters {
    private List<String> argumentPaths;
    private String responsePath;

    public MethodParameters() {
    }

    public MethodParameters(final List<String> argumentPaths, final String responsePath) {
        this.argumentPaths = argumentPaths;
        this.responsePath = responsePath;
    }

    public List<String> getArgumentPaths() {
        return argumentPaths;
    }

    public void setArgumentPaths(List<String> argumentPaths) {
        this.argumentPaths = argumentPaths;
    }

    public String getResponsePath() {
        return responsePath;
    }

    public void setResponsePath(String responsePath) {
        this.responsePath = responsePath;
    }
}
