package info.smart_tools.smartactors.downloader.features;

public class FeatureNamespace {

    private final String name;
    private final String group;
    private final String version;

    public FeatureNamespace(final String featureName) throws Exception {
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
            throw new Exception("Feature full name could not be null.");
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
