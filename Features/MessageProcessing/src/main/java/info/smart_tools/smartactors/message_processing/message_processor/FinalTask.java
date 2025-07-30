package info.smart_tools.smartactors.message_processing.message_processor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.util.List;

/**
 * Implementation of {@link ITask}.
 * This task executes all final actions when main chain of {@link MessageProcessor} was completed.
 *
 */
public class FinalTask implements ITask {

    private IFieldName contextFieldName;
    private IFieldName finalActionsFieldName;

    private IObject env;

    /**
     * Constructor.
     * Creates instance of {@link FinalTask} by given environment
     *
     * @param environment the environment
     * @throws InvalidArgumentException if environment is null
     * @throws ResolutionException if resolution is impossible because of any errors
     */
    public FinalTask(final IObject environment)
            throws InvalidArgumentException, ResolutionException {
        if (null == environment) {
            throw new InvalidArgumentException("Environment should not be null.");
        }
        this.env = environment;
        contextFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context");
        finalActionsFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "finalActions");
    }

    @Override
    public void execute()
            throws TaskExecutionException {
        try {
            IObject context = (IObject) this.env.getValue(this.contextFieldName);
            if (null == context) {
                return;
            }
            List<IAction> actions = (List<IAction>) context.getValue(this.finalActionsFieldName);
            if (null == actions) {
                return;
            }
            for (IAction action : actions) {
                try {
                    action.execute(this.env);
                } catch (ActionExecutionException e) {
                    e.printStackTrace();
                }
            }

        } catch (InvalidArgumentException | ReadValueException e) {
            throw new TaskExecutionException("Could not execute final task.");
        }
    }
}
