package info.smart_tools.smartactors.task.task_dispatcher;

import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * The task that is started by {@link TaskDispatcher}. It takes tasks from queue and executes them trying to start
 * itself in new threads (from thread pool).
 */
class ExecutionTask implements ITask {
    private final TaskDispatcher taskDispatcher;

    /**
     * The constructor.
     *
     * @param taskDispatcher the task dispatcher instance that created this task.
     */
    ExecutionTask(final TaskDispatcher taskDispatcher) {
        this.taskDispatcher = taskDispatcher;
    }

    @Override
    public void execute() throws TaskExecutionException {
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

                task.execute();
            }

            taskDispatcher.getThreadPool().tryExecute(taskDispatcher.getExecutionTask());
        } finally {
            taskDispatcher.notifyThreadStop();
        }
    }
}
