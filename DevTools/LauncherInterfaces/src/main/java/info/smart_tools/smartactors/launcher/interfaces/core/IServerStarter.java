package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.ServerStarterException;

import java.util.List;

/**
 * Interface ICoreInitializer
 */
public interface IServerStarter {

    /**
     * Initialize loaded core features
     *
     * @param classLoader class loader where to load feature classes
     * @param coreFeatures paths to loaded core features
     * @throws ServerStarterException if failed to initialize core features
     * @return is core initialized
     */
    boolean initServerFeatures(
            IClassLoaderWrapper classLoader,
            List<IPath> coreFeatures
    ) throws ServerStarterException;
}
