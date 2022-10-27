package info.smart_tools.smartactors.scope.scope_able;

import info.smart_tools.smartactors.scope.iscope.IScope;

/**
 * Interface for objects which having own scope
 */
public interface IScopeAble {

    /**
     * Get an object scope
     * @return instance of {@link IScope}
     */
    IScope getScope();
}
