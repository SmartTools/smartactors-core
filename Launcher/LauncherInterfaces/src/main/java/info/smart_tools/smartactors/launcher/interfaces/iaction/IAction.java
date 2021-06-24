package info.smart_tools.smartactors.launcher.interfaces.iaction;


import info.smart_tools.smartactors.launcher.interfaces.exception.iaction.ActionExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iaction.InvalidArgumentException;

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
    void execute(final T actingObject)
            throws ActionExecutionException, InvalidArgumentException;
}
