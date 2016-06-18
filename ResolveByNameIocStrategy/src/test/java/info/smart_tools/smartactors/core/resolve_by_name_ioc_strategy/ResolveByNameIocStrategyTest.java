package info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy;

import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for ResolveByNameIocStrategy
 */
public class ResolveByNameIocStrategyTest {

    @Test
    public void checkStrategyCreation() {
        IResolveDependencyStrategy strategy = new ResolveByNameIocStrategy();
        assertNotNull(strategy);
    }

    @Test
    public void checkStrategyResolution()
            throws Exception{
        IResolveDependencyStrategy strategy = new ResolveByNameIocStrategy();
        IKey key1 = strategy.resolve("unique_key");
        assertNotNull(key1);
        IKey key2 = strategy.resolve("unique_key");
        assertNotNull(key2);
        assertEquals(key1, key2);
        IKey key3 = strategy.resolve("unique_key_other");
        assertNotEquals(key1, key3);
    }

    @Test (expected = ResolveDependencyStrategyException.class)
    public void checkStrategyResolutionException()
            throws Exception {
        IResolveDependencyStrategy strategy = new ResolveByNameIocStrategy();
        strategy.resolve(null);
    }
}
