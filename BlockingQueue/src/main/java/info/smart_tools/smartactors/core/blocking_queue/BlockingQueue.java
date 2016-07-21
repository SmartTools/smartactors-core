package info.smart_tools.smartactors.core.blocking_queue;

import info.smart_tools.smartactors.core.iqueue.IQueue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of {@link IQueue} that is a facade for standard java queue.
 *
 * @param <T> type of element.
 */
public class BlockingQueue <T> implements IQueue <T> {
    private final java.util.concurrent.BlockingQueue<T> queue;
    private final List<Runnable> newElementCallbacks;
    private final Object callbacksListLock;

    /**
     * The constructor.
     *
     * @param queue the underlying standard queue
     */
    public BlockingQueue(final java.util.concurrent.BlockingQueue<T> queue) {
        this.queue = queue;
        this.newElementCallbacks = new CopyOnWriteArrayList<>();
        this.callbacksListLock = new Object();
    }

    @Override
    public void put(final T item) throws InterruptedException {
        queue.put(item);

        for (Runnable callback : newElementCallbacks) {
            callback.run();
        }
    }

    @Override
    public T take() throws InterruptedException {
        return queue.take();
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
