package info.smart_tools.smartactors.core.http_endpoint;

import info.smart_tools.smartactors.core.DeserializeStrategyPostJson;
import info.smart_tools.smartactors.core.http_client.HttpClient;
import info.smart_tools.smartactors.core.HttpEndpoint;
import info.smart_tools.smartactors.core.IDeserializeStrategy;
import info.smart_tools.smartactors.core.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.http_server.HttpServer;
import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_processing_sequence.MessageProcessingSequence;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


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
    public void setUp() throws ExecutionException, InterruptedException, URISyntaxException, InvalidArgumentException, ResolutionException, RegistrationException, ScopeProviderException {
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
                                return new DSObject((String) args[0]);
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

        ChannelInboundHandler handler = new SimpleChannelInboundHandler<FullHttpResponse>(getResponseClass()) {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
                me.handlerStub.accept(ctx, msg);
            }
        };
        try {
            endpoint = createEndpoint(environmentHandler, receiver, mapperStub);
        } catch (ResolutionException e) {
            fail("Failed to create endpoint");
        }
        client = createClient(handler);
        endpoint.start().thenCompose(x -> client.start()).get();
    }

    @AfterMethod
    public void tearDown() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(endpoint.stop()).get();
    }

    @Test
    public void whenEndpointHandlerReceivesRequest_ItShouldHandleEnvironmentHandler() throws ResolutionException {
        IObject stubMessage = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), "{\"hello\": \"world\"}");
        when(mapperStub.deserialize(any(byte[].class))).thenReturn(stubMessage);
        HttpRequest request = createTestRequest();
        sendRequest(request);
        verify(environmentHandler, timeout(1000)).handle(any(IObject.class), any(IReceiverChain.class));
    }

    protected CompletableFuture<Void> sendRequest(HttpRequest request) {
        return client.send(request);
    }

    protected HttpServer createEndpoint(
            IEnvironmentHandler environmentHandler, IReceiverChain receiver, IMessageMapper<byte[]> mapper
    ) throws ResolutionException, ScopeProviderException {
        Map<String, IDeserializeStrategy> strategies = new HashMap<>();
        strategies.put("application/json", new DeserializeStrategyPostJson(mapper));
        return new HttpEndpoint(getTestingPort(), 4096, ScopeProvider.getCurrentScope(),
                environmentHandler, receiver, strategies);
    }

    protected HttpClient createClient(ChannelInboundHandler handler) throws URISyntaxException {
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