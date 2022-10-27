package info.smart_tools.smartactors.testing.test_runner;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.testing.interfaces.isource.ISource;
import info.smart_tools.smartactors.testing.interfaces.isource.exception.SourceExtractionException;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.ITestRunner;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.exception.TestExecutionException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link HttpEndpointTestRunner}.
 */
public class HttpEndpointTestRunnerTest {

    private IStrategyContainer container = new StrategyContainer();
    private Object chainName = mock(Object.class);
    private IObject sourceObject = mock(IObject.class);
    private IObject message = mock(IObject.class);
    private IObject environment = mock(IObject.class);
    private ISource source = mock(ISource.class);

    @Before
    public void init()
            throws Exception {
        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(keyOfMainScope);
        scope.setValue(IOC.getIocKey(), this.container);
        ScopeProvider.setCurrentScope(scope);

        IOC.register(
                IOC.getKeyForKeyByNameStrategy(),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new Key((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new ApplyFunctionToArgumentsStrategy(
                        (a) -> {
                            try {
                                return new FieldName((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"),
                new ApplyFunctionToArgumentsStrategy(
                        (a) -> {
                            try {
                                return this.sourceObject;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "test_data_source"),
                new SingletonStrategy(this.source)
        );
    }

    @Test
    public void checkCreation()
            throws Exception {
        ITestRunner runner = new HttpEndpointTestRunner();
        assertNotNull(runner);
    }

    @Test
    public void checkRunnerHandle()
            throws Exception {
        ITestRunner runner = new HttpEndpointTestRunner();
        IObject desc = mock(IObject.class);
        IAction<Throwable> callback = new IAction<Throwable>() {
            @Override
            public void execute(Throwable actingObject)
                    throws ActionExecutionException, InvalidArgumentException {
            }
        };
        when(desc.getValue(new FieldName("chainName"))).thenReturn(this.chainName);
        runner.runTest(desc, callback);
        verify(this.source, times(1)).setSource(this.sourceObject);
        verify(this.sourceObject, times(1)).setValue(new FieldName("content"), desc);
        verify(this.sourceObject, times(1)).setValue(new FieldName("callback"), callback);
        verify(this.sourceObject, times(1)).setValue(new FieldName("chainName"), this.chainName);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullFirstArgument()
            throws Exception {
        IAction<Throwable> callback = new IAction<Throwable>() {
            @Override
            public void execute(Throwable actingObject)
                    throws ActionExecutionException, InvalidArgumentException {
            }
        };
        ITestRunner runner = new HttpEndpointTestRunner();
        runner.runTest(null, callback);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullSecondArgument()
            throws Exception {
        ITestRunner runner = new HttpEndpointTestRunner();
        runner.runTest(mock(IObject.class), null);
        fail();
    }

    @Test (expected = InitializationException.class)
    public void checkInitializationExceptionOnCreation()
            throws Exception {
        IStrategy strategy = mock(IStrategy.class);
        doThrow(ResolutionException.class).when(strategy).resolve(any());
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                strategy
        );
        new HttpEndpointTestRunner();
        fail();
    }

    @Test (expected = TestExecutionException.class)
    public void checkTestExecutionExceptionOnHandle()
            throws Exception {
        ITestRunner runner = new HttpEndpointTestRunner();
        IObject desc = mock(IObject.class);
        IAction<Throwable> callback = new IAction<Throwable>() {
            @Override
            public void execute(Throwable actingObject)
                    throws ActionExecutionException, InvalidArgumentException {
            }
        };
        when(desc.getValue(new FieldName("chainName"))).thenReturn(chainName);
        when(desc.getValue(new FieldName("environment"))).thenReturn(environment);
        when(environment.getValue(new FieldName("message"))).thenReturn(message);
        doThrow(SourceExtractionException.class).when(this.source).setSource(this.sourceObject);
        runner.runTest(desc, callback);
        fail();
    }
}
