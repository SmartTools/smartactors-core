package info.smart_tools.smartactors.base.simple_strict_storage_strategy;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class SimpleStrictStorageStrategyTest {
    @Test(expected = AdditionDependencyStrategyException.class)
    public void Should_throwWhenRegisteredStrategyIsNull() throws Exception {
        new SimpleStrictStorageStrategy("object").register("key", null);
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void Should_throwWhenNoArgumentsGiven() throws Exception {
        new SimpleStrictStorageStrategy("object").resolve();
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void Should_throwWhenNoStrategyFound() throws Exception {
        new SimpleStrictStorageStrategy("object").resolve("key");
    }

    @Test public void Should_resolveUsingRegisteredStrategies() throws Exception {
        SimpleStrictStorageStrategy simpleStrictStorageStrategy = new SimpleStrictStorageStrategy("object");

        simpleStrictStorageStrategy.register("key", new IResolveDependencyStrategy() {
            @Override
            public <T> T resolve(Object... args) throws ResolveDependencyStrategyException {
                return (T) SimpleStrictStorageStrategyTest.this;
            }
        });

        assertSame(this, simpleStrictStorageStrategy.resolve("key"));
    }

    @Test public void Should_wrapExceptionsThrownByStrategies() throws Exception {
        SimpleStrictStorageStrategy simpleStrictStorageStrategy = new SimpleStrictStorageStrategy("object");

        ResolveDependencyStrategyException exception = new ResolveDependencyStrategyException("whoops!!");

        simpleStrictStorageStrategy.register("key", new IResolveDependencyStrategy() {
            @Override
            public <T> T resolve(Object... args) throws ResolveDependencyStrategyException {
                throw exception;
            }
        });

        try {
            simpleStrictStorageStrategy.resolve("key");
            fail();
        } catch (ResolveDependencyStrategyException e) {
            assertSame(exception, e.getCause());
        }
    }
}
