package info.smart_tools.smartactors.scope.scope_guard;

import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_guard.IScopeGuard;
import info.smart_tools.smartactors.scope.iscope_guard.exception.ScopeGuardException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;

/**
 * Implementation of {@link IScopeGuard}
 * @since 1.7+
 */
public class ScopeGuard implements IScopeGuard {

    /**
     * Local storage for instance of {@link IScope}
     */
    private IScope previousScope;

    /**
     * Locally saves and substitutes current instance of {@link IScope} by
     * other
     * @param key unique identifier for find {@link IScope}
     * @throws ScopeGuardException if any errors occurred
     */
    public ScopeGuard(final Object key)
            throws ScopeGuardException {
        try {
            previousScope = ScopeProvider.getCurrentScope();
            ScopeProvider.setCurrentScope(ScopeProvider.getScope(key));
        } catch (Exception e) {
            throw new ScopeGuardException("ScopeGuard could not to switch scope.", e);
        }
    }

    /**
     * Sets locally saved {@link IScope} as current
     * @throws ScopeGuardException if any errors occurred
     */
    public void close() throws ScopeGuardException {
        try {
            if (previousScope != null) {
                ScopeProvider.setCurrentScope(previousScope);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new ScopeGuardException("ScopeGuard could not restore original state.", e);
        }
    }
}
