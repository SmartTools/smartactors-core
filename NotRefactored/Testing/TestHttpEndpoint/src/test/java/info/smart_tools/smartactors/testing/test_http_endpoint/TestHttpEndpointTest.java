package info.smart_tools.smartactors.testing.test_http_endpoint;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.iasync_service.IAsyncService;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.testing.interfaces.isource.ISource;
import info.smart_tools.smartactors.testing.test_data_source_iobject.IObjectDataSource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link TestHttpEndpoint}.
 */
public class TestHttpEndpointTest {

    private IStrategyContainer container = new StrategyContainer();
    private IReceiverChain chain = mock(IReceiverChain.class);
    private IAction<Throwable> callback = mock(IAction.class);

    @Rule
    public Timeout globalTimeout = Timeout.seconds(20);

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
    }

    @Test
    public void checkCreationAndExecution()
            throws Exception {
        IEnvironmentHandler handler = mock(IEnvironmentHandler.class);
        ISource<IObject, IObject> source = new IObjectDataSource();
        initFiledNameStrategy();
        initIObjectStrategy();
        initIChannelHandlerStrategy();
        Notification notification = new Notification();

        List<IObject> result = new ArrayList<>();

        IObject testObject = mock(IObject.class);

        IObject environment = new DSObject();
        IObject message = mock(IObject.class);
        IObject request = mock(IObject.class);
        IObject testContent = new DSObject();
        environment.setValue(new FieldName("message"), message);
        environment.setValue(new FieldName("request"), request);
        testContent.setValue(new FieldName("environment"), environment);

        when(testObject.getValue(new FieldName("chainName"))).thenReturn(this.chain);
        when(testObject.getValue(new FieldName("callback"))).thenReturn(this.callback);
        when(testObject.getValue(new FieldName("content"))).thenReturn(testContent);

        doAnswer(invocationOnMock -> {
            result.add((IObject) invocationOnMock.getArguments()[0]);
            notification.setExecuted(true);
            return null;
        }).when(handler).handle(testContent, this.chain, this.callback);
        source.setSource(testObject);
        IAsyncService endpoint = new TestHttpEndpoint(source, ScopeProvider.getCurrentScope(), handler, 0L, null);
        endpoint.start();
        while(true) {
            if(notification.getExecuted()) {
                break;
            }
        }
        endpoint.stop();
        verify(handler, times(1)).handle(testContent, this.chain, this.callback);
        assertEquals(result.size(), 1);
        IObject resultEnvironment = (IObject) result.get(0).getValue(new FieldName("environment"));
        IObject resultMessage = (IObject) resultEnvironment.getValue(new FieldName("message"));
        IObject resultContext = (IObject) resultEnvironment.getValue(new FieldName("context"));
        assertSame(resultMessage, message);
        assertNotNull(resultContext);
        assertNotNull(resultContext.getValue(new FieldName("headers")));
        assertNotNull(resultContext.getValue(new FieldName("cookies")));
        assertNotNull(resultContext.getValue(new FieldName("request")));
        assertNotNull(resultContext.getValue(new FieldName("channel")));
    }

    @Test
    public void checkCorrectStopService()
            throws Exception {
        IEnvironmentHandler handler = mock(IEnvironmentHandler.class);
        ISource<IObject, IObject> source = new IObjectDataSource();
        initFiledNameStrategy();
        initIObjectStrategy();
        initIChannelHandlerStrategy();

        Notification notification = new Notification();
        IObject testObject = mock(IObject.class);
        source.setSource(testObject);
        IAsyncService endpoint = new TestHttpEndpoint(source, ScopeProvider.getCurrentScope(), handler, 0L, (a) -> {
            notification.setExecuted(true);
            while(true) {
            }
        });
        endpoint.start();
        while(true) {
            if(notification.getExecuted()) {
                break;
            }
        }
        endpoint.stop();
    }

    @Test
    public void checkAdditionNewDataToQueueAfterStart()
            throws Exception {
        IEnvironmentHandler handler = mock(IEnvironmentHandler.class);
        ISource<IObject, IObject> source = new IObjectDataSource();
        initFiledNameStrategy();
        initIObjectStrategy();
        initIChannelHandlerStrategy();
        Notification notification = new Notification();

        List<IObject> result = new ArrayList<>();

        IObject testObject = mock(IObject.class);
        IObject testAnotherObject = mock(IObject.class);
        IObject testContent = new DSObject();
        IObject testAnotherContent = new DSObject();
        IObject testEnvironment = new DSObject();
        IObject testAnotherEnvironment = new DSObject();

        IObject messageOfTestObject = mock(IObject.class);
        IObject messageOfAnotherTestObject = mock(IObject.class);
        IObject requestOfTestObject = mock(IObject.class);
        IObject requestOfAnotherTestObject = mock(IObject.class);

        testEnvironment.setValue(new FieldName("message"), messageOfTestObject);
        testEnvironment.setValue(new FieldName("request"), requestOfTestObject);
        testAnotherEnvironment.setValue(new FieldName("message"), messageOfAnotherTestObject);
        testAnotherEnvironment.setValue(new FieldName("request"), requestOfAnotherTestObject);
        testContent.setValue(new FieldName("environment"), testEnvironment);
        testAnotherContent.setValue(new FieldName("environment"), testAnotherEnvironment);

        doAnswer(invocationOnMock -> {
            result.add((IObject) invocationOnMock.getArguments()[0]);
            notification.setExecuted(true);
            return null;
        }).when(handler).handle(any(IObject.class), any(IReceiverChain.class), any(null));
        when(testObject.getValue(new FieldName("content"))).thenReturn(testContent);
        when(testAnotherObject.getValue(new FieldName("content"))).thenReturn(testAnotherContent);
        source.setSource(testObject);
        IAsyncService endpoint = new TestHttpEndpoint(source, ScopeProvider.getCurrentScope(), handler, 0L, null);
        endpoint.start();
        while(true) {
            if(notification.getExecuted()) {
                break;
            }
        }
        notification.setExecuted(false);
        source.setSource(testAnotherObject);
        while(true) {
            if(notification.getExecuted()) {
                break;
            }
        }
        endpoint.stop();
        verify(handler, times(2)).handle(any(IObject.class), any(IReceiverChain.class), any(null));
        assertEquals(result.size(), 2);
        IObject resultEnvironment1 = (IObject) result.get(0).getValue(new FieldName("environment"));
        IObject resultEnvironment2 = (IObject) result.get(1).getValue(new FieldName("environment"));
        IObject message1 = (IObject) resultEnvironment1.getValue(new FieldName("message"));
        IObject context1 = (IObject) resultEnvironment1.getValue(new FieldName("context"));
        IObject message2 = (IObject) resultEnvironment2.getValue(new FieldName("message"));
        IObject context2 = (IObject) resultEnvironment2.getValue(new FieldName("context"));
        assertSame(message1, messageOfTestObject);
        assertNotNull(context1);
        assertSame(message2, messageOfAnotherTestObject);
        assertNotNull(context2);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullQueue()
            throws Exception {
        IEnvironmentHandler handler = mock(IEnvironmentHandler.class);
        IAsyncService endpoint = new TestHttpEndpoint(null, ScopeProvider.getCurrentScope(), handler, 0L, null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullScope()
            throws Exception {
        IEnvironmentHandler handler = mock(IEnvironmentHandler.class);
        ISource<IObject, IObject> source = mock(ISource.class);
        IAsyncService endpoint = new TestHttpEndpoint(source, null, handler, 0L, null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullHandler()
            throws Exception {
        ISource<IObject, IObject> source = mock(ISource.class);
        IAsyncService endpoint = new TestHttpEndpoint(source, ScopeProvider.getCurrentScope(), null, 0L, null);
        fail();
    }

    @Test (expected = InitializationException.class)
    public void checkInitializeExceptionOnNotInitializedIOC()
            throws Exception {
        IEnvironmentHandler handler = mock(IEnvironmentHandler.class);
        IStrategy strategy = mock(IStrategy.class);
        IOC.register(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), strategy);
        doThrow(Exception.class).when(strategy).resolve(any());
        ISource<IObject, IObject> source = mock(ISource.class);
        IAsyncService endpoint = new TestHttpEndpoint(source, ScopeProvider.getCurrentScope(), handler, 0L, null);
        fail();
    }

    @Test
    public void checkSkipMessageOnExceptionInFutureBlock()
            throws Exception {
        initFiledNameStrategy();
        initIObjectStrategy();
        initIChannelHandlerStrategy();
        IEnvironmentHandler handler = mock(IEnvironmentHandler.class);
        ISource<IObject, IObject> source = new IObjectDataSource();
        Notification notification = new Notification();

        IObject testObject = mock(IObject.class);
        IObject testAnotherObject = mock(IObject.class);
        IObject testContent = new DSObject();
        IObject testAnotherContent = new DSObject();
        IObject testEnvironment = new DSObject();
        IObject testAnotherEnvironment = new DSObject();

        IObject messageOfTestObject = mock(IObject.class);
        IObject messageOfAnotherTestObject = mock(IObject.class);
        IObject requestOfTestObject = mock(IObject.class);
        IObject requestOfAnotherTestObject = mock(IObject.class);

        testEnvironment.setValue(new FieldName("message"), messageOfTestObject);
        testEnvironment.setValue(new FieldName("request"), requestOfTestObject);
        testAnotherEnvironment.setValue(new FieldName("message"), messageOfAnotherTestObject);
        testAnotherEnvironment.setValue(new FieldName("request"), requestOfAnotherTestObject);
        testContent.setValue(new FieldName("environment"), testEnvironment);
        testAnotherContent.setValue(new FieldName("environment"), testAnotherEnvironment);

        List<IObject> result = new ArrayList<>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                IObject obj = (IObject) invocationOnMock.getArguments()[0];
                if (obj == testContent) {
                    throw new Exception();
                }
                if (obj == testAnotherContent) {
                    result.add(obj);
                }
                notification.setExecuted(true);
                return null;
            }
        }).when(handler).handle(any(IObject.class), any(IReceiverChain.class), any(null));
        when(testObject.getValue(new FieldName("content"))).thenReturn(testContent);
        when(testObject.getValue(new FieldName("chain"))).thenReturn(this.chain);
        when(testObject.getValue(new FieldName("callback"))).thenReturn(this.callback);
        when(testAnotherObject.getValue(new FieldName("content"))).thenReturn(testAnotherContent);
        when(testAnotherObject.getValue(new FieldName("chain"))).thenReturn(this.chain);
        when(testAnotherObject.getValue(new FieldName("callback"))).thenReturn(this.callback);
        source.setSource(testObject);
        IAsyncService endpoint = new TestHttpEndpoint(source, ScopeProvider.getCurrentScope(), handler, 0L, null);
        endpoint.start();
        source.setSource(testAnotherObject);
        while (true) {
            if (notification.getExecuted()) {
                break;
            }
        }
        endpoint.stop();
        assertEquals(result.size(), 1);
        assertSame(result.get(0), testAnotherContent);
    }

    @Test
    public void checkSkipMessageOnExceptionInDefaultStrategyBlock()
            throws Exception {
        IEnvironmentHandler handler = mock(IEnvironmentHandler.class);
        ISource<IObject, IObject> source = new IObjectDataSource();
        Notification notification = new Notification();

        IObject testObject = mock(IObject.class);
        IObject testAnotherObject = mock(IObject.class);
        IObject testContent = new DSObject();
        IObject testAnotherContent = new DSObject();
        IObject testEnvironment = new DSObject();
        IObject testAnotherEnvironment = new DSObject();

        IObject messageOfTestObject = mock(IObject.class);
        IObject messageOfAnotherTestObject = mock(IObject.class);
        IObject requestOfTestObject = mock(IObject.class);
        IObject requestOfAnotherTestObject = mock(IObject.class);

        testEnvironment.setValue(new FieldName("message"), messageOfTestObject);
        testEnvironment.setValue(new FieldName("request"), requestOfTestObject);
        testAnotherEnvironment.setValue(new FieldName("message"), messageOfAnotherTestObject);
        testAnotherEnvironment.setValue(new FieldName("request"), requestOfAnotherTestObject);
        testContent.setValue(new FieldName("environment"), testEnvironment);
        testAnotherContent.setValue(new FieldName("environment"), testAnotherEnvironment);

        when(testObject.getValue(new FieldName("chain"))).thenReturn(this.chain);
        when(testObject.getValue(new FieldName("callback"))).thenReturn(this.callback);
        when(testObject.getValue(new FieldName("content"))).thenReturn(testContent);
        when(testAnotherObject.getValue(new FieldName("chain"))).thenReturn(this.chain);
        when(testAnotherObject.getValue(new FieldName("callback"))).thenReturn(this.callback);
        when(testAnotherObject.getValue(new FieldName("content"))).thenReturn(testAnotherContent);

        initFiledNameStrategy();
        initIObjectStrategy();
        IChannelHandler channelHandler = mock(IChannelHandler.class);
        IStrategy strategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), TestChannelHandler.class.getCanonicalName()),
                strategy
        );
        when(strategy.resolve()).thenThrow(ResolutionException.class).thenReturn(channelHandler);
        List<IObject> result = new ArrayList<>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                IObject obj = (IObject) invocationOnMock.getArguments()[0];
                result.add(obj);
                notification.setExecuted(true);

                return null;
            }
        }).when(handler).handle(any(IObject.class), any(IReceiverChain.class), any(null));
        source.setSource(testObject);
        IAsyncService endpoint = new TestHttpEndpoint(source, ScopeProvider.getCurrentScope(), handler, 1000L, null);
        endpoint.start();
        notification.setExecuted(false);
        source.setSource(testAnotherObject);
        while (true) {
            if (notification.getExecuted()) {
                break;
            }
        }
        endpoint.stop();
        assertSame(((IObject)result.get(0).getValue(new FieldName("environment"))).getValue(new FieldName("message")), messageOfAnotherTestObject);
    }

    private void initFiledNameStrategy()
            throws Exception {
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new ResolveByNameIocStrategy((a)-> {
                    try {
                        return new FieldName((String) a[0]);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not create new instance of FieldName.");
                    }
                })
        );
    }

    private void initIObjectStrategy()
            throws Exception {
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"),
                new ApplyFunctionToArgumentsStrategy((a)-> {
                    try {
                        return new DSObject();
                    } catch (Exception e) {
                        throw new RuntimeException("Could not create new instance of FieldName.");
                    }
                })
        );
    }

    private void initIChannelHandlerStrategy()
            throws Exception {
        IChannelHandler channelHandler = mock(IChannelHandler.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), TestChannelHandler.class.getCanonicalName()),
                new SingletonStrategy(channelHandler)
        );
    }
}

class Notification {
    private volatile boolean executed = false;

    public boolean getExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }
}