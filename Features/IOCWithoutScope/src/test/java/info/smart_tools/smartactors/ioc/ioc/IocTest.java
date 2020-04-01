package info.smart_tools.smartactors.ioc.ioc;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests for IOC
 */
public class IocTest {

    @Test(expected = ResolutionException.class)
    public void checkRegistrationResolutionAndDeletion()
            throws Exception {
        Object value = new Object();
        IKey key = mock(IKey.class);
        Object param = new Object();
        IStrategy strategy = mock(IStrategy.class);
        IOC.register(key, strategy);
        when(strategy.resolve(param)).thenReturn(value);
        Object result = IOC.resolve(key, param);
        assertSame(result, value);
        verify(strategy, times(1)).resolve(param);
        IStrategy str = IOC.unregister(key);
        assertSame(strategy, str);
        IOC.resolve(key, param);
        fail();
    }

    @Test
    public void checkGetIocUuid()
            throws Exception {
        IKey key = IOC.getIocKey();
        assertNull(key);
    }

    @Test
    public void checkGetKeyForKeyByNameStrategy()
            throws Exception {
        IKey key = IOC.getKeyForKeyByNameStrategy();
        assertNotNull(key);
    }
}
