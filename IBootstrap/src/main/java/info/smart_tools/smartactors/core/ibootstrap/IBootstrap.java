package info.smart_tools.smartactors.core.ibootstrap;

import info.smart_tools.smartactors.core.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.core.ibootstrap.exception.RevertProcessExecutionException;

/**
 * IBootstrap
 * Interface for storage, sort and execute atomic steps of plugin loading chain
 * @param <T> type of bootstrap name
 */
public interface IBootstrap <T> {

    /**
     * Add instance of atomic step of plugin loading chain to the bootstrap local storage
     * @param bootstrapItem instance of {@link T}
     */
    void add(T bootstrapItem);

    /**
     * Resolve bootstrap items dependencies,
     * order bootstrap items and
     * execute all processes for each bootstrap item
     * @throws ProcessExecutionException if any errors occurred
     */
    void start()
            throws ProcessExecutionException;

    /**
     * Revert changes that was doing after execution start method
     * @throws RevertProcessExecutionException if any errors occurred
     */
    void revert()
            throws RevertProcessExecutionException;
}
