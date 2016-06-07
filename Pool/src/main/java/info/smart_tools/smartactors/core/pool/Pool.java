package info.smart_tools.smartactors.core.pool;

import info.smart_tools.smartactors.core.pool.exception.PoolException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


/**
 * Pool
 */
public class Pool {
    private final CopyOnWriteArrayList<Object> freeItems = new CopyOnWriteArrayList<>();
    private AtomicInteger freeItemsCounter = new AtomicInteger();

    /**
     * Local function for creation new instances of items
     */
    private Supplier<Object> creationFunction;

    /**
     * Constructs new Scope with defined parent scope
     * @param maxItems the maximum of active items.
     * @param func the function for creating new instances of items
     */
    public Pool(final Integer maxItems, final Supplier<Object>  func) {
        this.freeItemsCounter.set(maxItems);
        if (func == null) {
            throw new IllegalArgumentException("Incoming argument should not be null.");
        }
        this.creationFunction = func;
    }

    /**
     * Get a value by the key from the scope.
     * @throws PoolException if error was occurred
     */
    public Object tryTake() throws PoolException {
        if (freeItemsCounter.getAndDecrement() <= 0) {
            freeItemsCounter.incrementAndGet();
            return null;
        }

        if (freeItems.isEmpty()) {
            return creationFunction.get();
        }

        Object item = freeItems.get(0);
        freeItems.remove(item);
        return item;
    }

    /**
     * Stores a value to the pool.
     * @param item the item that now free.
     * @throws PoolException if any error occured
     */
    public void put(final Object item) throws PoolException {
        freeItems.add(item);
        freeItemsCounter.getAndIncrement();
    }
}
