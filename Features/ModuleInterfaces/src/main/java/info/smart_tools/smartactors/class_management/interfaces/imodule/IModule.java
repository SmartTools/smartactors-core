package info.smart_tools.smartactors.class_management.interfaces.imodule;


import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;

import java.util.List;
import java.util.Map;

/**
 * Interface IModule
 */
public interface IModule {

    String getName();

    String getVersion();

    Object getId();

    List<IModule> getDependencies();

    ISmartactorsClassLoader getClassLoader();

    void addDependency(IModule base);

    void finalizeDependencies(IModule defaultModule);

    void setDefault();

    <T> T getFromMap(Map<IModule, T> objects);

    <T> T removeFromMap(Map<IModule, T> objects);

    <T> T putToMap(Map<IModule, T> objects, T object);
}
