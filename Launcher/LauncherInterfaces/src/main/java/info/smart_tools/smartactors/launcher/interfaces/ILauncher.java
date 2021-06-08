package info.smart_tools.smartactors.launcher.interfaces;

import info.smart_tools.smartactors.launcher.interfaces.exception.LauncherExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.LauncherInitializeException;

/**
 * Interface for realize custom servers
 */
public interface ILauncher {

    /**
     * Load, configure and initialize server components
     * @throws LauncherInitializeException if any errors occurred
     */
    void initialize()
            throws LauncherInitializeException;

    /**
     * Launch main server components for execute assigned tasks
     * @throws LauncherExecutionException if any errors occurred
     */
    void start()
            throws LauncherExecutionException;
}
