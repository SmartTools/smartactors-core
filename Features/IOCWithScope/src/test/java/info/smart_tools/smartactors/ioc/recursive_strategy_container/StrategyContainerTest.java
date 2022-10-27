package info.smart_tools.smartactors.ioc.recursive_strategy_container;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.istrategy_container.exception.StrategyContainerException;
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
        IStrategy strategy = mock(IStrategy.class);
        Object key = new Object();
        container.register(key, strategy);
        IStrategy result = container.resolve(key);
        assertEquals(result, strategy);
        container.unregister(key);
        result = container.resolve(key);
        assertNull(result);
        reset(strategy);
    }

    @Test
    public void testRecursiveResolution() throws StrategyContainerException {
        IStrategyContainer parent = new StrategyContainer(null);
        IStrategyContainer child = new StrategyContainer(parent);

        Object key = new Object();
        IStrategy parentStrategy = mock(IStrategy.class);
        IStrategy childStrategy = mock(IStrategy.class);

        assertNull(parent.resolve(key));
        assertNull(child.resolve(key));

        parent.register(key, parentStrategy);
        assertSame(parentStrategy, parent.resolve(key));
        assertSame(parentStrategy, child.resolve(key));

        child.register(key, childStrategy);
        assertSame(parentStrategy, parent.resolve(key));
        assertSame(childStrategy, child.resolve(key));

        child.unregister(key);
        assertSame(parentStrategy, parent.resolve(key));
        assertSame(parentStrategy, child.resolve(key));

        parent.unregister(key);
        assertNull(parent.resolve(key));
        assertNull(child.resolve(key));
    }

}
