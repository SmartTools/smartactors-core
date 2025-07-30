package info.smart_tools.smartactors.shutdown.shutdown_task_processing_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.IShutdownAwareTask;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.exceptions.ShutdownAwareTaskNotificationException;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.exceptions.TaskProcessException;

/**
 * Strategy that gives a number of trials for task to complete.
 */
public class LimitTrialCountStrategy implements ITaskProcessStrategy {
    private final int maxTrials, silentTrials;

    /**
     * The trial counter that will be stored as task shutdown status.
     */
    private class TrialCounter {
        private int state = 0;

        int increment() {
            return ++state;
        }
    }

    /**
     * The constructor.
     *
     * @param maxTrials       maximum amount of trials for task to execute
     * @param silentTrials    number of trials the task will not be notified on shutdown request
     * @throws InvalidArgumentException if {@code maxTrials} is not a positive number
     * @throws InvalidArgumentException if {@code silentTrials} is negative or greater than {@code maxTrials}
     */
    public LimitTrialCountStrategy(final int maxTrials, final int silentTrials)
            throws InvalidArgumentException {
        if (maxTrials <= 0) {
            throw new InvalidArgumentException("Maximum trials number should be > 0");
        }

        if (silentTrials > maxTrials || silentTrials < 0) {
            throw new InvalidArgumentException("Silent trials number should be positive and not greater than total trials number.");
        }

        this.maxTrials = maxTrials;
        this.silentTrials = silentTrials;
    }

    @Override
    public void process(final ITaskExecutionState state)
            throws TaskExecutionException, InvalidArgumentException, TaskProcessException {
        IShutdownAwareTask shutdownAwareTask = state.getTaskAs(IShutdownAwareTask.class);

        TrialCounter counter = (TrialCounter) shutdownAwareTask.getShutdownStatus();

        if (null == counter) {
            counter = new TrialCounter();
            shutdownAwareTask.setShutdownStatus(counter);
        }

        int counterState = counter.increment();

        try {
            if (counterState > maxTrials) {
                shutdownAwareTask.notifyIgnored();
            } else {
                if (counterState > silentTrials) {
                    shutdownAwareTask.notifyShuttingDown();
                }

                state.execute();
            }
        } catch (ShutdownAwareTaskNotificationException e) {
            throw new TaskProcessException(e);
        }
    }
}
