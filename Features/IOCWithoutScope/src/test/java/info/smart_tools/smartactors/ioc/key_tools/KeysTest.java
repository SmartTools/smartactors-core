package info.smart_tools.smartactors.ioc.key_tools;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
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
    public void checkGetKeyByName()
            throws Exception {
        IStrategyContainer strategyContainer = mock(IStrategyContainer.class);
        IStrategy strategy = mock(IStrategy.class);
        IKey key = mock(IKey.class);
        when(strategyContainer.resolve(any())).thenReturn(strategy);
        when(strategy.resolve("test")).thenReturn(key);
        IOC.register(IOC.getKeyForKeyByNameStrategy(), strategy);
        IKey result = Keys.getKeyByName("test");
        assertNotNull(result);
        assertEquals(result, key);
    }
}

