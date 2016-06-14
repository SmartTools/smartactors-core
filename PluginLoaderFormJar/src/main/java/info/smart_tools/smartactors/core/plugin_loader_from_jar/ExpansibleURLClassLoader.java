package info.smart_tools.smartactors.core.plugin_loader_from_jar;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * Extension of {@link URLClassLoader}
 */
public class ExpansibleURLClassLoader extends URLClassLoader {

    /**
     * Redefined constructor
     * @param urls the URLs from which to load classes and resources
     */
    public ExpansibleURLClassLoader(final URL[] urls) {
        super(urls);
    }

    /**
     * Redefined constructor
     * @param urls the URLs from which to load classes and resources
     * @param parent the parent class loader for delegation
     */
    public ExpansibleURLClassLoader(final URL[] urls, final ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * Add new instance of {@link URL} to the current url class loader if url class loader doesn't contain this instance of {@link URL}
     * @param url instance of {@link URL}
     */
    public void addUrl(final URL url) {
        URL[] urls = getURLs();
        if (Arrays.asList(urls).contains(url)) {
            return;
        }
        addURL(url);
    }
}
