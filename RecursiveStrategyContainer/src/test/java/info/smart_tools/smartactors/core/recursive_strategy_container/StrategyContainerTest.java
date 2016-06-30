package info.smart_tools.smartactors.core.recursive_strategy_container;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.istrategy_container.exception.StrategyContainerException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * Tests for Strategy
 */
public class StrategyContainerTest {

    @Test
    public void testRegistrationResolutionDeletion()
            throws Exception {
        IStrategyContainer container = new StrategyContainer(null);
        IResolveDependencyStrategy strategy = mock(IResolveDependencyStrategy.class);
        Object key = new Object();
        container.register(key, strategy);
        IResolveDependencyStrategy result = container.resolve(key);
        assertEquals(result, strategy);
        container.remove(key);
        result = container.resolve(key);
        assertNull(result);
        reset(strategy);
    }

    @Test
    public void testRecursiveResolution() throws StrategyContainerException {
        IStrategyContainer parent = new StrategyContainer(null);
        IStrategyContainer child = new StrategyContainer(parent);

        Object key = new Object();
        IResolveDependencyStrategy parentStrategy = mock(IResolveDependencyStrategy.class);
        IResolveDependencyStrategy childStrategy = mock(IResolveDependencyStrategy.class);

        assertNull(parent.resolve(key));
        assertNull(child.resolve(key));

        parent.register(key, parentStrategy);
        assertSame(parentStrategy, parent.resolve(key));
        assertSame(parentStrategy, child.resolve(key));

        child.register(key, childStrategy);
        assertSame(parentStrategy, parent.resolve(key));
        assertSame(childStrategy, child.resolve(key));

        child.remove(key);
        assertSame(parentStrategy, parent.resolve(key));
        assertSame(parentStrategy, child.resolve(key));

        parent.remove(key);
        assertNull(parent.resolve(key));
        assertNull(child.resolve(key));
    }

}
