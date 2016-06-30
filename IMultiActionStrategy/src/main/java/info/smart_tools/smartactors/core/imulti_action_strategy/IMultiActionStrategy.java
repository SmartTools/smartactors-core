package info.smart_tools.smartactors.core.imulti_action_strategy;

import info.smart_tools.smartactors.core.iaction.IFunction;
import info.smart_tools.smartactors.core.imulti_action_strategy.exception.FunctionNotFoundException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;

/**
 * Interface IMultiActionStrategy
 * @param <T> type of action unique identifier
 */
public interface IMultiActionStrategy<T> extends IResolveDependencyStrategy {

    /**
     * Add new function with given id to the start of locale storage
     * @param id the id of new function
     * @param function the new adding function
     * @throws InvalidArgumentException if incoming arguments are incorrect
     */
    void addToStart(T id, IFunction function)
            throws InvalidArgumentException;

    /**
     * Add function with given id to the end of locale storage
     * @param id the id of new function
     * @param function the new adding function
     * @throws InvalidArgumentException if incoming arguments are incorrect
     */
    void addToEnd(T id, IFunction function)
            throws InvalidArgumentException;

    /**
     * Add new function with given id before function with specified id
     * @param idBeforeFunction the id of function that should be after new function
     * @param id the id of new function
     * @param function the new adding function
     * @throws InvalidArgumentException if incoming arguments are incorrect
     * @throws FunctionNotFoundException if function with idBeforeFunction not found
     */
    void addBefore(T idBeforeFunction, T id, IFunction function)
            throws InvalidArgumentException, FunctionNotFoundException;

    /**
     * Add new function with given id after function with specified id
     * @param idAfterFunction the id of function that should be before new function
     * @param id the id of new function
     * @param function the new adding function
     * @throws InvalidArgumentException if incoming arguments are incorrect
     * @throws FunctionNotFoundException if function with idBeforeFunction not found
     */
    void addAfter(T idAfterFunction, T id, IFunction function)
            throws InvalidArgumentException, FunctionNotFoundException;
}
