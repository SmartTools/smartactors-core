package info.smart_tools.smartactors.launcher.interfaces;

import java.net.URL;

/**
 * Wrapper for class loader
 */
public interface IClassLoaderWrapper {

    /**
     * Add {@link URL} to the current wrapped class loader if it doesn't contain this {@link URL} yet
     * @param url instance of {@link URL}
     */
    void addURL(final URL url);

    /**
     * Load class with wrapped class loader
     * @param className name of the class to load
     * @return reference to loaded class
     * @throws ClassNotFoundException if class with provided name was not found in wrapper class loader
     */
    Class<?> loadClass(String className) throws ClassNotFoundException;
}
