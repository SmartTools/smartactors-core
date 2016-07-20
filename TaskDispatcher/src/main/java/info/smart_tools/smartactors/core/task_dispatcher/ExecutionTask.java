package info.smart_tools.smartactors.core.task_dispatcher;

import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

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
            while (!Thread.interrupted()) {
                ITask task = taskDispatcher.getTaskQueue().tryTake();

                if (null == task) {
                    return;
                }

                taskDispatcher.notifyTaskTaken();

                taskDispatcher.getThreadPool().tryExecute(this);

                task.execute();
            }
        } finally {
            taskDispatcher.notifyThreadStop();
        }
    }
}
