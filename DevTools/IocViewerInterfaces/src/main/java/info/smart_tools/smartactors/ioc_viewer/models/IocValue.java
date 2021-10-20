package info.smart_tools.smartactors.ioc_viewer.models;

import java.util.List;

public class IocValue {

    private final String key;
    private final IocDependency[] dependencies;

    public IocValue(final String key, final List<IocDependency> dependencies) {
        this.key = key;
        this.dependencies = dependencies.toArray(new IocDependency[0]);
    }

    public String getKey() {
        return key;
    }

    public IocDependency[] getDependencies() {
        return dependencies;
    }
}
