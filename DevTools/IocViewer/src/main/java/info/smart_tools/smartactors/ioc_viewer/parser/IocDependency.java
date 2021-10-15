package info.smart_tools.smartactors.ioc_viewer.parser;

public class IocDependency {

    private final String module;
    private final String version;
    private final Object dependency;

    public IocDependency(final String module, final String version, final Object dependency) {
        this.module = module;
        this.version = version;
        this.dependency = dependency;
    }

    public String getModule() {
        return module;
    }

    public String getVersion() {
        return version;
    }

    public Object getDependency() {
        return dependency;
    }
}
