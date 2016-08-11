package info.smart_tools.smartactors.plugin.https_endpoint;

import info.smart_tools.smartactors.core.HttpEndpoint;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.deserialize_strategy_post_json.DeserializeStrategyPostJson;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.endpoint_handler.exceptions.EndpointException;
import info.smart_tools.smartactors.core.environment_handler.EnvironmentHandler;
import info.smart_tools.smartactors.core.http_response_sender.HttpResponseSender;
import info.smart_tools.smartactors.core.https_endpoint.HttpsEndpoint;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.icookies_extractor.ICookiesSetter;
import info.smart_tools.smartactors.core.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iheaders_extractor.IHeadersExtractor;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.iresponse_sender.IResponseSender;
import info.smart_tools.smartactors.core.iresponse_status_extractor.IResponseStatusExtractor;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.issl_engine_provider.ISslEngineProvider;
import info.smart_tools.smartactors.core.issl_engine_provider.exception.SSLEngineProviderException;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_to_bytes_mapper.MessageToBytesMapper;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.ssl_engine_provider.SslEngineProvider;
import info.smart_tools.smartactors.strategy.cookies_setter.CookiesSetter;
import info.smart_tools.smartactors.strategy.http_headers_setter.HttpHeadersExtractor;
import info.smart_tools.smartactors.strategy.respons_status_extractor.ResponseStatusExtractor;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Plugin for register http/https endpoint at IOC
 */
public class HttpsEndpointPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     *
     * @param bootstrap bootstrap
     */
    public HttpsEndpointPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("CreateHttpsEndpoint");
            item
                    .after("IOC")
                    .after("message_processor")
                    .after("message_processing_sequence")
                    .after("response")
                    .after("response_content_strategy")
                    .after("field_name")
                    .before("configure")
                    .process(
                            () -> {
                                try {
                                    IFieldName typeFieldName =
                                            IOC.resolve(
                                                    IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                                                    "type"
                                            );
                                    IFieldName portFieldName =
                                            IOC.resolve(
                                                    IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                                                    "port"
                                            );
                                    IFieldName startChainNameFieldName =
                                            IOC.resolve(
                                                    IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                                                    "startChain"
                                            );
                                    IFieldName stackDepthFieldName =
                                            IOC.resolve(
                                                    IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                                                    "stackDepth"
                                            );
                                    IFieldName maxContentLengthFieldName =
                                            IOC.resolve(
                                                    IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                                                    "maxContentLength"
                                            );
                                    IFieldName endpointNameFieldName =
                                            IOC.resolve(
                                                    IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                                                    "endpointName"
                                            );

                                    IFieldName queueFieldName =
                                            IOC.resolve(
                                                    IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                                                    "queue"
                                            );
                                    ICookiesSetter cookiesSetter = new CookiesSetter();
                                    IKey httpsEndpointKey = Keys.getOrAdd("https_endpoint");
                                    IKey cookiesSetterKey = Keys.getOrAdd(ICookiesSetter.class.getCanonicalName());
                                    IOC.register(cookiesSetterKey,
                                            new SingletonStrategy(cookiesSetter));

                                    IHeadersExtractor headersSetter = new HttpHeadersExtractor();

                                    IKey headersSetterKey = Keys.getOrAdd(IHeadersExtractor.class.getCanonicalName());
                                    IOC.register(headersSetterKey,
                                            new SingletonStrategy(headersSetter));

                                    IResponseStatusExtractor responseStatusExtractor = new ResponseStatusExtractor();
                                    IKey responseStatusExtractorKey = Keys.getOrAdd(
                                            IResponseStatusExtractor.class.getCanonicalName());
                                    IOC.register(responseStatusExtractorKey,
                                            new SingletonStrategy(responseStatusExtractor));

                                    IOC.register(Keys.getOrAdd(ISslEngineProvider.class.getCanonicalName()),
                                            new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        ISslEngineProvider sslContextProvider = new SslEngineProvider();
                                                        try {
                                                            sslContextProvider.init((IObject) args[0]);
                                                        } catch (SSLEngineProviderException e) {
                                                        }
                                                        return sslContextProvider;
                                                    }
                                            )
                                    );

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
                                                        } catch (ReadValueException | InvalidArgumentException e) {
                                                        }
                                                        return null;
                                                    }
                                            )
                                    );


                                    IOC.register(Keys.getOrAdd(FileInputStream.class.getCanonicalName()),
                                            new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        try {
                                                            return new FileInputStream((String) args[0]);
                                                        } catch (FileNotFoundException e) {
                                                        }
                                                        return null;
                                                    }
                                            ));

                                    IOC.register(httpsEndpointKey,
                                            new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        IObject configuration = (IObject) args[0];
                                                        try {
                                                            IEnvironmentHandler environmentHandler = IOC.resolve(
                                                                    Keys.getOrAdd(IEnvironmentHandler.class.getCanonicalName()),
                                                                    configuration);
                                                            ISslEngineProvider sslContextProvider =
                                                                    IOC.resolve(
                                                                            Keys.getOrAdd(ISslEngineProvider.class.getCanonicalName()),
                                                                            configuration
                                                                    );
                                                            return new HttpsEndpoint((Integer) configuration.getValue(portFieldName),
                                                                    (Integer) configuration.getValue(maxContentLengthFieldName),
                                                                    ScopeProvider.getCurrentScope(), environmentHandler,
                                                                    (IReceiverChain) configuration.getValue(startChainNameFieldName),
                                                                    sslContextProvider);
                                                        } catch (ReadValueException | InvalidArgumentException | ScopeProviderException |
                                                                ResolutionException | EndpointException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            )
                                    );
                                    IOC.register(Keys.getOrAdd("http_endpoint"),
                                            new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        IObject configuration = (IObject) args[0];
                                                        try {
                                                            IEnvironmentHandler environmentHandler = IOC.resolve(
                                                                    Keys.getOrAdd(IEnvironmentHandler.class.getCanonicalName()),
                                                                    configuration);
                                                            return new HttpEndpoint((Integer) configuration.getValue(portFieldName),
                                                                    (Integer) configuration.getValue(maxContentLengthFieldName),
                                                                    ScopeProvider.getCurrentScope(), environmentHandler,
                                                                    (IReceiverChain) configuration.getValue(startChainNameFieldName));
                                                        } catch (ReadValueException | InvalidArgumentException | ScopeProviderException |
                                                                ResolutionException | EndpointException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            )
                                    );
                                    IMessageMapper<byte[]> messageMapper = new MessageToBytesMapper();
                                    IDeserializeStrategy deserializeStrategy =
                                            new DeserializeStrategyPostJson(messageMapper);
                                    IKey deserializeStrategyKey = Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName());

                                    IOC.register(deserializeStrategyKey, new SingletonStrategy(deserializeStrategy));

                                    IKey httpResponseSender = Keys.getOrAdd(IResponseSender.class.getCanonicalName());
                                    // TODO: 21.07.16 add opportunity to set custom name of the sender
                                    HttpResponseSender sender = new HttpResponseSender("default");
                                    IOC.register(httpResponseSender,
                                            new SingletonStrategy(
                                                    sender
                                            ));

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

                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }

                            }
                    );
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Can't load \"CreateHttpsEndpoint\" plugin", e);
        }
    }
}
