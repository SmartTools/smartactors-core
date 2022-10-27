package info.smart_tools.smartactors.testing.test_environment_handler;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link MainTestChain}.
 */
public class MainTestChainTest {

    private IStrategyContainer container = new StrategyContainer();
    private IAction<Throwable> completionCallbackMock;
    private IObject successArgumentsMock;
    private Object testingChainName;

    @Before
    public void setUp()
            throws Exception {
        this.completionCallbackMock = mock(IAction.class);
        this.successArgumentsMock = mock(IObject.class);
        this.testingChainName = mock(Object.class);

        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(keyOfMainScope);
        scope.setValue(IOC.getIocKey(), this.container);
        ScopeProvider.setCurrentScope(scope);

        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
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
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"),
                new ApplyFunctionToArgumentsStrategy((args) -> new DSObject())
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new ApplyFunctionToArgumentsStrategy((args) -> new FieldName((String) args[0]))
        );
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenCallbackIsNull()
            throws Exception {
        assertNotNull(new MainTestChain(
                this.testingChainName,
                null,
                this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule()
        ));
        fail();
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenChainIsNull()
            throws Exception {
        assertNotNull(new MainTestChain(null, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule()));
        fail();
    }

    @Test(expected = InitializationException.class)
    public void Should_constructorThrowInitializationExceptionWhenIOCNotInitialized()
            throws Exception {
        IStrategy strategy = mock(IStrategy.class);
        IOC.register(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"), strategy);
        doThrow(Exception.class).when(strategy).resolve();
        assertNotNull(new MainTestChain(this.testingChainName, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule()));
        fail();
    }

    @Test
    public void Should_initSuccessReceiverArgs()
            throws Exception {
        IReceiverChain chain = new MainTestChain(this.testingChainName, this.completionCallbackMock, null,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule());
        assertNotNull(chain.get(1));
    }

    @Test
    public void Should_haveName()
            throws Exception {
        IReceiverChain chain = new MainTestChain(this.testingChainName, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule());
        assertNotNull(chain.getId());
    }

    @Test
    public void Should_returnSuccessReceiverArgumentsForFirstReceiverInChain()
            throws Exception {
        IReceiverChain chain = new MainTestChain(this.testingChainName, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule());
        assertSame(this.successArgumentsMock, chain.getArguments(1));
        assertNotNull(chain.getArguments(0));
    }

    @Test
    public void Should_returnSuccessReceiverForFirstAndSecondReceiverInChain()
            throws Exception {
        IReceiverChain chain = new MainTestChain(this.testingChainName, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule());
        assertNotNull(chain.get(0));
        assertNotNull(chain.get(1));
        assertNull(chain.get(2));
    }

    @Test
    public void Should_callCallbackWhenChainCompletedWithException()
            throws Exception {
        Throwable exceptionMock = mock(Throwable.class);

        IReceiverChain chain = new MainTestChain(this.testingChainName, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule());

        chain.getExceptionalChainNamesAndEnvironments(exceptionMock);
        chain.getExceptionalChainNamesAndEnvironments(exceptionMock);

        verify(this.completionCallbackMock, times(1)).execute(same(exceptionMock));
    }

    @Test
    public void Should_callCallbackWhenChainCompletedSuccessful()
            throws Exception {
        IReceiverChain chain = new MainTestChain(this.testingChainName, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule());

        IMessageProcessor mpMock = mock(IMessageProcessor.class);
        IMessageProcessingSequence mpsMock = mock(IMessageProcessingSequence.class);
        when(mpMock.getSequence()).thenReturn(mpsMock);

        chain.get(0).receive(mpMock);
        chain.get(1).receive(mpMock);

        verify(this.completionCallbackMock, times(1)).execute(null);
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_successReceiverWrapExceptionThrownByCallback()
            throws Exception {
        IReceiverChain chain = new MainTestChain(this.testingChainName, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule());
        IMessageProcessor mpMock = mock(IMessageProcessor.class);

        doThrow(ActionExecutionException.class).when(this.completionCallbackMock).execute(null);

        chain.get(1).receive(mpMock);
    }

    @Test
    public void Should_returnExceptionalTestChainWhenExceptionInCallback()
            throws Exception {
        IReceiverChain chain = new MainTestChain(this.testingChainName, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule());
        doThrow(InvalidArgumentException.class).when(this.completionCallbackMock).execute(any(Exception.class));
        IObject exceptionalChainAndEnv = chain.getExceptionalChainNamesAndEnvironments(new Exception());
        IReceiverChain exceptionalChain = (IReceiverChain) exceptionalChainAndEnv.getValue(new FieldName("chain"));
        assertNotNull(exceptionalChain);
    }

    @Test (expected = MessageReceiveException.class)
    public void Should_throwMessageReceiverExceptionOnFirstReceiverFail()
            throws Exception {
        IReceiverChain chain = new MainTestChain(this.testingChainName, this.completionCallbackMock, this.successArgumentsMock,
                ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule());

        IMessageProcessor mpMock = mock(IMessageProcessor.class);
        doThrow(NestedChainStackOverflowException.class).when(mpMock).getSequence();
        chain.get(0).receive(mpMock);
        fail();
    }
}
