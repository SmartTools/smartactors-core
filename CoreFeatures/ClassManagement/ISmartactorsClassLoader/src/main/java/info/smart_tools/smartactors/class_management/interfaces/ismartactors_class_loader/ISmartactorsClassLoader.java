package info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader;

import java.net.URL;

/**
 * Interface ISmartactorsClassLoader
 */
public interface ISmartactorsClassLoader {

    void setDefault();

    void addDependency(ISmartactorsClassLoader base);

    /**
     * Add {@link URL} to the current url class loader if url class loader doesn't contain this {@link URL} yet
     * @param url instance of {@link URL}
     */
    public void addURL(URL url);

    /**
     * Returns the search path of URLs for loading classes and resources.
     * This includes the original list of URLs specified to the constructor,
     * along with any URLs subsequently appended by the addURL() method.
     * @return the search path of URLs for loading classes and resources.
     */
    URL[] getURLsFromDependencies();

    Class<?> loadClass(String className) throws ClassNotFoundException;

    /**
     * Add compiled byte code of the class directly to this class loader
     * @param className The name of the class to define
     * @param classByteCode Compiled byte code of the class to add
     * @return The reference to the class
     */
    Class<?> addClass(String className, byte[] classByteCode);

    ClassLoader getCompilationClassLoader();
}
