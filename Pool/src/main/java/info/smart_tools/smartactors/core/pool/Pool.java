package info.smart_tools.smartactors.core.pool;

import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.ipool.exception.PoolPutException;
import info.smart_tools.smartactors.core.ipool.exception.PoolTakeException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


/**
 * Implementation of {@link info.smart_tools.smartactors.core.ipool.IPool}
 */
public class Pool implements IPool {
    private final ArrayBlockingQueue<Object> freeItems;
    private AtomicInteger freeItemsCounter = new AtomicInteger();

    /**
     * Local function for creation new instances of items
     */
    private Supplier<Object> creationFunction;

    /**
     * Constructs new Pool
     * @param maxItems the maximum of active items.
     * @param func the function for creating new instances of items
     */
    public Pool(final Integer maxItems, final Supplier<Object>  func) {
        if (func == null) {
            throw new IllegalArgumentException("Function must be not null");
        }
        if (maxItems <= 0) {
            throw new IllegalArgumentException("Count of max items mast be more 0");
        }
        this.freeItems = new ArrayBlockingQueue<>(maxItems);
        this.freeItemsCounter.set(maxItems);
        this.creationFunction = func;
    }

    /**
     * Get a value by the key from the scope.
     * @throws PoolTakeException if error was occurred
     */
    public Object take() throws PoolTakeException {
        if (freeItemsCounter.getAndDecrement() <= 0) {
            freeItemsCounter.getAndIncrement();
            throw new PoolTakeException("Reached limit of items for this pool.");
        }

        try {
            Object result = freeItems.poll();
            if (result == null) {
                result = creationFunction.get();
            }

            return result;
        } catch (Exception e) {
            freeItemsCounter.getAndIncrement();
            throw new PoolTakeException("Failed to get item", e);
        }
    }

    /**
     * Stores a value to the pool.
     * @param item the item that now free.
     * @throws PoolPutException if any error occurred
     */
    public void put(final Object item) throws PoolPutException {
        try {
            if (freeItems.size() >= freeItemsCounter.get()) {
                freeItems.add(item);
                freeItemsCounter.getAndIncrement();
            }
        } catch (Exception e) {
            throw new PoolPutException("Error was occurred", e);
        }
    }
}
