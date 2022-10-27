package info.smart_tools.smartactors.http_endpoint.http_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.endpoint.interfaces.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.exception.RequestSenderException;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.IResponseHandler;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.http_endpoint.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.http_endpoint.deserialize_strategy_post_json.DeserializeStrategyPostJson;
import info.smart_tools.smartactors.http_endpoint.http_client.HttpClient;
import info.smart_tools.smartactors.http_endpoint.http_server.HttpServer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.message_processing_sequence.MessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.testng.annotations.AfterMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;


public class HttpEndpointTest extends IOCInitializer {
    protected IMessageMapper<byte[]> mapperStub;
    protected HttpServer endpoint;
    protected HttpClient client;
    protected IEnvironmentHandler environmentHandler;
    protected BiConsumer<ChannelHandlerContext, FullHttpResponse> handlerStub;
    protected String receiver;


    protected int getTestingPort() {
        return 9001;
    }

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        mapperStub = mock(IMessageMapper.class);
        receiver = mock(String.class);
        environmentHandler = mock(IEnvironmentHandler.class);
        HttpEndpointTest me = this;

        IKey keyMessageProcessingSequence = Keys.getKeyByName(MessageProcessingSequence.class.getCanonicalName());
        IKey keyIObject = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject");
        IKey keyChannelHandler = Keys.getKeyByName("info.smart_tools.smartactors.http_endpoint.channel_handler_netty.ChannelHandlerNetty");
        IKey keyIFieldName = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        IOC.register(
                keyMessageProcessingSequence,
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                boolean switchScopeOnStartup = args.length > 3 ? (Boolean)args[3] : true;
                                return new MessageProcessingSequence((int) args[0], args[1], (IObject)args[2], switchScopeOnStartup);
                            } catch (InvalidArgumentException | ResolutionException | ChainNotFoundException ignored) {
                            }
                            return null;
                        }
                )
        );
        IOC.register(
                keyChannelHandler,
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IChannelHandler handler = new ChannelHandlerNetty();
                            handler.init(args[0]);
                            return handler;
                        }
                )
        );
        IOC.register(
                Keys.getKeyByName("info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> new DeserializeStrategyPostJson(mapperStub)
                ));
        IOC.register(
                Keys.getKeyByName("EmptyIObject"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> new DSObject()
                )
        );

        IResponseHandler responseHandler = mock(IResponseHandler.class);
        try {
            endpoint = createEndpoint(environmentHandler, receiver, mapperStub);
        } catch (ResolutionException e) {
            fail("Failed to create endpoint");
        }
        client = createClient(responseHandler);
        endpoint.start().thenCompose(x -> client.start()).get();
    }

    @AfterMethod
    public void tearDown() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(endpoint.stop()).get();
    }


    /* @Test
     public void whenEndpointHandlerReceivesRequest_ItShouldHandleEnvironmentHandler()
             throws ResolutionException, InvalidArgumentException, EnvironmentHandleException, RequestHandlerInternalException {
         IObject stubMessage = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{\"hello\": \"world\"}");
         when(mapperStub.deserialize(any(byte[].class))).thenReturn(stubMessage);
         HttpRequest request = createTestRequest();
         sendRequest(request);
         verify(environmentHandler, timeout(1000)).handle(any(IObject.class), any(IReceiverChain.class), any(null));
     }
 */
    protected CompletableFuture<Void> sendRequest(HttpRequest request) {
        return client.send(request);
    }

    protected HttpServer createEndpoint(
            IEnvironmentHandler environmentHandler, Object receiverName, IMessageMapper<byte[]> mapper
    ) throws ResolutionException, ScopeProviderException, UpCounterCallbackExecutionException {
        Map<String, IDeserializeStrategy> strategies = new HashMap<>();
        strategies.put("application/json", new DeserializeStrategyPostJson(mapper));
        return new HttpEndpoint(getTestingPort(), 4096, ScopeProvider.getCurrentScope(),
                ModuleManager.getCurrentModule(), environmentHandler, receiverName, "", mock(IUpCounter.class));
    }

    protected HttpClient createClient(IResponseHandler handler) throws URISyntaxException, RequestSenderException {
        return new HttpClient(new URI("http://localhost:" + getTestingPort()), handler);
    }

    protected Class<? extends FullHttpResponse> getResponseClass() {
        return FullHttpResponse.class;
    }

    protected HttpRequest createTestRequest() {
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost:" + getTestingPort());
        request.headers().set(HttpHeaders.Names.HOST, "localhost");
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
        request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
        request.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        return request;
    }
}