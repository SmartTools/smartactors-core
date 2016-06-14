package info.smart_tools.smartactors.core.iserver;

import info.smart_tools.smartactors.core.iserver.exception.ServerExecutionException;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;

/**
 * Interface for realize custom servers
 */
public interface IServer {

    /**
     * Load, configure and initialize server components
     * @throws ServerInitializeException if any errors occurred
     */
    void initialize()
            throws ServerInitializeException;

    /**
     * Launch main server components for execute assigned tasks
     * @throws ServerExecutionException if any errors occurred
     */
    void start()
            throws ServerExecutionException;
}
