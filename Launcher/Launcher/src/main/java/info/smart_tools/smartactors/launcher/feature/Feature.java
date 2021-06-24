package info.smart_tools.smartactors.launcher.feature;

import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;

import java.util.List;

public class Feature implements IFeature {

    private final Object id;
    private final String fileName;
    private final String name;
    private final List<String> afterFeatures;

    public Feature(
            final Object id,
            final String fileName,
            final String name,
            final List<String> afterFeatures
    ) {
        this.id = id;
        this.fileName = fileName;
        this.name = name;
        this.afterFeatures = afterFeatures;
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
    public String getName() {
        return name;
    }

    @Override
    public List<String> getAfterFeatures() {
        return afterFeatures;
    }
}
