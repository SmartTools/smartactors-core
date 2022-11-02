package info.smart_tools.smartactors.scope.scope_provider;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
        Object key = mock(Object.class);
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

//    @Test
//    public void checkSubscribeOnCreationNewScope()
//            throws Exception {
//        IAction observer = mock(IAction.class);
//        doNothing().when(observer).execute(any(IScope.class));
//        ScopeProvider.subscribeOnCreationNewScope(observer);
//        ScopeProvider.createScope(null);
//        verify(observer, times(1)).execute(any(IScope.class));
//        reset(observer);
//    }

    @Test
    public void checkSubscribeAndClearListOfSubscribers()
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
