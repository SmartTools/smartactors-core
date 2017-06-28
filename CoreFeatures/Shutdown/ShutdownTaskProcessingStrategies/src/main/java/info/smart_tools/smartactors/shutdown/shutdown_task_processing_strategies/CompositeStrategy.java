package info.smart_tools.smartactors.shutdown.shutdown_task_processing_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.exceptions.TaskProcessException;

/**
 * {@link ITaskProcessStrategy Task process strategy} that chooses a strategy depending on task class.
 */
public class CompositeStrategy implements ITaskProcessStrategy {
    private final IKey strategyKey;
    private final ITaskProcessStrategy defaultStrategy;

    /**
     * @param strategyKey        key to use to resolve strategy using {@link IOC}
     * @param defaultStrategy    the default strategy to use
     */
    public CompositeStrategy(final IKey strategyKey, final ITaskProcessStrategy defaultStrategy) {
        this.strategyKey = strategyKey;
        this.defaultStrategy = defaultStrategy;
    }

    @Override
    public void process(final ITaskExecutionState state)
            throws TaskExecutionException, InvalidArgumentException, TaskProcessException {
        ITaskProcessStrategy strategy;

        try {
            strategy = IOC.resolve(strategyKey, state.getTaskClass());
        } catch (ResolutionException e) {
            strategy = defaultStrategy;
        }

        strategy.process(state);
    }
}
