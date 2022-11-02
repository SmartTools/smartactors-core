package info.smart_tools.smartactors.scope.iscope_guard;

import info.smart_tools.smartactors.scope.iscope_guard.exception.ScopeGuardException;

/**
 * ScopeGuard interface
 * provides methods for switch current {@link info.smart_tools.smartactors.scope.iscope.IScope}
 * by other.
 * <p>
 *     You pass the scope where you want to switch to to the constructor of this object.
 *     And when the Guard is closed, the initial scope is restored.
 * </p>
 * {@link AutoCloseable} requires JRE version 1.7+.
 */
public interface IScopeGuard extends AutoCloseable {

    /**
     * Set locally saved {@link info.smart_tools.smartactors.scope.iscope.IScope} as current
     * @throws ScopeGuardException if any errors occurred
     */
    void close() throws ScopeGuardException;
}
