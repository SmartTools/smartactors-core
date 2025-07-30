package info.smart_tools.smartactors.scope.iscope;

import info.smart_tools.smartactors.scope.iscope.exception.ScopeException;

/**
 * Scope interface
 *
 * Scope is a key-value storage.
 *
 */
public interface IScope {

    /**
     * Get a value by the key from the scope
     * @param key given key
     * @return found object
     * @throws ScopeException if value is not found or any error occurred
     */
    Object getValue(final Object key)
            throws ScopeException;

    /**
     * Stores an value to the scope associating it with defined key
     * @param key given key
     * @param value given value
     * @throws ScopeException any if error occurred
     */
    void setValue(final Object key, final Object value)
            throws ScopeException;

    /**
     * Removes the value associated with key
     * @param key given key
     * @throws ScopeException if any error occurred
     */
    void deleteValue(final Object key)
            throws ScopeException;
}
