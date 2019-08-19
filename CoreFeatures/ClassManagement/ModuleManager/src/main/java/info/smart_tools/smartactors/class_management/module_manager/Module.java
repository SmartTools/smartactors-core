package info.smart_tools.smartactors.class_management.module_manager;

import info.smart_tools.smartactors.class_management.hierarchical_class_loader.SmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Module implements IModule {
    private String name;
    private String version;
    private Object id;
    private List<IModule> dependencies;
    private ISmartactorsClassLoader classLoader;

    Module(final Object id, final String name, final String version) {
        this.name = name;
        this.version = version;
        this.id = id;
        this.dependencies = new /*CopyOnWrite*/ArrayList<>();
        this.classLoader = SmartactorsClassLoader.newInstance(name, version);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public List<IModule> getDependencies() {
        return dependencies;
    }

    @Override
    public ISmartactorsClassLoader getClassLoader() { return classLoader; }

    @Override
    public void addDependency(final IModule base) {
        synchronized (dependencies) {
            if (base != this) {
                dependencies.add(base);
                for (IModule dependency : base.getDependencies()) {
                    if (!dependencies.contains(dependency)) {
                        dependencies.add(dependency);
                    }
                }
                classLoader.addDependency(base.getClassLoader());
            }
        }
    }

    @Override
    public void finalizeDependencies(final IModule defaultModule) {
        synchronized (dependencies) {
            if (dependencies.size() == 0) {
                addDependency(defaultModule);
            }
        }
    }

    public void setDefault() {
        classLoader.setDefault();
    }

    @Override
    public <T> T getFromMap(final Map<IModule, T> objects) {
        T object = objects.get(this);
        if (object == null) {
            for (IModule dependency : dependencies) {
                object = objects.get(dependency);
                if (object != null) {
                    break;
                }
            }
        }
        return object;
    }

    @Override
    public <T> T removeFromMap(final Map<IModule, T> objects) {
        T object = objects.remove(this);
        /*
        // if we need to remove value from dependencies too then uncomment the following
        if (object == null) {
            for(Object dependency : dependencies) {
                object = objects.remove(dependency);
                if (object != null) {
                    break;
                }
            }
        }
        */
        return object;
    }

    public <T> T putToMap(final Map<IModule, T> objects, final T object) {
        return objects.put(this, object);
    }
}
