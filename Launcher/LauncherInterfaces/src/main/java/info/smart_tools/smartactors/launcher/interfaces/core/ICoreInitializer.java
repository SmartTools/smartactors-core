package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.CoreInitializerException;

import java.util.List;

public interface ICoreInitializer {

    void initializeCoreFeatures(List<IPath> coreFeatures) throws CoreInitializerException;
}
