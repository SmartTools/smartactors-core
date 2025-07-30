package info.smart_tools.smartactors.task.non_blocking_queue;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Non-blocking implementation of {@link IQueue}.
 *
 * @param <T> type of elements
 */
public class NonBlockingQueue<T> implements IQueue<T> {
    private final Queue<T> queue;
    private final List<Runnable> newElementCallbacks;
    private final Object callbacksListLock;

    /**
     * The constructor.
     *
     * @param queue    underlying standard queue
     * @throws InvalidArgumentException if queue queue is {@code null}
     */
    public NonBlockingQueue(final Queue<T> queue)
            throws InvalidArgumentException {
        if (null == queue) {
            throw new InvalidArgumentException("Internal queue may not be null.");
        }

        this.queue = queue;
        this.newElementCallbacks = new CopyOnWriteArrayList<>();
        this.callbacksListLock = new Object();
    }

    @Override
    public void put(final T item) throws InterruptedException {
        queue.add(item);

        for (Runnable callback : newElementCallbacks) {
            callback.run();
        }
    }

    @Override
    public T take() throws InterruptedException {
        throw new UnsupportedOperationException("Blocking take operation is not supported.");
    }

    @Override
    public T tryTake() {
        return queue.poll();
    }

    @Override
    public void addNewItemCallback(final Runnable callback) {
        synchronized (callbacksListLock) {
            newElementCallbacks.add(callback);

            if (!queue.isEmpty()) {
                callback.run();
            }
        }
    }

    @Override
    public void removeNewItemCallback(final Runnable callback) {
        synchronized (callbacksListLock) {
            newElementCallbacks.remove(callback);
        }
    }
}
