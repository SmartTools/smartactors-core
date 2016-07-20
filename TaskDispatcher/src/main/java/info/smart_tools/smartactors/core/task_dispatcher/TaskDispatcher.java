package info.smart_tools.smartactors.core.task_dispatcher;

import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.itask_dispatcher.ITaskDispatcher;
import info.smart_tools.smartactors.core.ithread_pool.IThreadPool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of {@link ITaskDispatcher}.
 */
public class TaskDispatcher implements ITaskDispatcher {
    private final IQueue<ITask> taskQueue;
    private final IThreadPool threadPool;
    private final AtomicInteger runningThreadCount;

    private final long maxExecutionDelay;
    private final int maxRunningThreads;

    /**
     * The time in nanoseconds (as returned by {@link System#nanoTime()}) when the last task was taken from queue.
     */
    private final AtomicLong lastTakeTime;

    private final Runnable callback;
    private final ExecutionTask executionTask;

    /**
     * The constructor.
     *
     * @param taskQueue            the queue to take tasks from
     * @param threadPool           the thread pool to use to execute tasks
     * @param maxExecutionDelay    the maximum delay between starts of execution of tasks
     * @param maxRunningThreads    the maximum amount of threads to start on new enqueued tasks when there seems to be
     *                             no long tasks
     */
    public TaskDispatcher(final IQueue<ITask> taskQueue, final IThreadPool threadPool,
                          final long maxExecutionDelay, final int maxRunningThreads) {
        this.taskQueue = taskQueue;
        this.threadPool = threadPool;

        this.maxExecutionDelay = maxExecutionDelay;
        this.maxRunningThreads = maxRunningThreads;

        this.executionTask = new ExecutionTask(this);
        this.callback = new Runnable() {
            @Override
            public void run() {
                tryStartNewThread();
            }
        };

        this.runningThreadCount = new AtomicInteger(0);
        this.lastTakeTime = new AtomicLong(0);
    }

    /**
     * Should be called when a task is taken from queue.
     */
    void notifyTaskTaken() {
        lastTakeTime.set(System.nanoTime());
    }

    /**
     * Should be called when new thread starts taking tasks from queue.
     */
    void notifyThreadStart() {
        runningThreadCount.incrementAndGet();
    }

    /**
     * Should be called when a thread stops taking tasks from queue.
     */
    void notifyThreadStop() {
        runningThreadCount.decrementAndGet();
    }

    /**
     * Starts (takes from pool) new thread executing the {@link #executionTask} if it is necessary.
     */
    void tryStartNewThread() {
        if (!shouldStartNewThread()) {
            return;
        }

        try {
            threadPool.tryExecute(executionTask);
        } catch (final TaskExecutionException e) {
            // TODO: Handle?
        }
    }

    /**
     * Decide if new thread executing {@link #executionTask} should be started.
     *
     * @return {@code true} if new thread should be started.
     */
    protected boolean shouldStartNewThread() {
        int runningThreads = runningThreadCount.get();
        long timeDelta = System.nanoTime() - lastTakeTime.get();

        return runningThreads < maxRunningThreads || timeDelta > maxExecutionDelay;
    }

    IThreadPool getThreadPool() {
        return threadPool;
    }

    IQueue<ITask> getTaskQueue() {
        return taskQueue;
    }

    @Override
    public void start() {
        lastTakeTime.set(System.nanoTime());
        taskQueue.addNewItemCallback(callback);
    }

    @Override
    public void stop() {
        taskQueue.removeNewItemCallback(callback);
    }
}
