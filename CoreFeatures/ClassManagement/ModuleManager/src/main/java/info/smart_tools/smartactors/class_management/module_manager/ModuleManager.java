package info.smart_tools.smartactors.class_management.module_manager;


import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.module_manager.exception.ModuleManagerException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class ModuleManager
 */
public final class ModuleManager {

    public  static final Object coreId = java.util.UUID.randomUUID();
    private static final String coreName = "info.smart_tools:smartactors";
    // ToDo: manage with version
    private static final String coreVersion = "0.6.0";

    private static ThreadLocal<IModule> currentModule = new ThreadLocal<>();

    private static Map<Object, IModule> modules = new ConcurrentHashMap<>();

    static {
        try {
            addModule(coreId, coreName, coreVersion);
            modules.get(coreId).setDefault();
        } catch (InvalidArgumentException | ModuleManagerException e) {
            // TODO: Empty catch block
        }
    }

    private ModuleManager() {}

    public static void addModule(final Object moduleId, final String moduleName, final String moduleVersion)
            throws InvalidArgumentException, ModuleManagerException {
        if (moduleId == null || moduleName == null || moduleName.equals("")) {
            throw new InvalidArgumentException("Module id or name cannot be null.");
        }

        String version = (moduleVersion == null ? "" : moduleVersion);

        if (modules.get(moduleId) == null) {
            modules.put(moduleId, new Module(moduleId, moduleName, version));
        } else {
            throw new ModuleManagerException("Module " + String.valueOf(moduleId) + " already defined.");
        }
    }

    public static ISmartactorsClassLoader getCurrentClassLoader() {
        return currentModule.get().getClassLoader();
    }

    public static void addModuleDependency(final Object dependentModuleId, final Object baseModuleId)
            throws InvalidArgumentException {

        if (baseModuleId == null || dependentModuleId == null) {
            throw new InvalidArgumentException("Module id cannot be null.");
        }

        modules.get(dependentModuleId).addDependency(modules.get(baseModuleId));
    }

    public static void finalizeModuleDependencies(final Object moduleId)
            throws InvalidArgumentException {
        if (moduleId == null) {
            throw new InvalidArgumentException("Module id cannot be null.");
        }
        modules.get(moduleId).finalizeDependencies(modules.get(coreId));
    }

    public static IModule getModuleById(final Object moduleId) {
        return modules.get(moduleId);
    }

    public static void setCurrentModule(final IModule module) {
        currentModule.set(module);
    }

    public static IModule getCurrentModule() {
        return currentModule.get();
    }

    public static <T> T getFromMap(final Map<IModule, T> objects) {
        return getCurrentModule().getFromMap(objects);
    }

    public static <T> T removeFromMap(final Map<IModule, T> objects) {
        return getCurrentModule().removeFromMap(objects);
    }

    public static <T> T putToMap(final Map<IModule, T> objects, final T object) {
        return getCurrentModule().putToMap(objects, object);
    }
}
