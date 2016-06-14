package info.smart_tools.smartactors.core.named_keys_storage;

import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link Keys}
 */
public class KeysTest {

    @Test
    public void checkGetOrAdd()
            throws Exception {
        IStrategyContainer strategyContainer = mock(IStrategyContainer.class);
        IResolveDependencyStrategy strategy = mock(IResolveDependencyStrategy.class);
        IKey<Integer> key = mock(IKey.class);
        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), strategyContainer);
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );
        Object scopeId = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(scopeId);
        ScopeProvider.setCurrentScope(scope);
        when(strategyContainer.resolve(any())).thenReturn(strategy);
        when(strategy.resolve("test")).thenReturn(key);
        IKey<Integer> result = Keys.getOrAdd("test");
        assertNotNull(result);
        assertEquals(result, key);
    }
}

