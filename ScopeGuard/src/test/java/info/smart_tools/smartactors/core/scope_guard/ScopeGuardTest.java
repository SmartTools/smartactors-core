package info.smart_tools.smartactors.core.scope_guard;

import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.scope_guard.exception.ScopeGuardException;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for ScopeGuard
 */
public class ScopeGuardTest {

    @Test
    public void checkGuard()
            throws Exception {
        Object key1 = ScopeProvider.createScope(null);
        Object key2 = ScopeProvider.createScope(null);
        IScope scope1 = ScopeProvider.getScope(key1);
        IScope scope2 = ScopeProvider.getScope(key2);
        assertNotSame(scope1, scope2);
        ScopeProvider.setCurrentScope(scope1);
        ScopeGuard guard = new ScopeGuard(key2);
        assertEquals(scope2, ScopeProvider.getCurrentScope());
        guard.close();
        assertEquals(scope1, ScopeProvider.getCurrentScope());
    }

    @Test
    public void checkGuardInResourceTry() throws ScopeProviderException, ScopeGuardException {
        Object key1 = ScopeProvider.createScope(null);
        Object key2 = ScopeProvider.createScope(null);
        IScope scope1 = ScopeProvider.getScope(key1);
        IScope scope2 = ScopeProvider.getScope(key2);
        assertNotSame(scope1, scope2);
        ScopeProvider.setCurrentScope(scope1);
        try (ScopeGuard guard = new ScopeGuard(key2)) {
            assertEquals(scope2, ScopeProvider.getCurrentScope());
        }
        assertEquals(scope1, ScopeProvider.getCurrentScope());
    }

    @Test (expected = ScopeGuardException.class)
    public void checkScopeGuardExceptionOnGuard()
            throws Exception {
        Object key = new Object();
        ScopeGuard guard = new ScopeGuard(key);
        fail();
    }

}
