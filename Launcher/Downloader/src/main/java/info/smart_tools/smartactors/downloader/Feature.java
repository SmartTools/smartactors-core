package info.smart_tools.smartactors.downloader;

import java.util.List;

public class Feature {
    private String featureName;
    private List<String> afterFeatures;
    private Repository repository;
    private List<String> plugins;


    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public List<String> getAfterFeatures() {
        return afterFeatures;
    }

    public void setAfterFeatures(List<String> afterFeatures) {
        this.afterFeatures = afterFeatures;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }
//
//    public String getName() {
//        return this.featureName.split(":")[1];
//    }
//
//    public String getGroup() {
//        return this.featureName.split(":")[0];
//    }
//
//    public String getVersion() {
//        return this.featureName.split(":")[2];
//    }
}
