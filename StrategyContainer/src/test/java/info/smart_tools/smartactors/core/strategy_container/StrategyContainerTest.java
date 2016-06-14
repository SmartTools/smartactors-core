package info.smart_tools.smartactors.core.strategy_container;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * Tests for Strategy
 */
public class StrategyContainerTest {

    @Test
    public void checkRegistrationResolutionDeletion()
            throws Exception {
        IStrategyContainer container = new StrategyContainer();
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
}
