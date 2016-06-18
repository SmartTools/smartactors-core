package info.smart_tools.smartactors.core.scope_provider;

import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for ScopeProvider
 */
public class ScopeProviderTest {

    @Test
    public void checkAddScopeAndGetScope()
            throws Exception {
        Object key = mock(Object.class);
        IScope scope = mock(IScope.class);
        ScopeProvider.addScope(key, scope);
        IScope result = ScopeProvider.getScope(key);
        assertNotNull(result);
        assertSame(result, scope);
        reset(scope);
        reset(key);
    }

    @Test
    public void checkGetCurrentScopeSetCurrentScope()
            throws Exception {
        IScope scope = mock(IScope.class);
        ScopeProvider.setCurrentScope(scope);
        IScope result = ScopeProvider.getCurrentScope();
        assertSame(result, scope);
        reset(scope);
    }

    @Test (expected = ScopeProviderException.class)
    public void checkDeleteScope()
            throws Exception {
        IScope scope = mock(IScope.class);
        IKey key = mock(IKey.class);
        ScopeProvider.addScope(key, scope);
        IScope result = ScopeProvider.getScope(key);
        assertSame(result, scope);
        ScopeProvider.deleteScope(key);
        ScopeProvider.getScope(key);
        fail();
    }

    @Test
    public void checkCreateScope()
            throws Exception {
        Object key1 = ScopeProvider.createScope(null);
        Object key2 = ScopeProvider.createScope(null);
        IScope result1 = ScopeProvider.getScope(key1);
        IScope result2 = ScopeProvider.getScope(key2);
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
    }

    @Test
    public void checkSubscribeOnCreationNewScope()
            throws Exception {
        IAction observer = mock(IAction.class);
        doNothing().when(observer).execute(any(IScope.class));
        ScopeProvider.subscribeOnCreationNewScope(observer);
        ScopeProvider.createScope(null);
        verify(observer, times(1)).execute(any(IScope.class));
        reset(observer);
    }

    @Test
    public void checkClearListOfSubscribers()
            throws Exception {
        IAction observer = mock(IAction.class);
        doNothing().when(observer).execute(any(IScope.class));
        ScopeProvider.subscribeOnCreationNewScope(observer);
        ScopeProvider.createScope(null);
        ScopeProvider.clearListOfSubscribers();
        ScopeProvider.createScope(null);
        verify(observer, times(1)).execute(any(IScope.class));
        reset(observer);
    }
}
