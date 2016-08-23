package info.smart_tools.smartactors.core.chain_testing.checkers;

import info.smart_tools.smartactors.core.chain_testing.exceptions.AssertionFailureException;
import info.smart_tools.smartactors.core.chain_testing.exceptions.TestStartupException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.core.irouter.IRouter;
import info.smart_tools.smartactors.core.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.plugin.dsobject.PluginDSObject;
import info.smart_tools.smartactors.plugin.ifieldname.IFieldNamePlugin;
import info.smart_tools.smartactors.plugin.ioc_keys.PluginIOCKeys;
import info.smart_tools.smartactors.plugin.scope_provider.PluginScopeProvider;
import info.smart_tools.smartactors.plugin.scoped_ioc.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ExceptionInterceptor}.
 */
public class ExceptionInterceptorTest extends PluginsLoadingTestBase {
    private IResolveDependencyStrategy receiverIdStrategyMock;
    private IRouter routerMock;
    private IMessageReceiver expectedReceiverMock;
    private IMessageReceiver unexpectedReceiverMock;
    private IMessageProcessor messageProcessorMock;
    private IMessageProcessingSequence sequenceMock;

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks()
            throws Exception {
        receiverIdStrategyMock = mock(IResolveDependencyStrategy.class);
        routerMock = mock(IRouter.class);
        expectedReceiverMock = mock(IMessageReceiver.class);
        unexpectedReceiverMock = mock(IMessageReceiver.class);
        messageProcessorMock = mock(IMessageProcessor.class);
        sequenceMock = mock(IMessageProcessingSequence.class);

        when(messageProcessorMock.getSequence()).thenReturn(sequenceMock);

        IOC.register(Keys.getOrAdd("receiver_id_from_iobject"), receiverIdStrategyMock);
        IOC.register(Keys.getOrAdd(IRouter.class.getCanonicalName()), new IResolveDependencyStrategy() {
            @Override
            public <T> T resolve(Object... args) throws ResolveDependencyStrategyException {
                return (T)routerMock;
            }
        });
    }

    @Test(expected = TestStartupException.class)
    public void Should_constructorThrowWhenInvalidClassNameGivenInDescriptionObject()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'class': 'org.hell.Unexist'}".replace('\'', '"'));
        Object receiverId = new Object();
        when(receiverIdStrategyMock.resolve(same(desc))).thenReturn(receiverId);
        when(routerMock.route(same(receiverId))).thenReturn(expectedReceiverMock);

        new ExceptionInterceptor(desc);
    }

    @Test(expected = TestStartupException.class)
    public void Should_constructorThrowWhenInvalidReceiverIdGivenAsArgument()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'class': 'java.lang.NullPointerException'}".replace('\'', '"'));
        Object receiverId = new Object();
        when(receiverIdStrategyMock.resolve(same(desc))).thenReturn(receiverId);
        when(routerMock.route(same(receiverId))).thenThrow(RouteNotFoundException.class);

        new ExceptionInterceptor(desc);
    }

    @Test(expected = TestStartupException.class)
    public void Should_constructorThrowWhenSomethingGoesWrong()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'class': 'java.lang.NullPointerException'}".replace('\'', '"'));

        IOC.remove(Keys.getOrAdd("receiver_id_from_iobject"));

        new ExceptionInterceptor(desc);
    }

    private ExceptionInterceptor createCorrect()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'class': 'java.lang.IllegalStateException'}".replace('\'', '"'));
        Object receiverId = new Object();
        when(receiverIdStrategyMock.resolve(same(desc))).thenReturn(receiverId);
        when(routerMock.route(same(receiverId))).thenReturn(expectedReceiverMock);

        return new ExceptionInterceptor(desc);
    }

    @Test
    public void Should_createNoArgumentsObjectForSuccessReceiver()
            throws Exception {
        assertNull(createCorrect().getSuccessfulReceiverArguments());
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenNoExceptionOccurred()
            throws Exception {
        createCorrect().check(messageProcessorMock, null);
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenUnexpectedExceptionOccurs()
            throws Exception {
        createCorrect().check(messageProcessorMock, new Exception(new IllegalArgumentException(new NullPointerException())));
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenExceptionOccursInUnexpectedPlace()
            throws Exception {
        when(sequenceMock.getCurrentReceiver()).thenReturn(unexpectedReceiverMock);

        createCorrect().check(messageProcessorMock, new Exception(new IllegalStateException(new NullPointerException())));
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenExceptionOccursInUnexpectedPlaceAndStrategyCannotResolveUnexpectedReceiverId()
            throws Exception {
        when(sequenceMock.getCurrentReceiver()).thenReturn(unexpectedReceiverMock);
        IObject unexpectedId = mock(IObject.class);
        when(sequenceMock.getCurrentReceiverArguments()).thenReturn(unexpectedId);
        when(receiverIdStrategyMock.resolve(same(unexpectedId))).thenThrow(ResolveDependencyStrategyException.class);

        createCorrect().check(messageProcessorMock, new Exception(new IllegalStateException(new NullPointerException())));
    }

    @Test
    public void Should_notThrowWhenEverythingIsOk()
            throws Exception {
        when(sequenceMock.getCurrentReceiver()).thenReturn(expectedReceiverMock);

        createCorrect().check(messageProcessorMock, new Exception(new IllegalStateException(new NullPointerException())));
    }
}
