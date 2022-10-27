package info.smart_tools.smartactors.http_endpoint.http_request_handler;

import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.iadd_request_parameters_to_iobject.IAddRequestParametersToIObject;
import info.smart_tools.smartactors.endpoint.interfaces.iadd_request_parameters_to_iobject.exception.AddRequestParametersToIObjectException;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.RequestHandlerDataException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.http_endpoint.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by sevenbits on 05.09.16.
 */
public class HttpRequestHandlerTest extends IOCInitializer {

    private IDeserializeStrategy deserializeStrategy;
    private IKey mockedKey;
    private IAddRequestParametersToIObject requestParametersToIObject;
    private IObject httpResponse;
    private IQueue taskQueueMock;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        deserializeStrategy = mock(IDeserializeStrategy.class);
        mockedKey = mock(IKey.class);
        requestParametersToIObject = mock(IAddRequestParametersToIObject.class);
        httpResponse = mock(IObject.class);
        IOC.register(
                Keys.getKeyByName("EmptyIObject"),
                new ApplyFunctionToArgumentsStrategy(
                        args -> new DSObject()
                )
        );

        IOC.register(Keys.getKeyByName("info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy"), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            if (args[0].equals(mockedKey)) {
                                return deserializeStrategy;
                            } else {
                                return requestParametersToIObject;
                            }
                        }
                )
        );


        IOC.register(Keys.getKeyByName("http_request_key_for_deserialize"), new SingletonStrategy(mockedKey));

        IOC.register(
                Keys.getKeyByName("info.smart_tools.smartactors.http_endpoint.channel_handler_netty.ChannelHandlerNetty"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IChannelHandler channelHandler = new ChannelHandlerNetty();
                            channelHandler.init(args[0]);
                            return channelHandler;
                        }
                )
        );

        IOC.register(Keys.getKeyByName("endpoint response strategy"), new SingletonStrategy(new Object()));

        taskQueueMock = mock(IQueue.class);

        IOC.register(Keys.getKeyByName("task_queue"), new SingletonStrategy(taskQueueMock));
    }

    @Test
    public void testDeserialization() throws Exception {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        FullHttpRequest request = mock(FullHttpRequest.class);
        IObject message = new DSObject("{\"hello\": \"world\"}");
        when(deserializeStrategy.deserialize(request)).thenReturn(message);
        when(request.method()).thenReturn(HttpMethod.POST);
        HttpRequestHandler requestHandler = new HttpRequestHandler(ScopeProvider.getCurrentScope(), null, null, null, null, mock(IUpCounter.class));
        IObject environment = requestHandler.getEnvironment(ctx, request);
        assertEquals(environment.getValue(new FieldName("message")), message);
    }

    @Test(expected = RequestHandlerDataException.class)
    public void testBadRequestBodyException() throws Exception {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        FullHttpRequest request = mock(FullHttpRequest.class);
        IObject message = new DSObject("{\"hello\": \"world\"}");
        when(deserializeStrategy.deserialize(request)).thenThrow(DeserializationException.class);
        when(request.method()).thenReturn(HttpMethod.POST);
        HttpRequestHandler requestHandler = new HttpRequestHandler(ScopeProvider.getCurrentScope(), null, null, null, null, mock(IUpCounter.class));
        IOC.register(Keys.getKeyByName("HttpPostParametersToIObjectException"), new SingletonStrategy(
                        new DSObject("{\"statusCode\": 200}")
                )
        );
        IObject environment = requestHandler.getEnvironment(ctx, request);
        verify(ctx.writeAndFlush(any()));
    }

    @Test(expected = RequestHandlerDataException.class)
    public void testBadRequestUriException() throws Exception {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        FullHttpRequest request = mock(FullHttpRequest.class);
        IObject message = new DSObject("{\"hello\": \"world\"}");
        when(request.method()).thenReturn(HttpMethod.GET);
        doThrow(new AddRequestParametersToIObjectException("exception")).when(requestParametersToIObject).extract(any(), any());
        HttpRequestHandler requestHandler = new HttpRequestHandler(ScopeProvider.getCurrentScope(), null, null, null, null, mock(IUpCounter.class));
        IOC.register(Keys.getKeyByName("HttpRequestParametersToIObjectException"), new SingletonStrategy(
                        new DSObject("{\"statusCode\": 200}")
                )
        );
        IObject environment = requestHandler.getEnvironment(ctx, request);
    }

}
