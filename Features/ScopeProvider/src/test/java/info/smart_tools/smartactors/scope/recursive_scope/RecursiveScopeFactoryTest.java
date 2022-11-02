package info.smart_tools.smartactors.scope.recursive_scope;

import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope.exception.ScopeException;
import info.smart_tools.smartactors.scope.iscope.exception.ScopeFactoryException;
import org.junit.Test;

import static org.junit.Assert.*;

public class RecursiveScopeFactoryTest {

    @Test
    public void checkScopeCreation()
            throws ScopeFactoryException, ScopeException {
        ScopeFactory factory = new ScopeFactory();
        assertNotNull(factory);
        IScope scope = factory.createScope(null);
        scope.setValue("some key", "some value");
        assertNotNull(scope);
        IScope childScope = factory.createScope(scope);
        Object value = childScope.getValue("some key");
        assertEquals(value, "some value");
    }

    @Test (expected = ScopeFactoryException.class)
    public void checkScopeFactoryException()
            throws ScopeFactoryException {
        ScopeFactory factory = new ScopeFactory();
        IScope scope = factory.createScope("");
        fail();
    }
}
