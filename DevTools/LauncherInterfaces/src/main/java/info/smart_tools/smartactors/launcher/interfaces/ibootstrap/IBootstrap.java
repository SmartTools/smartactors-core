package info.smart_tools.smartactors.launcher.interfaces.ibootstrap;

import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap.ProcessExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap.RevertProcessExecutionException;

/**
 * Wrapper around bootstrap instance
 */
public interface IBootstrap {

    /**
     * Start bootstrap
     *
     * @throws ProcessExecutionException if failed to start bootstrap
     */
    void start() throws ProcessExecutionException;

    /**
     * Revert bootstrap
     *
     * @throws RevertProcessExecutionException if failed to revert bootstrap
     */
    void revert() throws RevertProcessExecutionException;

    /**
     * Get wrapped instance of bootstrap
     *
     * @return instance of bootstrap
     */
    Object getInstance();
}
