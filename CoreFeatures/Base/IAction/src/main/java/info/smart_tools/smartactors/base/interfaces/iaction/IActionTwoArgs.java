package info.smart_tools.smartactors.base.interfaces.iaction;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;

/**
 * Interface IActionTwoArgs
 * @param <T1> type of first acting object
 * @param <T2> type of second acting object
 */
public interface IActionTwoArgs<T1, T2> {

    /**
     * Action for acting object with two parameters
     * @param firstActingObject first acting object
     * @param secondActingObject second acting object
     * @throws ActionExecutionException if any errors occurred
     * @throws InvalidArgumentException if incoming argument are incorrect
     */
    void execute(T1 firstActingObject, T2 secondActingObject)
            throws ActionExecutionException, InvalidArgumentException;
}
