package info.smart_tools.smartactors.launcher.interfaces;

import info.smart_tools.smartactors.launcher.interfaces.exception.launcher.LauncherExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.launcher.LauncherInitializeException;

import java.util.Map;

/**
 * Interface for realize custom servers
 */
public interface ILauncher {

    /**
     * Load, configure and initialize server components
     * @param parameters parameters for launcher to start with
     * @throws LauncherInitializeException if any errors occurred
     */
    Boolean initialize(Map<String, Object> parameters) throws LauncherInitializeException;

    /**
     * Launch main server components for execute assigned tasks
     * @throws LauncherExecutionException if any errors occurred
     */
    Boolean start() throws LauncherExecutionException;
}
