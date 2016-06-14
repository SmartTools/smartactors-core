package info.smart_tools.smartactors.core.scope_guard;

import info.smart_tools.smartactors.core.scope_guard.exception.ScopeGuardException;


/**
 * ScopeGuard interface
 * provides methods for switch current {@link info.smart_tools.smartactors.core.iscope.IScope}
 * by other
 * @since 1.7+
 */
public interface IScopeGuard extends AutoCloseable {

    /**
     * Locally save and substitute current instance of {@link info.smart_tools.smartactors.core.iscope.IScope} by
     * other
     * @param key unique identifier for find {@link info.smart_tools.smartactors.core.iscope.IScope}
     * instance in a scope storage
     * @throws  ScopeGuardException if any errors occurred
     */
    void guard(final Object key)
            throws ScopeGuardException;

    /**
     * Set locally saved {@link info.smart_tools.smartactors.core.iscope.IScope} as current
     * @throws  ScopeGuardException if any errors occurred
     */
    void close() throws ScopeGuardException;
}
