package info.smart_tools.smartactors.http_endpoint_plugins.http_endpoint_plugin;


import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.http_endpoint.deserialize_strategy_post_form_urlencoded.DeserializeStrategyPostFormUrlencoded;
import info.smart_tools.smartactors.http_endpoint.http_endpoint.HttpEndpoint;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.http_endpoint.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.http_endpoint.deserialize_strategy_get.DeserializeStrategyGet;
import info.smart_tools.smartactors.http_endpoint.deserialize_strategy_get.parse_tree.IParseTree;
import info.smart_tools.smartactors.http_endpoint.deserialize_strategy_get.parse_tree.ParseTree;
import info.smart_tools.smartactors.http_endpoint.deserialize_strategy_post_json.DeserializeStrategyPostJson;
import info.smart_tools.smartactors.http_endpoint.environment_handler.EnvironmentHandler;
import info.smart_tools.smartactors.http_endpoint.http_response_sender.HttpResponseSender;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.endpoint.interfaces.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.http_endpoint.message_to_bytes_mapper.MessageToBytesMapper;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.http_endpoint.cookies_setter.CookiesSetter;
import info.smart_tools.smartactors.http_endpoint.http_headers_setter.HttpHeadersExtractor;
import info.smart_tools.smartactors.http_endpoint.respons_status_extractor.ResponseStatusExtractor;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.List;

/**
 * Plugin, that register {@link HttpEndpoint} and {@link HttpResponseSender} at {@link IOC}
 */
public class HttpEndpointPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    private IFieldName typeFieldName;
    private IFieldName portFieldName;
    private IFieldName startChainNameFieldName;
    private IFieldName stackDepthFieldName;
    private IFieldName maxContentLengthFieldName;
    private IFieldName endpointNameFieldName;
    private IFieldName queueFieldName;
    private IFieldName templatesFieldName;

    /**
     * Constructor
     *
     * @param bootstrap bootstrap
     */
    public HttpEndpointPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("CreateHttpEndpoint");
            item
                    .after("EndpointPlugin")
                    .after("response")
                    .after("response_content_strategy")
                    .before("configure")
                    .process(
                            () -> {
                                try {
                                    initializeFieldNames();
                                    IKey httpEndpointKey = Keys.getOrAdd("http_endpoint");
                                    registerCookiesSetter();
                                    registerHeadersExtractor();
                                    registerResponseStatusExtractor();
                                    registerExceptionalResponse();
                                    IOC.register(
                                            Keys.getOrAdd(IEnvironmentHandler.class.getCanonicalName()),
                                            new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        IObject configuration = (IObject) args[0];
                                                        IQueue queue = null;
                                                        Integer stackDepth = null;
                                                        try {
                                                            queue = (IQueue) configuration.getValue(queueFieldName);
                                                            stackDepth =
                                                                    (Integer) configuration.getValue(stackDepthFieldName);
                                                            return new EnvironmentHandler(queue, stackDepth);
                                                        } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                                                        }
                                                        return null;
                                                    }
                                            )
                                    );

                                    IOC.register(httpEndpointKey,
                                            new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        IObject configuration = (IObject) args[0];
                                                        try {

                                                            IOC.resolve(
                                                                    Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName()),
                                                                    "HTTP_GET",
                                                                    configuration.getValue(endpointNameFieldName),
                                                                    configuration.getValue(templatesFieldName));
                                                            IOC.resolve(
                                                                    Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName()),
                                                                    "HTTP_application/json",
                                                                    configuration.getValue(endpointNameFieldName));
                                                            IOC.resolve(
                                                                    Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName()),
                                                                    "HTTP_application/x-www-form-urlencoded",
                                                                    configuration.getValue(endpointNameFieldName));

                                                            IUpCounter upCounter = IOC.resolve(Keys.getOrAdd("root upcounter"));

                                                            IEnvironmentHandler environmentHandler = IOC.resolve(
                                                                    Keys.getOrAdd(IEnvironmentHandler.class.getCanonicalName()),
                                                                    configuration);
                                                            HttpEndpoint endpoint = new HttpEndpoint(
                                                                    (Integer) configuration.getValue(portFieldName),
                                                                    (Integer) configuration.getValue(maxContentLengthFieldName),
                                                                    ScopeProvider.getCurrentScope(), environmentHandler,
                                                                    (IReceiverChain) configuration.getValue(startChainNameFieldName),
                                                                    (String) configuration.getValue(endpointNameFieldName),
                                                                    upCounter);

                                                            upCounter.onShutdownComplete(endpoint::stop);

                                                            return endpoint;
                                                        } catch (ReadValueException | InvalidArgumentException
                                                                | ScopeProviderException | ResolutionException
                                                                | UpCounterCallbackExecutionException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            )
                                    );
                                    registerDeserializationStrategies();
                                    registerResponseSenders();
                                    IKey emptyIObjectKey = Keys.getOrAdd("EmptyIObject");
                                    IOC.register(emptyIObjectKey, new CreateNewInstanceStrategy(
                                                    (args) -> new DSObject()
                                            )
                                    );

                                    IKey channelHandlerNettyKey = Keys.getOrAdd(ChannelHandlerNetty.class.getCanonicalName());
                                    IOC.register(channelHandlerNettyKey,
                                            new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        ChannelHandlerNetty channelHandlerNetty = new ChannelHandlerNetty();
                                                        channelHandlerNetty.init((ChannelHandlerContext) args[0]);
                                                        return channelHandlerNetty;
                                                    }
                                            ));

                                } catch (ResolutionException e) {
                                    throw new ActionExecuteException("EndpointCollection plugin can't load: can't get key", e);
                                } catch (InvalidArgumentException e) {
                                    throw new ActionExecuteException("EndpointCollection plugin can't load: can't create strategy", e);
                                } catch (RegistrationException | AdditionDependencyStrategyException e) {
                                    throw new ActionExecuteException("EndpointCollection plugin can't load: can't register new strategy", e);
                                }
                            }
                    );
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Can't load \"CreateHttpEndpoint\" plugin", e);
        }
    }

    private void initializeFieldNames() throws ResolutionException {
        typeFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                        "type"
                );
        portFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                        "port"
                );
        startChainNameFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                        "startChain"
                );
        stackDepthFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                        "stackDepth"
                );
        maxContentLengthFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                        "maxContentLength"
                );
        endpointNameFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                        "name"
                );

        queueFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                        "queue"
                );

        templatesFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                        "templates"
                );
    }

    private void registerResponseSenders() throws ResolutionException, InvalidArgumentException, RegistrationException,
            AdditionDependencyStrategyException {
        IAdditionDependencyStrategy responseSenderChooser =
                IOC.resolve(Keys.getOrAdd("ResponseSenderChooser"));

        IOC.register(Keys.getOrAdd("http_request_key_for_response_sender"), new ApplyFunctionToArgumentsStrategy(
                        (args) ->
                                "HTTP_POST"

                )
        );

        responseSenderChooser.register("HTTP_POST",
                new CreateNewInstanceStrategy(
                        (args) -> {
                            //args[0] - type of the request
                            //args[1] - name of the endpoint
                            try {
                                return new HttpResponseSender((String) args[1]);
                            } catch (ResolutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
    }

    private void registerCookiesSetter() throws ResolutionException, InvalidArgumentException, RegistrationException,
            AdditionDependencyStrategyException {
        IAdditionDependencyStrategy cookiesSetterChooser =
                IOC.resolve(Keys.getOrAdd("CookiesSetterChooser"));

        IOC.register(Keys.getOrAdd("key_for_cookies_extractor"), new ApplyFunctionToArgumentsStrategy(
                        (args) ->
                                "HTTP"
                )
        );

        cookiesSetterChooser.register("HTTP",
                new CreateNewInstanceStrategy(
                        (args) -> new CookiesSetter()
                )
        );
    }


    private void registerHeadersExtractor() throws ResolutionException, InvalidArgumentException, RegistrationException,
            AdditionDependencyStrategyException {
        IAdditionDependencyStrategy cookiesSetterChooser =
                IOC.resolve(Keys.getOrAdd("HeadersExtractorChooser"));

        IOC.register(Keys.getOrAdd("key_for_headers_extractor"), new ApplyFunctionToArgumentsStrategy(
                        (args) ->
                                "HTTP"
                )
        );

        cookiesSetterChooser.register("HTTP",
                new CreateNewInstanceStrategy(
                        (args) -> new HttpHeadersExtractor()
                )
        );
    }


    private void registerResponseStatusExtractor() throws ResolutionException, InvalidArgumentException, RegistrationException,
            AdditionDependencyStrategyException {
        IAdditionDependencyStrategy cookiesSetterChooser =
                IOC.resolve(Keys.getOrAdd("ResponseStatusSetter"));

        IOC.register(Keys.getOrAdd("key_for_response_status_setter"), new ApplyFunctionToArgumentsStrategy(
                        (args) ->
                                "HTTP"
                )
        );

        cookiesSetterChooser.register("HTTP",
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new ResponseStatusExtractor();
                            } catch (ResolutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
    }

    private void registerDeserializationStrategies() throws ResolutionException, InvalidArgumentException, RegistrationException, AdditionDependencyStrategyException {
        IAdditionDependencyStrategy deserializationStrategyChooser =
                IOC.resolve(Keys.getOrAdd("DeserializationStrategyChooser"));

        IMessageMapper messageMapper = new MessageToBytesMapper();

        IOC.register(Keys.getOrAdd("http_request_key_for_deserialize"), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            FullHttpRequest request = (FullHttpRequest) args[0];
                            if (request.method().toString().equals("GET")) {
                                return "HTTP_GET";
                            }
                            return "HTTP_" + ((HttpHeaders) request.headers()).get(HttpHeaders.Names.CONTENT_TYPE);
                        }
                )
        );

        deserializationStrategyChooser.register("HTTP_application/json",
                new CreateNewInstanceStrategy(
                        //args[0] - type of the request
                        //args[1] - name of the endpoint
                        (args) -> new DeserializeStrategyPostJson(messageMapper)
                )
        );
        deserializationStrategyChooser.register("HTTP_application/x-www-form-urlencoded",
                new CreateNewInstanceStrategy(
                        //args[0] - type of the request
                        //args[1] - name of the endpoint
                        (args) -> new DeserializeStrategyPostFormUrlencoded()
                )
        );
        deserializationStrategyChooser.register("HTTP_GET",
                new CreateNewInstanceStrategy(
                        //args[0] - type of the request
                        //args[1] - name of the endpoint
                        (args) -> {
                            try {
                                return new DeserializeStrategyGet((List<String>) args[2]);
                            } catch (ResolutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
        IParseTree tree = new ParseTree();
        IOC.register(Keys.getOrAdd(IParseTree.class.getCanonicalName()), new SingletonStrategy(
                        tree
                )
        );
    }

    private void registerExceptionalResponse() throws InvalidArgumentException, ResolutionException, RegistrationException {
        IOC.register(Keys.getOrAdd("HttpPostParametersToIObjectException"), new SingletonStrategy(
                        new DSObject("{\"exception\": \"Request body is not json\", \"statusCode\": 400}")
                )
        );
        IOC.register(Keys.getOrAdd("HttpRequestParametersToIObjectException"), new SingletonStrategy(
                        new DSObject("{\"exception\": \"This url is not registered\", \"statusCode\": 404}")
                )
        );
        IOC.register(Keys.getOrAdd("HttpInternalException"), new SingletonStrategy(
                        new DSObject("{\"exception\": \"Internal server error\", \"statusCode\": 500}")
                )
        );
        IOC.register(Keys.getOrAdd("HttpShuttingDownException"), new SingletonStrategy(
                        new DSObject("{\"exception\": \"Service not available (shutting down)\", \"statusCode\": 503}")
                )
        );
    }
}
