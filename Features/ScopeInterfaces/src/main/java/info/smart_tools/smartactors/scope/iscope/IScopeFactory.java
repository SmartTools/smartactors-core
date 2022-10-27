package info.smart_tools.smartactors.scope.iscope;

import info.smart_tools.smartactors.scope.iscope.exception.ScopeFactoryException;

/**
 * ScopeFactory interface
 */
public interface IScopeFactory {

    /**
     * Factory method for create new instance if {@link IScope}
     * @param obj needed parameters for creation
     * @return instance of {@link IScope}
     * @throws ScopeFactoryException if any errors occurred
     */
    IScope createScope(final Object obj) throws ScopeFactoryException;
}
