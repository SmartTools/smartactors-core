package info.smart_tools.smartactors.core.examples.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  A sample of container which takes care on the parent-child relationships.
 *  It's {@link #get} method queries parent container if cannot find the requested value in it's own storage.
 */
public class MyRecursiveContainer<K, V> {

    private final MyRecursiveContainer<K, V> parent;

    private final Map<K, V> storage = new ConcurrentHashMap<>();

    public MyRecursiveContainer(final MyRecursiveContainer<K, V> parent) {
        this.parent = parent;
    }

    public V get(final K key) {
        V result = null;
        result = storage.get(key);
        if (result == null && parent != null) {
            result = parent.get(key);
        }
        return result;
    }

    public void put(final K key, final V value) {
        storage.put(key, value);
    }

}