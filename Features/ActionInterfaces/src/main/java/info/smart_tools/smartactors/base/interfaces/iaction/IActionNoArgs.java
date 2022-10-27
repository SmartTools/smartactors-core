package info.smart_tools.smartactors.base.interfaces.iaction;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;

/**
 * Interface IActionNoArgs.
 * Action without incoming argument.
 */
public interface IActionNoArgs {
    /**
     * Action without incoming arguments
     * @throws ActionExecutionException if any errors occurred
     */
    void execute()
            throws ActionExecutionException;
}
