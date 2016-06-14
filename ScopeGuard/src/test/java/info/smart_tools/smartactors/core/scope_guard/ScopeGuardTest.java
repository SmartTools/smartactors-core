package info.smart_tools.smartactors.core.scope_guard;

import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.IScopeProviderContainer;
import info.smart_tools.smartactors.core.scope_guard.exception.ScopeGuardException;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        ScopeGuard guard = new ScopeGuard();
        guard.guard(key2);
        assertEquals(scope2, ScopeProvider.getCurrentScope());
        guard.close();
        assertEquals(scope1, ScopeProvider.getCurrentScope());
    }

    @Test (expected = ScopeGuardException.class)
    public void checkScopeGuardExceptionOnGuard()
            throws Exception {
        ScopeGuard guard = new ScopeGuard();
        Object key = new Object();
        guard.guard(key);
        fail();
    }

    @Test (expected = ScopeGuardException.class)
    public void checkScopeGuardExceptionOnClose() throws ScopeGuardException {
        ScopeGuard guard = new ScopeGuard();
        guard.close();
        fail();
    }
}
