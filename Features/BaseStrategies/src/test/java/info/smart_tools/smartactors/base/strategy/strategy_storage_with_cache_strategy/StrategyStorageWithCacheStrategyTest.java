package info.smart_tools.smartactors.base.strategy.strategy_storage_with_cache_strategy;

import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunctionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.serialization.ICacheable;
import info.smart_tools.smartactors.base.interfaces.serialization.exception.CacheDropException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class StrategyStorageWithCacheStrategyTest {

    @Before
    public void init()
            throws Exception {

    }

    @Test
    public void checkInstanceCreation()
            throws Exception {

        IStrategy sscs = new StrategyStorageWithCacheStrategy(a -> a, (c, a) -> a);
        assertNotNull(sscs);
    }

    @Test
    public void checkResolution() throws Exception {
        Map<String, Integer> count = new HashMap<>();
        count.put("count", 0);
        IFunction argToKey = arg -> arg.getClass();
        IFunctionTwoArgs findValueByArgument = (map, arg) -> {
            IStrategy strategy = null;
            Integer tmp = count.get("count");
            tmp ++;
            count.put("count", tmp);
            for (Map.Entry<Class, IStrategy> entry : ((Map<Class, IStrategy>) map).entrySet()) {
                if (entry.getKey().isInstance(arg)) {
                    strategy = entry.getValue();

                    break;
                }
            }
            return strategy;
        };

        IStrategy cachedStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
        IStrategy intToString = new ApplyFunctionToArgumentsStrategy(
                args -> Integer.toString((Integer) args[0])
        );
        IStrategy floatToString = new ApplyFunctionToArgumentsStrategy(
                args -> Float.toString((Float) args[0])
        );
        ((IStrategyRegistration)cachedStrategy).register(Integer.class, intToString);
        ((IStrategyRegistration)cachedStrategy).register(Float.class, floatToString);

        String result = cachedStrategy.resolve(1);
        assertEquals("1", result);
        result = cachedStrategy.resolve(1f);
        assertEquals("1.0", result);
        count.put("count", 0);
        result = cachedStrategy.resolve(2f);
        assertEquals("2.0", result);

        assertEquals((int) count.get("count"), 0);
    }

    @Test
    public void checkDropCache()
            throws Exception {
        Map<String, Integer> count = new HashMap<>();
        count.put("count", 0);
        IFunction argToKey = arg -> arg.getClass();
        IFunctionTwoArgs findValueByArgument = (map, arg) -> {
            IStrategy strategy = null;
            Integer tmp = count.get("count");
            tmp ++;
            count.put("count", tmp);
            for (Map.Entry<Class, IStrategy> entry : ((Map<Class, IStrategy>) map).entrySet()) {
                if (entry.getKey().isInstance(arg)) {
                    strategy = entry.getValue();

                    break;
                }
            }
            return strategy;
        };

        IStrategy cachedStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
        IStrategy floatToString = new ApplyFunctionToArgumentsStrategy(
                args -> Float.toString((Float) args[0])
        );
        ((IStrategyRegistration)cachedStrategy).register(Float.class, floatToString);

        String result = cachedStrategy.resolve(1f);
        assertEquals("1.0", result);
        ((ICacheable) cachedStrategy).dropCache();
        count.put("count", 0);
        result = cachedStrategy.resolve(2f);
        assertEquals("2.0", result);
        assertEquals((int) count.get("count"), 1);

        ((ICacheable) cachedStrategy).dropCacheFor(Float.class);
        count.put("count", 0);
        result = cachedStrategy.resolve(2f);
        assertEquals("2.0", result);
        assertEquals((int) count.get("count"), 1);
    }

    @Test (expected = StrategyException.class)
    public void checkOnRemove()
            throws Exception {
        IFunction argToKey = arg -> arg.getClass();
        IFunctionTwoArgs findValueByArgument = (map, arg) -> {
            IStrategy strategy = null;
            for (Map.Entry<Class, IStrategy> entry : ((Map<Class, IStrategy>) map).entrySet()) {
                if (entry.getKey().isInstance(arg)) {
                    strategy = entry.getValue();

                    break;
                }
            }
            return strategy;
        };

        IStrategy cachedStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
        IStrategy floatToString = new ApplyFunctionToArgumentsStrategy(
                args -> Float.toString((Float) args[0])
        );
        ((IStrategyRegistration)cachedStrategy).register(Float.class, floatToString);

        String result = cachedStrategy.resolve(2f);
        assertEquals("2.0", result);

        ((IStrategyRegistration) cachedStrategy).unregister(Float.class);
        cachedStrategy.resolve(2f);
        fail();
    }

    @Test (expected = CacheDropException.class)
    public void checkExceptionOnDropCache()
            throws Exception {
        IStrategy cachedStrategy = new StrategyStorageWithCacheStrategy(null, null);
        ((ICacheable) cachedStrategy).dropCacheFor(null);
    }

    @Test (expected = StrategyRegistrationException.class)
    public void checkExceptionOnRegister()
            throws Exception {
        IStrategy cachedStrategy = new StrategyStorageWithCacheStrategy(null, null);
        ((IStrategyRegistration) cachedStrategy).register(null, null);
    }

    @Test (expected = StrategyRegistrationException.class)
    public void checkExceptionOnRemove()
            throws Exception {
        IStrategy cachedStrategy = new StrategyStorageWithCacheStrategy(null, null);
        ((IStrategyRegistration) cachedStrategy).unregister(null);
    }
}
