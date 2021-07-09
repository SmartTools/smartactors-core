package info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap;

import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.RevertProcessExecutionException;

import java.util.List;
import java.util.Map;

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
     * then re-order bootstrap items correspondingly and
     * execute item .process() action for each bootstrap item
     * @return the list of loaded items
     * @throws ProcessExecutionException if any errors occurred
     */
    List<T> start()
            throws ProcessExecutionException;

    /**
     * Revert all actions done while start method was being executed
     * @throws RevertProcessExecutionException if any errors occurred
     */
    void revert()
            throws RevertProcessExecutionException;
}
