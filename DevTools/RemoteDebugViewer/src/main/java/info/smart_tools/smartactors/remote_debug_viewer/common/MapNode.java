package info.smart_tools.smartactors.remote_debug_viewer.common;

public class MapNode<T> {

    private final String key;
    private final T value;

    public MapNode(final String key, final T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MapNode ("+ key + "=" + value + ')';
    }
}
