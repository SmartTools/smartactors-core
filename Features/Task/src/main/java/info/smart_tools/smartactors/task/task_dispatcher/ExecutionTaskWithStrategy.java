package info.smart_tools.smartactors.task.task_dispatcher;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.task.imanaged_task.IManagedTask;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.exceptions.TaskProcessException;

/**
 * A task that acts as {@link ExecutionTask} but uses {@link ITaskProcessStrategy} to process tasks.
 */
public class ExecutionTaskWithStrategy implements ITask {
    private final TaskDispatcher taskDispatcher;
    private final ITaskProcessStrategy taskProcessStrategy;

    /**
     * Implementation of {@link ITaskExecutionState}.
     */
    private class ExecutionState implements ITaskExecutionState {
        private ITask task;

        void setTask(final ITask task) {
            this.task = task;
        }

        @Override
        public void execute()
                throws TaskExecutionException {
            task.execute();
        }

        @Override
        public <T> T getTaskAs(final Class<T> clazz)
                throws InvalidArgumentException {
            IManagedTask managedTask;

            try {
                managedTask = (IManagedTask) task;
            } catch (ClassCastException e) {
                throw new InvalidArgumentException("Can not get object of type " + clazz.getCanonicalName() + " for task " + task);
            }

            return managedTask.getAs(clazz);
        }

        @Override
        public Class<? extends ITask> getTaskClass() {
            return task.getClass();
        }
    }

    /**
     * The constructor.
     *
     * @param taskDispatcher         the task dispatcher
     * @param taskProcessStrategy    the strategy to use
     */
    ExecutionTaskWithStrategy(
            final TaskDispatcher taskDispatcher, final ITaskProcessStrategy taskProcessStrategy) {
        this.taskDispatcher = taskDispatcher;
        this.taskProcessStrategy = taskProcessStrategy;
    }

    @Override
    public void execute() throws TaskExecutionException {
        final ExecutionState state = new ExecutionState();

        taskDispatcher.notifyThreadStart();

        try {
            while (taskDispatcher.getExecutionTask() == this) {
                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    return;
                }

                ITask task = taskDispatcher.getTaskQueue().tryTake();

                if (null == task) {
                    return;
                }

                taskDispatcher.notifyTaskTaken();

                taskDispatcher.getThreadPool().tryExecute(this);

                state.setTask(task);

                try {
                    taskProcessStrategy.process(state);
                } catch (TaskProcessException | InvalidArgumentException e) {
                    throw new TaskExecutionException(e);
                }
            }

            taskDispatcher.getThreadPool().tryExecute(taskDispatcher.getExecutionTask());
        } finally {
            taskDispatcher.notifyThreadStop();
        }
    }
}
