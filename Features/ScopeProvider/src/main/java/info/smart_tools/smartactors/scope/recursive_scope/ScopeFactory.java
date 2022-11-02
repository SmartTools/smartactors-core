package info.smart_tools.smartactors.scope.recursive_scope;

import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope.IScopeFactory;
import info.smart_tools.smartactors.scope.iscope.exception.ScopeFactoryException;

/**
 * Implementation of {@link IScopeFactory}
 */
public class ScopeFactory implements IScopeFactory {

    /**
     * Create instance of {@link IScope}
     * @param obj needed parameters for creation
     * @return new instance of {@link IScope}
     * @throws ScopeFactoryException if any errors occurred
     */
    public IScope createScope(final Object obj) throws ScopeFactoryException {
        try {
            return new Scope((IScope) obj);
        } catch (Exception e) {
            throw new ScopeFactoryException("Failed to create instance of IScope.", e);
        }
    }
}
