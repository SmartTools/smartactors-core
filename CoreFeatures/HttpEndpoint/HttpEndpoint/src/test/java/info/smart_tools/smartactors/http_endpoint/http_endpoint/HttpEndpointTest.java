package info.smart_tools.smartactors.http_endpoint.http_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.IResponseHandler;
import info.smart_tools.smartactors.http_endpoint.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.http_endpoint.deserialize_strategy_post_json.DeserializeStrategyPostJson;
import info.smart_tools.smartactors.http_endpoint.http_client.HttpClient;
import info.smart_tools.smartactors.http_endpoint.http_server.HttpServer;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.endpoint.interfaces.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.exception.RequestSenderException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.message_processing.message_processing_sequence.MessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;


public class HttpEndpointTest {
    protected IMessageMapper<byte[]> mapperStub;
    protected HttpServer endpoint;
    protected HttpClient client;
    protected IEnvironmentHandler environmentHandler;
    protected BiConsumer<ChannelHandlerContext, FullHttpResponse> handlerStub;
    protected IReceiverChain receiver;


    protected int getTestingPort() {
        return 9001;
    }


    @BeforeMethod
    public void setUp() throws ExecutionException, InterruptedException, URISyntaxException, InvalidArgumentException, ResolutionException, RegistrationException, ScopeProviderException, RequestSenderException, UpCounterCallbackExecutionException {
        mapperStub = mock(IMessageMapper.class);
        receiver = mock(IReceiverChain.class);
        environmentHandler = mock(IEnvironmentHandler.class);
        HttpEndpointTest me = this;

        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );

        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope mainScope = ScopeProvider.getScope(keyOfMainScope);
        ScopeProvider.setCurrentScope(mainScope);

        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy()
        );
        IKey keyMessageProcessingSequence = Keys.getOrAdd(MessageProcessingSequence.class.getCanonicalName());
        IKey keyIObject = Keys.getOrAdd(IObject.class.getCanonicalName());
        IKey keyChannelHandler = Keys.getOrAdd(ChannelHandlerNetty.class.getCanonicalName());
        IKey keyIFieldName = Keys.getOrAdd(IFieldName.class.getCanonicalName());
        IOC.register(
                keyMessageProcessingSequence,
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new MessageProcessingSequence((int) args[0], (IReceiverChain) args[1]);
                            } catch (InvalidArgumentException | ResolutionException ignored) {
                            }
                            return null;
                        }
                )
        );
        IOC.register(
                keyIObject,
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return args.length > 0 ? new DSObject((String) args[0]) : new DSObject();
                            } catch (InvalidArgumentException ignored) {
                            }
                            return null;
                        }
                )
        );
        IOC.register(
                keyIFieldName,
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException ignored) {
                            }
                            return null;
                        }
                )
        );
        IOC.register(
                keyChannelHandler,
                new CreateNewInstanceStrategy(
                        (args) -> {
                            IChannelHandler handler = new ChannelHandlerNetty();
                            handler.init(args[0]);
                            return handler;
                        }
                )
        );
        IOC.register(Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName()),
                new CreateNewInstanceStrategy(
                        (args) -> new DeserializeStrategyPostJson(mapperStub)
                ));
        IOC.register(Keys.getOrAdd("EmptyIObject"), new CreateNewInstanceStrategy(
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
         IObject stubMessage = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), "{\"hello\": \"world\"}");
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
            IEnvironmentHandler environmentHandler, IReceiverChain receiver, IMessageMapper<byte[]> mapper
    ) throws ResolutionException, ScopeProviderException, UpCounterCallbackExecutionException {
        Map<String, IDeserializeStrategy> strategies = new HashMap<>();
        strategies.put("application/json", new DeserializeStrategyPostJson(mapper));
        return new HttpEndpoint(getTestingPort(), 4096, ScopeProvider.getCurrentScope(),
                environmentHandler, receiver, "", mock(IUpCounter.class));
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