package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.CoreInitializerException;

import java.util.List;

/**
 * Interface ICoreInitializer
 */
public interface ICoreInitializer {

    /**
     * Initialize loaded core features
     *
     * @param coreFeatures paths to loaded core features
     * @throws CoreInitializerException if failed to initialize core features
     */
    void initializeCoreFeatures(List<IPath> coreFeatures) throws CoreInitializerException;
}
