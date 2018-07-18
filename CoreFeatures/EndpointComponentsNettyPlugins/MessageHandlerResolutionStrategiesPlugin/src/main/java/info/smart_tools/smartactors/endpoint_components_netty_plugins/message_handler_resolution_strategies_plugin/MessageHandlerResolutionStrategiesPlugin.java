package info.smart_tools.smartactors.endpoint_components_netty_plugins.message_handler_resolution_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.simple_strict_storage_strategy.SimpleStrictStorageStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_component_netty.cookies_setter.CookiesSetter;
import info.smart_tools.smartactors.endpoint_component_netty.http_path_parse.HttpPathParse;
import info.smart_tools.smartactors.endpoint_components_generic.default_outbound_connection_channel.DefaultOutboundConnectionChannel;
import info.smart_tools.smartactors.endpoint_components_generic.message_handler_resolution_strategy.MessageHandlerResolutionStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.parse_tree.IParseTree;
import info.smart_tools.smartactors.endpoint_components_generic.parse_tree.ParseTree;
import info.smart_tools.smartactors.endpoint_components_netty.channel_pool_handlers.AcquireChannelFromPoolHandler;
import info.smart_tools.smartactors.endpoint_components_netty.channel_pool_handlers.ReleasePooledChannelHandler;
import info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers.BindRequestToChannelHandler;
import info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers.StoreBoundRequestHandler;
import info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers.*;
import info.smart_tools.smartactors.endpoint_components_netty.http_exceptional_action.HttpServerExceptionalAction;
import info.smart_tools.smartactors.endpoint_components_netty.http_headers_setter.HttpHeadersSetter;
import info.smart_tools.smartactors.endpoint_components_netty.http_method_setter.HttpMethodSetter;
import info.smart_tools.smartactors.endpoint_components_netty.http_query_string_parser.HttpQueryStringParser;
import info.smart_tools.smartactors.endpoint_components_netty.http_response_metadata_presetup.HttpResponseMetadataPreSetup;
import info.smart_tools.smartactors.endpoint_components_netty.http_status_setter.HttpStatusSetter;
import info.smart_tools.smartactors.endpoint_components_netty.http_web_socket_upgrade_listener.HttpWebSocketUpgradeListenerSetter;
import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_channel_handler.InboundNettyChannelHandler;
import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.ReleaseNettyMessageHandler;
import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.RetainNettyMessageHandler;
import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.message_extractors.InboundMessageExtractor;
import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.message_extractors.OutboundMessageExtractor;
import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.message_extractors.WrappedInboundMessageExtractor;
import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.message_extractors.WrappedOutboundMessageExtractor;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPool;
import info.smart_tools.smartactors.endpoint_components_netty.outbound_netty_message_creation_handler.OutboundNettyMessageCreationHandler;
import info.smart_tools.smartactors.endpoint_components_netty.send_netty_message_message_handler.SendNettyMessageMessageHandler;
import info.smart_tools.smartactors.endpoint_components_netty.ssl_channel_initialization_handler.SslChannelInitializationHandler;
import info.smart_tools.smartactors.endpoint_components_netty.wrap_inbound_netty_message_to_message_byte_array_message_handler.WrapInboundNettyMessageToMessageByteArrayMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannelListener;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.ssl.SslContext;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Plugin that registers some {@link info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler
 * message handler} implementations specific for Netty endpoints and some related strategies.
 */
public class MessageHandlerResolutionStrategiesPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public MessageHandlerResolutionStrategiesPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("netty_endpoint_message_handlers")
    @After({
            "netty_outbound_message_factories_strategy",
            "netty_ssl_engine_strategies",
    })
    public void registerHandlerStrategies() throws Exception {
        IFieldName maxAggregatedContentLenFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                "maxAggregatedContentLength");
        IFieldName pipelineFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "pipeline");
        IFieldName errorPipelineFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "errorPipeline");
        IFieldName messageClassFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageClass");
        IFieldName listenerFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "listener");
        IFieldName templatesFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "templates");
        IFieldName messageExtractorFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageExtractor");
        IFieldName messageTypeFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageType");
        IFieldName pathFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "path");
        IFieldName setupPipelineFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "setupPipeline");
        IFieldName poolTypeFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "poolType");

        IAdditionDependencyStrategy storage = IOC.resolve(
                Keys.getOrAdd("expandable_strategy#endpoint message handler"));

        storage.register("netty/http cookie setter",
                new SingletonStrategy(new CookiesSetter()));
        storage.register("netty/http headers setter",
                new SingletonStrategy(new HttpHeadersSetter()));
        storage.register("netty/http response status code setter",
                new SingletonStrategy(new HttpStatusSetter()));

        IOC.register(Keys.getOrAdd(IParseTree.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(args -> new ParseTree()));

        /*
         * {
         *  "type": "netty/fixed http path parser",
         *  "templates": [
         *      ".. path template ..",
         *      .. more templates ..
         *  ]
         * }
         */
        storage.register("netty/fixed http path parser",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    List templates = (List) handlerConf.getValue(templatesFN);

                    if (null == templates) {
                        templates = (List) endpointConf.getValue(templatesFN);
                    }

                    return new HttpPathParse(templates);
                }));

        storage.register("netty/http query string parser",
                new SingletonStrategy(new HttpQueryStringParser()));

        storage.register("netty/http response metadata presetup",
                new SingletonStrategy(new HttpResponseMetadataPreSetup()));

        /*
         * {
         *  "type": "netty/setup/http server channel",
         *  "maxAggregatedContentLength": #max. length of aggregated message#
         * }
         */
        storage.register("netty/setup/http server channel",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    int maxAggregatedContentLen = ((Number) handlerConf.getValue(maxAggregatedContentLenFN)).intValue();

                    return new HTTPServerChannelSetupHandler(maxAggregatedContentLen);
                }));

        /*
         * {
         *  "type": "netty/setup/http client channel",
         *  "maxAggregatedContentLength": #max. length of aggregated message#
         * }
         */
        storage.register("netty/setup/http client channel",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    int maxAggregatedContentLen = ((Number) handlerConf.getValue(maxAggregatedContentLenFN)).intValue();

                    return new HTTPClientChannelSetupHandler(maxAggregatedContentLen);
                }));

        /*
         * {
         *  "type": "netty/setup/http web-socket upgrade listener",
         *  "pipeline": ".. pipeline name ..",
         *  "path": ".. web-socket path .."
         * }
         */
        storage.register("netty/setup/http web-socket upgrade listener",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    IEndpointPipeline pipeline = pipelineSet.getPipeline((String) handlerConf.getValue(pipelineFN));
                    String path = (String) handlerConf.getValue(pathFN);

                    return new HttpWebSocketUpgradeListenerSetter(pipeline, path);
                }));

        /*
         * {
         *  "type": "netty/setup/inbound chanel handler",
         *  "pipeline": ".. pipeline name ..",
         *  "errorPipeline": ".. error pipeline name ..",
         *  "messageClass": ".. canonical name of message class .."
         * }
         */
        storage.register("netty/setup/inbound chanel handler",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    String pipelineId = (String) handlerConf.getValue(pipelineFN);
                    IEndpointPipeline pipeline = pipelineSet.getPipeline(pipelineId);
                    String errorPipelineId = (String) handlerConf.getValue(errorPipelineFN);
                    IEndpointPipeline errorPipeline = pipelineSet.getPipeline(errorPipelineId);
                    Class messageClass = getClass().getClassLoader().loadClass((String) handlerConf.getValue(messageClassFN));

                    return new InboundNettyChannelHandlerSetupHandler(
                            new InboundNettyChannelHandler(
                                    pipeline,
                                    errorPipeline,
                                    messageClass
                            ));
                }));

        /*
         * {
         *  "type": "netty/setup/attach dynamic outbound channel",
         *  "pipeline": ".. pipeline id ..",
         *  "listener": ".. channel listener dependency name .."
         * }
         */
        storage.register("netty/setup/attach dynamic outbound channel",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    String pipelineName = (String) handlerConf.getValue(pipelineFN);
                    IEndpointPipeline pipeline = pipelineSet.getPipeline(pipelineName);
                    IOutboundConnectionChannelListener listener =
                            IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), handlerConf.getValue(listenerFN)), handlerConf);

                    IFunction<Channel, IOutboundConnectionChannel> outboundChannelProvider =
                            ch -> new DefaultOutboundConnectionChannel<>(pipeline, ch);

                    return new OutboundChannelCreator<>(
                            () -> UUID.randomUUID().toString(),
                            outboundChannelProvider,
                            listener
                    );
                }));

        storage.register("netty/store outbound channel id",
                new SingletonStrategy(new StoreOutboundChannelIdToContext<>()));

        SimpleStrictStorageStrategy extractorStorage = new SimpleStrictStorageStrategy(new HashMap<>(), "message extractor");
        extractorStorage.register("unwrapped inbound", new SingletonStrategy(new InboundMessageExtractor()));
        extractorStorage.register("unwrapped outbound", new SingletonStrategy(new OutboundMessageExtractor()));
        extractorStorage.register("wrapped inbound", new SingletonStrategy(new WrappedInboundMessageExtractor()));
        extractorStorage.register("wrapped outbound", new SingletonStrategy(new WrappedOutboundMessageExtractor()));

        IOC.register(Keys.getOrAdd("netty message extractor"), extractorStorage);
        IOC.register(Keys.getOrAdd("expandable_strategy#netty message extractor"),
                new SingletonStrategy(extractorStorage));

        /*
         * {
         *  "type": "netty/retain message",
         *  "messageExtractor": ".. message extractor name .."
         * }
         */
        storage.register("netty/retain message",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    String extractorName = (String) handlerConf.getValue(messageExtractorFN);
                    IFunction extractor = IOC.resolve(Keys.getOrAdd("netty message extractor"), extractorName);
                    return new RetainNettyMessageHandler(extractor);
                }));

        /*
         * {
         *  "type": "netty/release message",
         *  "messageExtractor": ".. message extractor name .."
         * }
         */
        storage.register("netty/release message",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    String extractorName = (String) handlerConf.getValue(messageExtractorFN);
                    IFunction extractor = IOC.resolve(Keys.getOrAdd("netty message extractor"), extractorName);
                    return new ReleaseNettyMessageHandler(extractor);
                }));

        /*
         * {
         *  "type": "netty/create outbound message",
         *  "messageType": ".. message factory name .."
         * }
         */
        storage.register("netty/create outbound message",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    Object typeName = handlerConf.getValue(messageTypeFN);
                    IFunction0 factory = IOC.resolve(Keys.getOrAdd("netty outbound message factory"), typeName);
                    return new OutboundNettyMessageCreationHandler(factory);
                }));

        storage.register("netty/send outbound message",
                new SingletonStrategy(new SendNettyMessageMessageHandler()));
        storage.register("netty/wrap inbound message",
                new SingletonStrategy(new WrapInboundNettyMessageToMessageByteArrayMessageHandler()));

        /*
         * {
         *  "type": "netty/ssl-setup/server",
         *  "ciphers": [
         *   .. list of supported ciphers ..
         *  ]
         * }
         *
         * In endpoint configuration:
         *
         * {
         *  ..
         *  "serverCertificate": ".. certificate (*.crt file) path ..",
         *  "serverCertificateKey": ".. key (*.key file) path ..",
         *  ..
         * }
         */
        storage.register("netty/ssl-setup/server",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    SslContext context = IOC.resolve(
                            Keys.getOrAdd("netty server endpoint ssl context"), handlerConf, endpointConf);

                    return new SslChannelInitializationHandler(context);
                }));

        /*
         * {
         *  "type": "netty/ssl-setup/client",
         *  "ciphers": [
         *   .. list of supported ciphers ..
         *  ]
         * }
         */
        storage.register("netty/ssl-setup/client",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    SslContext context = IOC.resolve(
                            Keys.getOrAdd("netty client endpoint ssl context"), handlerConf, endpointConf);

                    return new SslChannelInitializationHandler(context);
                }));

        /*
         * {
         *  "type": "netty/client/acquire channel",
         *  "setupPipeline": ".. channel setup pipeline ..",
         *  "poolType": ".. channel pool type ..",
         *  .. pool options ..
         * }
         */
        storage.register("netty/client/acquire channel",
                new MessageHandlerResolutionStrategy((type, handlerConf, endpointConf, pipelineSet) -> {
                    IEndpointPipeline<IDefaultMessageContext<SocketChannel, Void, SocketChannel>> setupPipeline
                            = pipelineSet.getPipeline((String) handlerConf.getValue(setupPipelineFN));
                    IAction<SocketChannel> setupAction = ch -> {
                        try {
                            IDefaultMessageContext<SocketChannel, Void, SocketChannel> ctx = setupPipeline.getContextFactory().execute();

                            ctx.setSrcMessage(ch);
                            ctx.setConnectionContext(ch);

                            setupPipeline.getInputCallback().handle(ctx);
                        } catch (FunctionExecutionException | MessageHandlerException e) {
                            throw new ActionExecuteException(e);
                        }
                    };

                    ISocketConnectionPool pool = IOC.resolve(Keys.getOrAdd("netty client connection pool"),
                            handlerConf.getValue(poolTypeFN), handlerConf, setupAction);

                    return new AcquireChannelFromPoolHandler(pool);
                }));

        storage.register("netty/client/release channel",
                new SingletonStrategy(new ReleasePooledChannelHandler()));

        storage.register("netty/client/bind request to channel",
                new SingletonStrategy(new BindRequestToChannelHandler()));
        storage.register("netty/client/get bound request",
                new SingletonStrategy(new StoreBoundRequestHandler()));

        storage.register("netty/set outbound http request method",
                new SingletonStrategy(new HttpMethodSetter()));
    }

    @Item("netty_exceptional_actions")
    public void registerExceptionalActions() throws Exception {
        IFieldName statusFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "status");

        IAdditionDependencyStrategy storage = IOC.resolve(
                Keys.getOrAdd("expandable_strategy#exceptional endpoint action"));
        storage.register("netty/http/server/default", new ApplyFunctionToArgumentsStrategy(args -> {
            IObject conf = (IObject) args[1];

            try {
                int status = ((Number) conf.getValue(statusFN)).intValue();

                return new HttpServerExceptionalAction(HttpResponseStatus.valueOf(status));
            } catch (ReadValueException | InvalidArgumentException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    @Item("netty_outbound_message_factories_strategy")
    public void registerOutboundMessageFactories() throws Exception {
        SimpleStrictStorageStrategy storage = new SimpleStrictStorageStrategy("outbound netty message");

        IOC.register(Keys.getOrAdd("netty outbound message factory"), storage);
        IOC.register(Keys.getOrAdd("expandable_strategy#netty outbound message factory"),
                new SingletonStrategy(storage));
    }

    @Item("netty_default_outbound_message_factories")
    @After({
            "netty_outbound_message_factories_strategy"
    })
    public void registerDefaultOutboundNettyMessageFactories() throws Exception {
        IAdditionDependencyStrategy storage = IOC.resolve(Keys.getOrAdd("expandable_strategy#netty outbound message factory"));

        storage.register("http response", new SingletonStrategy(
                (IFunction0) () -> new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)));
        storage.register("websocket text frame", new SingletonStrategy(
                (IFunction0) TextWebSocketFrame::new));
        storage.register("websocket binary frame", new SingletonStrategy(
                (IFunction0) BinaryWebSocketFrame::new));
        storage.register("http request", new SingletonStrategy(
                (IFunction0) () -> new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/")));
    }
}
