package info.smart_tools.smartactors.class_management.class_loaders;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Extension of {@link URLClassLoader}
 */
public class SmartactorsClassLoader extends URLClassLoader implements ISmartactorsClassLoader {

    private String moduleName;
    private String moduleVersion;
    private Set<SmartactorsClassLoader> dependencies = new HashSet<>(); //Collections.synchronizedSet(new HashSet<>());
    private Map<String, ClassLoader> classMap = new ConcurrentHashMap<>();
    private SmartactorsClassLoader compilationClassLoader = null;

    /**
     * Redefined constructor
     * @param moduleName the name of module which class loader contains
     * @param moduleVersion the version of module which class loader contains
     */
    private SmartactorsClassLoader(String moduleName, String moduleVersion) {
        super(new URL[]{});
        moduleName = moduleName.replace('/', '.');
        moduleName = moduleName.replace(':', '.');
        moduleName = moduleName.replace('-', '_');
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        classMap.put(this.moduleName, this);
    }

    public static ISmartactorsClassLoader newInstance(String moduleName, String moduleVersion) {
        return new SmartactorsClassLoader(moduleName, moduleVersion);
    }

    public void setDefault() { }

    public void addDependency(ISmartactorsClassLoader base) {
        if (base != null && base != this) {
            Set<SmartactorsClassLoader> classLoaders = new HashSet<SmartactorsClassLoader>();
            classLoaders.add((SmartactorsClassLoader)base);
            synchronized (base) {
                classLoaders.addAll(((SmartactorsClassLoader)base).dependencies);
            }
            synchronized (this) {
                for (SmartactorsClassLoader cl : classLoaders) {
                    classMap.put(cl.moduleName, cl);
                }
                dependencies.addAll(classLoaders);
            }
        }
    }

    public URL[] getURLsFromDependencies() {

        Set<ClassLoader> dependencies = new HashSet<>();
        dependencies.add(this);
        dependencies.addAll(this.dependencies);

        Set<ClassLoader> classLoaders = new HashSet<>();
        for(ClassLoader classLoader : dependencies) {
            classLoaders.add(classLoader);
            ClassLoader parent = classLoader.getParent();
            while(parent != null) {
                classLoaders.add(parent);
                parent = parent.getParent();
            }
        }

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

    private Class<?> loadClass0(String className, boolean upperLevel)
            throws ClassNotFoundException {

        Class clazz = this.findLoadedClass(className);
        if (clazz == null) {

            ClassLoader classLoader = classMap.get(className);
            if (classLoader != null) {
                try {
                    clazz = classLoader.loadClass(className);
                } catch (ClassNotFoundException e) { }
            }

            if (clazz == null) {
                if (this.getParent() != null) {
                    try {
                        clazz = this.getParent().loadClass(className);
                    } catch (ClassNotFoundException e) { }
                }

                if (clazz == null && upperLevel) {
                    String name = className;
                    do {
                        int index = name.lastIndexOf(".");
                        if (index == -1) {
                            break;
                        }
                        name = name.substring(0, index);
                        classLoader = classMap.get(name);
                    } while (classLoader == null);

                    if (classLoader != null && this != classLoader) {
                        try {
                            clazz = classLoader.loadClass(className);
                            classMap.put(className, clazz.getClassLoader());
                        } catch (ClassNotFoundException e) { }
                    }
                }

                if (clazz == null && upperLevel) {
                    for (SmartactorsClassLoader dependency : dependencies) {
                        if (dependency != classLoader) {
                            try {
                                clazz = dependency.loadClass0(className, false);
                                classMap.put(className, clazz.getClassLoader());
                                break;
                            } catch (ClassNotFoundException e) { }
                        }
                    }
                }

                if (clazz == null) {
                    clazz = this.findClass(className);
                }
            }
        }

        return clazz;
    }

    protected Class<?> loadClass(String className, boolean resolve)
            throws ClassNotFoundException {
        synchronized (this.getClassLoadingLock(className)) {

            Class clazz = loadClass0(className, true);
            if (resolve) {
                this.resolveClass(clazz);
            }

            return clazz;
        }
    }
}
