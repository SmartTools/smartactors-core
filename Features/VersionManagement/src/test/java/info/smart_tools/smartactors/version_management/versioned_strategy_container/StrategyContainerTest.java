package info.smart_tools.smartactors.version_management.versioned_strategy_container;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * Tests for Strategy
 */
public class StrategyContainerTest {

    @Test
    public void checkRegistrationResolutionDeletion()
            throws Exception {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        IStrategyContainer container = new StrategyContainer();
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

}
