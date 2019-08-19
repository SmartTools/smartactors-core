package info.smart_tools.smartactors.base.interfaces.iaction;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;

/**
 * Interface IAction.
 * @param <T> type of acting object
 */
public interface IAction<T> {

    /**
     * Action for acting object
     * @param actingObject acting object
     * @throws ActionExecutionException if any errors occurred
     * @throws InvalidArgumentException if incoming argument are incorrect
     */
    void execute(T actingObject)
            throws ActionExecutionException, InvalidArgumentException;
}
