package info.smart_tools.smartactors.ioc.recursive_strategy_container;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.istrategy_container.exception.StrategyContainerException;
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
        container.register(new Object(), mock(IStrategy.class));
    }

    @Test(expected = StrategyContainerException.class)
    public void testRemove() throws StrategyContainerException {
        container.unregister(new Object());
    }

}
