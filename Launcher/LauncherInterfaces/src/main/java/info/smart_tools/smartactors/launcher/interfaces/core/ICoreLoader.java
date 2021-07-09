package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.CoreLoaderException;

import java.util.List;

public interface ICoreLoader {

    List<IPath> loadCoreFeature(String coreFeaturesPath) throws CoreLoaderException;
}
