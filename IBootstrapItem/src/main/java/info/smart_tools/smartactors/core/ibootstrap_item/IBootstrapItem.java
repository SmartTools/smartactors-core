package info.smart_tools.smartactors.core.ibootstrap_item;


import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.ibootstrap_item.exception.ProcessExecutionException;
import info.smart_tools.smartactors.core.ibootstrap_item.exception.RevertProcessExecutionException;

import java.util.List;

/**
 * Interface for atomic step of plugin loading chain
 * @param <T> type of item name
 */
public interface IBootstrapItem <T> {

    /**
     * Add name of {@link IBootstrapItem} instance what should be loaded before current item
     * Must support fluent interface
     * @param itemName name of {@link IBootstrapItem} instance what should be loaded before current item
     * @return current instance of {@link IBootstrapItem}
     */
    IBootstrapItem before(T itemName);

    /**
     * Add name of {@link IBootstrapItem} instance what should be loaded after current item
     * Must support fluent interface
     * @param itemName name of {@link IBootstrapItem} instance what should be loaded after current item
     * @return current instance of {@link IBootstrapItem}
     */
    IBootstrapItem after(T itemName);

    /**
     * Action for execution for loading current item to the system
     * Must support fluent interface
     * @param process instance of {@link IPoorAction} or function
     * @return current instance of {@link IBootstrapItem}
     */
    IBootstrapItem process(IPoorAction process);

    /**
     * Action for revert process action (unload current item from the server)
     * Must support fluent interface
     * @param process instance of {@link IPoorAction} or function
     * @return current instance of {@link IBootstrapItem}
     */
    IBootstrapItem revertProcess(IPoorAction process);

    /**
     * Execute process action
     * @throws ProcessExecutionException if any error occurred
     */
    void executeProcess() throws ProcessExecutionException;

    /**
     * Execute revert process action
     * @throws RevertProcessExecutionException if any error occurred
     */
    void executeRevertProcess() throws RevertProcessExecutionException;

    /**
     * Get all names of dependencies that should be loaded before current
     * @return list of names
     */
    List<T> getBeforeItems();

    /**
     * Get all names of dependencies that should be loaded after current
     * @return list of names
     */
    List<T> getAfterItems();

    /**
     * Rreturn name of current item
     * @return name of current item
     */
    T getItemName();
}
