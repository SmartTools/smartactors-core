package info.smart_tools.smartactors.downloader;

public class FeatureNameSpace {

    private String name;
    private String group;
    private String version;

    private FeatureNameSpace() {
    }

    public FeatureNameSpace(final String featureName) throws Exception {
        if (null != featureName ) {
            String[] parts = featureName.split(":");
            if (parts.length == 3) {
                this.group = parts[0];
                this.name = parts[1];
                this.version = parts[2];
            } else {
                throw new Exception("Could not parse feature full name - " + featureName + ".");
            }
        } else {
            throw new Exception("Feature full name colud not be null.");
        }
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }
}
