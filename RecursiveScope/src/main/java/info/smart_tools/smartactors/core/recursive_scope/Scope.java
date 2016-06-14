package info.smart_tools.smartactors.core.recursive_scope;

import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope.exception.ScopeException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link info.smart_tools.smartactors.core.iscope.IScope}
 * with recursive call method getValue of parent scope
 */
class Scope implements IScope {

    private Map<Object, Object> storage = new ConcurrentHashMap<Object, Object>();
    private IScope parent;

    /**
     * Constructs new Scope with defined parent scope
     * @param parent the parent scope.
     */
    Scope(final IScope parent) {
        this.parent = parent;
    }

    /**
     * Get a value by the key from the scope.
     * @param key given key
     * @return the found value
     * @throws ScopeException if value is not found or error was occurred
     */
    public Object getValue(final Object key)
            throws ScopeException {
        Object value = storage.get(key);

        if (null == value) {
            try {
                return parent.getValue(key);
            } catch (NullPointerException e) {
                throw new ScopeException("Value not found", e);
            }
        }
        return value;
    }

    /**
     * Stores a value to the scope associating it with defined key
     * @param key given key
     * @param value given value
     * @throws ScopeException if error was occurred
     */
    public void setValue(final Object key, final Object value)
            throws ScopeException {
        try {
            storage.put(key, value);
        } catch (Exception e) {
            throw new ScopeException("Error was occurred", e);
        }
    }

    /**
     * Removes the value associated with key if any
     * @param key given key
     * @throws ScopeException if any error was occurred
     */
    public void deleteValue(final Object key)
            throws ScopeException {
        try {
            storage.remove(key);
        } catch (Exception e) {
            throw new ScopeException("Error was occurred", e);
        }
    }
}
