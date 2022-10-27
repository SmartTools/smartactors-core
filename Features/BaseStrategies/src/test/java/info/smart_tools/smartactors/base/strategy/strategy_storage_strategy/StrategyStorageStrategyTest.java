package info.smart_tools.smartactors.base.strategy.strategy_storage_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunctionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StrategyStorageStrategyTest {

    @Test
    public void checkInstanceCreation()
            throws Exception {
        IStrategy sss = new StrategyStorageStrategy((a) -> a, (c, a) -> a);
        assertNotNull(sss);
    }

    @Test
    public void checkResolution()
            throws Exception {
        String value1 = "result1";
        String value2 = "result2";
        String defaultValue = "default";
        IStrategy innerStrategy1 = mock(IStrategy.class);
        IStrategy innerStrategy2 = mock(IStrategy.class);
        IStrategy defaultStrategy = mock(IStrategy.class);
        when(innerStrategy1.resolve(new Object[] {"in_something", "arg"})).thenReturn(value1);
        when(innerStrategy2.resolve(new Object[] {"out_something", "arg"})).thenReturn(value2);
        when(defaultStrategy.resolve(new Object[] {"something", "arg"})).thenReturn(defaultValue);
        when(defaultStrategy.resolve(new Object[] {"in_something", "arg"})).thenReturn(defaultValue);

        IFunctionTwoArgs findValueByArgument = (map, arg) -> {
            char[] symbols = arg.toString().toCharArray();
            String defaultKey = "default";
            IStrategy strategy = null;
            StringBuilder key = new StringBuilder();
            for (char c : symbols) {
                key.append(c);
                strategy = ((Map<String, IStrategy>)map).get(key.toString());
                if (null != strategy) {
                    break;
                }
            }
            return null != strategy ? strategy : ((Map<String, IStrategy>)map).get(defaultKey);
        };
        IStrategy sss = new StrategyStorageStrategy((a) -> a, findValueByArgument);
        ((IStrategyRegistration) sss).register("in_", innerStrategy1);
        ((IStrategyRegistration) sss).register("out_", innerStrategy2);
        ((IStrategyRegistration) sss).register("default", defaultStrategy);


        String result = sss.resolve("in_something", "arg");
        assertEquals(value1, result);
        result = sss.resolve("out_something", "arg");
        assertEquals(value2, result);
        result = sss.resolve("something", "arg");
        assertEquals(defaultValue, result);

        ((IStrategyRegistration) sss).unregister("in_");

        result = sss.resolve("in_something", "arg");
        assertEquals(defaultValue, result);
    }

    @Test (expected = StrategyException.class)
    public void checkResolutionException()
            throws Exception {
        IStrategy sss = new StrategyStorageStrategy(null, null);
        sss.resolve(null);
    }

    @Test (expected = StrategyRegistrationException.class)
    public void checkExceptionOnRegister()
            throws Exception {
        IStrategy strategy = new StrategyStorageStrategy(
                (a) -> {
                    throw new InvalidArgumentException("");
                },
                null
        );
        ((IStrategyRegistration) strategy).register(null, null);
    }

    @Test (expected = StrategyRegistrationException.class)
    public void checkExceptionOnRemove()
            throws Exception {
        IStrategy strategy = new StrategyStorageStrategy(
                (a) -> {
                    throw new InvalidArgumentException("");
                },
                null
        );
        ((IStrategyRegistration) strategy).unregister(null);
    }
}
