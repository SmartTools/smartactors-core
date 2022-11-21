package info.smart_tools.smartactors.downloader.features;

public class ProjectFeature {
    private String group;
    private String name;
    private String version;

    public ProjectFeature() {
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String toFeatureName() {
        return String.join(":", this.group, this.name, this.version);
    }
}
