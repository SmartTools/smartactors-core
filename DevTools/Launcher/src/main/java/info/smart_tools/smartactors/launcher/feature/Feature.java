package info.smart_tools.smartactors.launcher.feature;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.path.Path;

import java.util.List;

public class Feature implements IFeature {

    private Object id;
    private String fileName;
    private String name;
    private List<String> afterFeatures;
    private List<String> plugins;

    public Feature(
            final Object id,
            final String fileName,
            final String name,
            final List<String> afterFeatures,
            final List<String> plugins
    ) {
        this.id = id;
        this.fileName = fileName;
        this.name = name;
        this.afterFeatures = afterFeatures;
        this.plugins = plugins;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public IPath getPath() {
        return new Path(fileName);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getAfterFeatures() {
        return afterFeatures;
    }

    @Override
    public void setAfterFeatures(List<String> afterFeatures) {
        this.afterFeatures = afterFeatures;
    }

    @Override
    public List<String> getPlugins() {
        return plugins;
    }
}
