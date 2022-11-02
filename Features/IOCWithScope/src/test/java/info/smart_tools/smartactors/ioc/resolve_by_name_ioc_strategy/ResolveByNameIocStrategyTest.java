package info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for ResolveByNameIocStrategy
 */
public class ResolveByNameIocStrategyTest {

    @Test
    public void checkStrategyCreation() {
        IStrategy strategy = new ResolveByNameIocStrategy();
        assertNotNull(strategy);
    }

    @Test
    public void checkStrategyResolution()
            throws Exception{
        IStrategy strategy = new ResolveByNameIocStrategy();
        IKey key1 = strategy.resolve("unique_key");
        assertNotNull(key1);
        IKey key2 = strategy.resolve("unique_key");
        assertNotNull(key2);
        assertEquals(key1, key2);
        IKey key3 = strategy.resolve("unique_key_other");
        assertNotEquals(key1, key3);
    }

    @Test (expected = StrategyException.class)
    public void checkStrategyResolutionException()
            throws Exception {
        IStrategy strategy = new ResolveByNameIocStrategy();
        strategy.resolve(null);
    }
}
