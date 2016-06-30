package info.smart_tools.smartactors.core.recursive_strategy_container;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.istrategy_container.exception.StrategyContainerException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 *  Simplest tests for empty container.
 */
public class EmptyStrategyContainerTest {

    private IStrategyContainer container;

    @Before
    public void setUp() {
        container = new EmptyStrategyContainer();
    }

    @Test
    public void testResolve() throws StrategyContainerException {
        assertNull(container.resolve(new Object()));
    }

    @Test(expected = StrategyContainerException.class)
    public void testRegister() throws StrategyContainerException {
        container.register(new Object(), mock(IResolveDependencyStrategy.class));
    }

    @Test(expected = StrategyContainerException.class)
    public void testRemove() throws StrategyContainerException {
        container.remove(new Object());
    }

}
