package info.smart_tools.smartactors.base.synchronized_lazy_named_items_storage_strategy;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SynchronizedLazyNamedItemsStorageStrategyTest {
    private SynchronizedLazyNamedItemsStorageStrategy strategy;

    @Before public void setUp() throws Exception {
        strategy = new SynchronizedLazyNamedItemsStorageStrategy();
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void Should_detectDependencyLoops() throws Exception {
        strategy.register("1", new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return strategy.resolve("2");
            } catch (ResolveDependencyStrategyException e) {
                throw new FunctionExecutionException(e);
            }
        }));

        strategy.register("2", new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return strategy.resolve("1");
            } catch (ResolveDependencyStrategyException e) {
                throw new FunctionExecutionException(e);
            }
        }));

        strategy.resolve("1");
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void Should_throwWhenNoItemStrategyRegisteredForRequiredKey() throws Exception {
        strategy.resolve("1");
    }

    @Test public void Should_resolveItemsUsingStrategies() throws Exception {
        strategy.register("1", new SingletonStrategy("foo"));
        strategy.register("2", new SingletonStrategy("bar"));

        assertEquals("foo", strategy.resolve("1"));
        assertEquals("bar", strategy.resolve("2"));
    }

    @Test public void Should_resolveEveryItemOnce() throws Exception {
        IResolveDependencyStrategy itemStrategyMock = mock(IResolveDependencyStrategy.class);
        when(itemStrategyMock.resolve(anyVararg())).thenReturn(new Object()).thenThrow(ResolveDependencyStrategyException.class);

        strategy.register("1", itemStrategyMock);

        Object item = strategy.resolve("1");
        assertNotNull(item);

        assertSame(item, strategy.resolve("1"));
    }

    @Test public void Should_allowReplaceStrategyUntilItemIsResolved() throws Exception {
        strategy.register("1", new SingletonStrategy("foo"));
        strategy.register("1", new SingletonStrategy("bar"));

        assertEquals("bar", strategy.resolve("1"));
    }

    @Test(expected = AdditionDependencyStrategyException.class)
    public void Should_notAllowStrategyReplacementWhenItemIsResolved() throws Exception {
        strategy.register("1", new SingletonStrategy("foo"));

        assertEquals("foo", strategy.resolve("1"));

        strategy.register("1", new SingletonStrategy("bar"));
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void Should_allowStrategyRemovalWhenItemIsNotResolved() throws Exception {
        strategy.register("1", new SingletonStrategy("foo"));
        strategy.remove("1");

        strategy.resolve("1");
    }

    @Test(expected = AdditionDependencyStrategyException.class)
    public void Should_notAllowStrategyRemovalWhenItemIsResolved() throws Exception {
        strategy.register("1", new SingletonStrategy("foo"));

        assertEquals("foo", strategy.resolve("1"));

        strategy.remove("1");
    }

    @Test public void Should_cacheResolutionErrors() throws Exception {
        IResolveDependencyStrategy itemStrategy = mock(IResolveDependencyStrategy.class);

        when(itemStrategy.resolve(anyVararg()))
                .thenThrow(ResolveDependencyStrategyException.class)
                .thenReturn("");

        strategy.register("1", itemStrategy);

        Exception ee;

        try {
            strategy.resolve("1");
            fail();
            return;
        } catch (Exception e) {
            ee = (Exception) e.getCause();
        }

        try {
            strategy.resolve("1");
            fail();
        } catch (Exception e) {
            assertSame(ee, e.getCause());
        }
    }
}
