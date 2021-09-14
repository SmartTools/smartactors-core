package info.smart_tools.smartactors.launcher.classloader;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;

import java.net.URL;

public class SmartactorsClassLoaderWrapper implements IClassLoaderWrapper {

    private final ISmartactorsClassLoader classLoader;

    public SmartactorsClassLoaderWrapper(
            final ISmartactorsClassLoader classLoader
    ) {
        this.classLoader = classLoader;
    }

    @Override
    public void addURL(URL url) {
        classLoader.addURL(url);
    }

    @Override
    public Class<?> loadClass(
            final String className
    ) throws ClassNotFoundException {
        try {
            return classLoader.loadClass(className);
        } catch (Exception e) {
            throw new ClassNotFoundException("Failed to add class to class loader", e);
        }
    }
}
