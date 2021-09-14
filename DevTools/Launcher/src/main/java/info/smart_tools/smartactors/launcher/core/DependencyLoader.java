package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyLoader;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.DependencyLoaderException;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

public class DependencyLoader implements IDependencyLoader {
    
    @Override
    public void load(
            final IClassLoaderWrapper classLoader,
            final List<IPath> coreFeatures
    ) throws DependencyLoaderException {
        String featurePath = null;
        try {
            for (IPath coreFeature : coreFeatures) {
                featurePath = coreFeature.getPath();
                URL url = new URL("jar:file:" + coreFeature.getPath() + "!/");
                classLoader.addURL(url);
            }
        } catch (MalformedURLException e) {
            throw new DependencyLoaderException(MessageFormat.format("Failed to form URL for feature {0}", featurePath));
        }
    }
}
