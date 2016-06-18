package info.smart_tools.smartactors.core.iaction;

import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;

/**
 * Interface IPoorAction.
 * Action without incoming argument.
 */
public interface IPoorAction {
    /**
     * Action without incoming arguments
     * @throws ActionExecuteException if any errors occurred
     */
    void execute()
            throws ActionExecuteException;
}
