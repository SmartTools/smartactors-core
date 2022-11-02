package info.smart_tools.smartactors.class_management.class_loaders;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Extension of {@link URLClassLoader}
 */
public class SmartactorsClassLoader extends URLClassLoader implements ISmartactorsClassLoader {

    private static SmartactorsClassLoader defaultClassLoader = null;

    private String moduleName;
    private String moduleVersion;
    private Set<SmartactorsClassLoader> dependencies = new HashSet<>();
    private SmartactorsClassLoader compilationClassLoader = null;

    /**
     * Redefined constructor
     * @param moduleName the name of module which class loader contains
     * @param moduleVersion the version of module which class loader contains
     */
    private SmartactorsClassLoader(String moduleName, String moduleVersion) {
        super(new URL[]{});
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
    }

    public static ISmartactorsClassLoader newInstance(String moduleName, String moduleVersion) {
        return new SmartactorsClassLoader(moduleName, moduleVersion);
    }

    public void setDefault() {
        defaultClassLoader = this;
    }

    public void addDependency(ISmartactorsClassLoader base) {
        if (base != null && base != this) {
            synchronized (this) {
                dependencies.add((SmartactorsClassLoader)base);
            }
        }
    }

    private void addDependenciesToSet(Set<ClassLoader> classLoaders) {
        classLoaders.add(this);
        if (dependencies.size() == 0) {
            ClassLoader parent = this.getParent();
            while(parent != null) {
                classLoaders.add(parent);
                parent = parent.getParent();
            }
        } else {
            for(SmartactorsClassLoader classLoader : dependencies) {
                classLoader.addDependenciesToSet(classLoaders);
            }
        }
    }

    public URL[] getURLsFromDependencies() {

        Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
        addDependenciesToSet(classLoaders);

        ArrayList<URL> urlArrayList = new ArrayList<>();
        for( ClassLoader classLoader : classLoaders) {
            if (classLoader instanceof URLClassLoader) {
                Collections.addAll(urlArrayList, ((URLClassLoader) classLoader).getURLs());
            }
        }

        URL[] urls = new URL[urlArrayList.size()];
        urlArrayList.toArray(urls);

        return urls;
    }

    /**
     * Search class in this class loader and each of its dependencies
     * (if dependency is instance of SmartactorsClassLoader)
     * @param className The name of the class to get
     * @return The reference to the class
     */
    private final Class<?> searchClassInDependencies(String className) {
        Class clazz = this.findLoadedClass(className);
        if (null == clazz) {
            for(SmartactorsClassLoader dependency : this.dependencies) {
                clazz = dependency.searchClassInDependencies(className);
                if (clazz != null) {
                    break;
                }
            }
        }
        return clazz;
    }

    private Class<?> loadClassFromDependencies(String className, ClassLoader scl, ClassLoader dcl, boolean[] rclUsed )
            throws ClassNotFoundException {
        if (dependencies.size() == 0) {
            try {
                ClassLoader parent = getParent();
                if (parent != scl) {
                    return parent.loadClass(className);
                } else if (!rclUsed[0]) {
                    rclUsed[0] = true;
                    return parent.loadClass(className);
                }
            } catch (ClassNotFoundException e) { }
        } else {
            for (SmartactorsClassLoader dependency : dependencies) {
                try {
                    if (dependency != dcl) {
                        return dependency.loadClassFromDependencies(className, scl, dcl, rclUsed);
                    } else if (!rclUsed[1]) {
                        rclUsed[1] = true;
                        return dependency.loadClassFromDependencies(className, scl, dcl, rclUsed);
                    }
                } catch (ClassNotFoundException e) { }
            }
        }
        return this.findClass(className);
    }

    protected Class<?> loadClass(String className, boolean resolve)
            throws ClassNotFoundException {
        synchronized(this.getClassLoadingLock(className)) {

            Class clazz = this.searchClassInDependencies(className);
            if (clazz == null) {
                boolean[] sclUsed = {false, false};
                clazz = this.loadClassFromDependencies(
                        className,
                        getSystemClassLoader(),
                        defaultClassLoader,
                        sclUsed
                );
            }

            if (resolve) {
                this.resolveClass(clazz);
            }

            return clazz;
        }
    }

    /**
     * Add compiled byte code of the class directly to this class loader
     * @param className The name of the class to define
     * @param classByteCode Compiled byte code of the class to add
     * @return The reference to the class
     */
    public Class<?> addClass(final String className, byte[] classByteCode) {
        return defineClass(className, classByteCode, 0, classByteCode.length);
    }

    public ClassLoader getCompilationClassLoader() {
        if (compilationClassLoader == null) {
            compilationClassLoader = new SmartactorsClassLoader(
                    "wrapper-compilation-class-loader",
                    ""
            );
            compilationClassLoader.addDependency(this);
        }
        return compilationClassLoader;
    }

    /**
     * Add new instance of {@link URL} to the current url class loader if url class loader doesn't contain this instance of {@link URL}
     * @param url instance of {@link URL}
     */
    public void addURL(final URL url) {
        super.addURL(url);
    }
}
