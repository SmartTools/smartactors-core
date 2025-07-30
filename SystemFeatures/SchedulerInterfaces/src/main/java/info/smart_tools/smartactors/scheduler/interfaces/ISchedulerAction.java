package info.smart_tools.smartactors.scheduler.interfaces;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionInitializationException;

/**
 * Action executed when scheduler entry fires.
 */
public interface ISchedulerAction {
    /**
     * Prepare the entry for action execution.
     *
     * @param entry    the entry
     * @param args     the arguments object the entry was created with. Strategy may copy some values from this object to entry state.
     * @throws SchedulerActionInitializationException if any error occurs
     */
    void init(ISchedulerEntry entry, IObject args) throws SchedulerActionInitializationException;

    /**
     * Execute the action on the entry.
     *
     * @param entry    the entry to execute action on
     * @throws SchedulerActionExecutionException if any error occurs executing the action
     */
    void execute(ISchedulerEntry entry) throws SchedulerActionExecutionException;
}
