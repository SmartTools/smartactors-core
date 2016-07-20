package info.smart_tools.smartactors.core.examples.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  A sample of container which takes care on the parent-child relationships.
 *  It's {@link #get} method queries parent container if cannot find the requested value in it's own storage.
 *  @param <K> type of the key for the container, should be {@link info.smart_tools.smartactors.core.ikey.IKey} for IOC.
 *  @param <V> type of the value for the container, should be Object for IOC.
 */
public class RecursiveContainer<K, V> {

    private final RecursiveContainer<K, V> parent;

    private final Map<K, V> storage = new ConcurrentHashMap<>();

    /**
     * Creates the container.
     * @param parent reference to the parent container, should be null for root container
     */
    public RecursiveContainer(final RecursiveContainer<K, V> parent) {
        this.parent = parent;
    }

    /**
     * Returns the value from the container.
     * If the value is absent in the current container asks the parent container.
     * @param key key to select the value
     * @return found value or null
     */
    public V get(final K key) {
        V result = null;
        result = storage.get(key);
        if (result == null && parent != null) {
            result = parent.get(key);
        }
        return result;
    }

    /**
     * Puts the value to the container.
     * The value is always put to the current container, the parent stays untouched.
     * @param key key to identify the value
     * @param value value to put to the container
     */
    public void put(final K key, final V value) {
        storage.put(key, value);
    }

}