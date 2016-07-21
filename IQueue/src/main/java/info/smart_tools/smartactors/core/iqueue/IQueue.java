package info.smart_tools.smartactors.core.iqueue;

/**
 * Blocking queue.
 *
 * @param <T> element type.
 */
public interface IQueue <T> {
    /**
     * Put the item to the tail of this queue.
     *
     * @param item the item to put into queue
     * @throws InterruptedException if thread is interrupted
     */
    void put(T item) throws InterruptedException;

    /**
     * Take the element from the head of the queue blocking if the queue is empty.
     *
     * @return the element from head of queue
     * @throws InterruptedException if thread is interrupted
     */
    T take() throws InterruptedException;

    /**
     * Take the element from the head of the queue returning {@code null} if the queue is empty.
     *
     * @return the element from head of queue or {@code null} if queue is empty
     */
    T tryTake();

    /**
     * Add a {@link Runnable} to run when new item appears in queue. The runnable will be executed immediately if there
     * is any item in queue.
     *
     * @param callback the runnable to execute when new element is added to queue
     */
    void addNewItemCallback(Runnable callback);

    /**
     * Remove a {@link Runnable} added by {@link #addNewItemCallback(Runnable)}.
     *
     * @param callback the runnable that should no more be run when new element is added to queue
     * @see #addNewItemCallback(Runnable)
     */
    void removeNewItemCallback(Runnable callback);
}
