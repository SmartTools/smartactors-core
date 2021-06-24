package info.smart_tools.smartactors.launcher.interfaces.iaction;


import info.smart_tools.smartactors.launcher.interfaces.exception.iaction.ActionExecutionException;

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
