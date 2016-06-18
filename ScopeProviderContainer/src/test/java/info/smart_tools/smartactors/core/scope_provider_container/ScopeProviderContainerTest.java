package info.smart_tools.smartactors.core.scope_provider_container;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope.IScopeFactory;
import info.smart_tools.smartactors.core.iscope_provider_container.IScopeProviderContainer;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ScopeProviderContainerTest {

    @Test
    public void checkCreation() {
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(mock(IScopeFactory.class));
        assertNotNull(scopeProviderContainer);
    }

    @Test
    public void checkGetterSetterCreator()
            throws Exception {
        IScope scope = mock(IScope.class);
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        Object param = new Object();
        when(factory.createScope(param)).thenReturn(scope);
        Object key1 = scopeProviderContainer.createScope(param);
        IScope resultScope1 = scopeProviderContainer.getScope(key1);
        assertEquals(scope, resultScope1);
        String key2 = "key2";
        scopeProviderContainer.addScope(key2, scope);
        IScope resultScope2 = scopeProviderContainer.getScope(key2);
        assertEquals(scope, resultScope2);
    }

    @Test (expected = ScopeProviderException.class)
    public void checkScopeProviderExceptionByGet()
            throws Exception {
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        scopeProviderContainer.getScope("key1");
        fail();
    }

    @Test (expected = ScopeProviderException.class)
    public void checkScopeProviderExceptionByAdd()
            throws Exception {
        IScope scope = mock(IScope.class);
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        scopeProviderContainer.addScope(null, scope);
        fail();
    }

    @Test (expected = ScopeProviderException.class)
    public void checkScopeProviderExceptionByCreate()
            throws Exception {
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        scopeProviderContainer.createScope(null);
        fail();
    }

    @Test  (expected = ScopeProviderException.class)
    public void checkDeletion()
            throws Exception {
        Object param = new Object();
        IScope scope = mock(IScope.class);
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        when(factory.createScope(param)).thenReturn(scope);
        Object key = scopeProviderContainer.createScope(param);
        scopeProviderContainer.deleteScope(key);
        scopeProviderContainer.getScope(key);
        fail();
    }

    @Test (expected = ScopeProviderException.class)
    public void checkScopeProviderExceptionByDelete()
            throws Exception {
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        scopeProviderContainer.deleteScope(null);
        fail();
    }

    @Test
    public void checkSetCurrentGetCurrent()
            throws Exception {
        IScope scope = mock(IScope.class);
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        scopeProviderContainer.setCurrentScope(scope);
        IScope resultScope = scopeProviderContainer.getCurrentScope();
        assertEquals(scope, resultScope);
    }

    @Test (expected = ScopeProviderException.class)
    public void checkScopeProviderExceptionByGetCurrent()
            throws Exception {
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        scopeProviderContainer.getCurrentScope();
        fail();
    }

    @Test
    public void checkSubscribeOnCreationNewScope()
            throws Exception {
        IScope scope = mock(IScope.class);
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        IAction observer = mock(IAction.class);
        Object param = new Object();
        when(factory.createScope(param)).thenReturn(scope);
        doNothing().when(observer).execute(scope);
        scopeProviderContainer.subscribeOnCreationNewScope(observer);
        scopeProviderContainer.createScope(param);
        verify(observer, times(1)).execute(scope);
    }

    @Test (expected = ScopeProviderException.class)
    public void checkScopeProviderExceptionOnSubscribeOnCreationNewScope()
            throws Exception {
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        scopeProviderContainer.subscribeOnCreationNewScope(null);
    }

    @Test
    public void checkClearListOfSubscribers() throws Exception {
        IScope scope = mock(IScope.class);
        IScopeFactory factory = mock(IScopeFactory.class);
        IScopeProviderContainer scopeProviderContainer = new ScopeProviderContainer(factory);
        IAction observer = mock(IAction.class);
        Object param = new Object();
        when(factory.createScope(param)).thenReturn(scope);
        doNothing().when(observer).execute(scope);
        scopeProviderContainer.subscribeOnCreationNewScope(observer);
        scopeProviderContainer.createScope(param);
        scopeProviderContainer.clearListOfSubscribers();
        scopeProviderContainer.createScope(param);
        verify(observer, times(1)).execute(scope);
    }
}
