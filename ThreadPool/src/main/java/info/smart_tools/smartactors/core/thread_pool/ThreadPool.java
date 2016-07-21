package info.smart_tools.smartactors.core.thread_pool;

import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.ithread_pool.IThreadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Implementation of {@link IThreadPool}.
 */
public class ThreadPool implements IThreadPool {
    private final BlockingQueue<ThreadImpl> threadsQueue;

    /**
     * The constructor.
     *
     * @param threadCount    initial count of threads.
     */
    public ThreadPool(final int threadCount) {
        threadsQueue = new ArrayBlockingQueue<>(threadCount);

        for (int i = 0; i < threadCount; i++) {
            threadsQueue.offer(new ThreadImpl(this));
        }
    }

    @Override
    public boolean tryExecute(final ITask task)
            throws TaskExecutionException {
        ThreadImpl thread = threadsQueue.poll();

        if (null != thread) {
            thread.execute(task);
            return true;
        }

        return false;
    }

    /**
     * Returns the thread to this pool.
     *
     * @param thread the thread
     */
    void returnThread(final ThreadImpl thread) {
        if (!threadsQueue.offer(thread)) {
            thread.interrupt();
        }
    }
}
