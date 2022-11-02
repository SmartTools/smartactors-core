package info.smart_tools.smartactors.ioc_strategy_pack.uuid_nextid_strategy;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for UUID strategy.
 */
public class UuidNextIdStrategyTest {

    @Test
    public void testUuidGenerated() throws StrategyException {
        IStrategy strategy = new UuidNextIdStrategy();
        assertTrue(strategy.resolve() instanceof String);
        assertNotEquals(strategy.resolve(), strategy.resolve());
    }

}
