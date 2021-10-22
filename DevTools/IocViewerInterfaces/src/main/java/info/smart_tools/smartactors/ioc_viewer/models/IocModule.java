package info.smart_tools.smartactors.ioc_viewer.models;

import java.util.Objects;

public class IocModule {

    private final String name;
    private final String version;

    public IocModule(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IocModule iocModule = (IocModule) o;
        return Objects.equals(name, iocModule.name) && Objects.equals(version, iocModule.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version);
    }
}
