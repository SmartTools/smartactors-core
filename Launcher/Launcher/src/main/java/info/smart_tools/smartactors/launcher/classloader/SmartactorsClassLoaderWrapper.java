package info.smart_tools.smartactors.launcher.classloader;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

public class SmartactorsClassLoaderWrapper implements IClassLoaderWrapper {

    private final ISmartactorsClassLoader classLoader;

    public SmartactorsClassLoaderWrapper(
            final ISmartactorsClassLoader classLoader
    ) {
        this.classLoader = classLoader;
    }

    @Override
    public void setDefault() {
        try {
            classLoader.setDefault();
        } catch (Exception e) {
            // TODO: handle exception in more appropriate way
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addURL(URL url) {
        try {
            classLoader.addURL(url);
        } catch (Exception e) {
            // TODO: handle exception in more appropriate way
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL[] getURLsFromDependencies() {
        try {
            return classLoader.getURLsFromDependencies();
        } catch (Exception e) {
            // TODO: handle exception in more appropriate way
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> loadClass(
            final String className
    ) throws ClassNotFoundException {
        try {
            return classLoader.loadClass(className);
        } catch (Exception e) {
            // TODO: handle exception in more appropriate way
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> addClass(
            final String className,
            final byte[] classByteCode
    ) {
        try {
            return classLoader.addClass(className, classByteCode);
        } catch (Exception e) {
            // TODO: handle exception in more appropriate way
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClassLoader getCompilationClassLoader() {
        try {
            return classLoader.getCompilationClassLoader();
        } catch (Exception e) {
            // TODO: handle exception in more appropriate way
            throw new RuntimeException(e);
        }
    }
}
