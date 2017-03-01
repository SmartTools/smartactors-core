package info.smart_tools.smartactors.https_endpoint_plugins.https_endpoint_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.http_endpoint.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.http_endpoint.environment_handler.EnvironmentHandler;
import info.smart_tools.smartactors.https_endpoint.https_endpoint.HttpsEndpoint;
import info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.ISslEngineProvider;
import info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.exception.SSLEngineProviderException;
import info.smart_tools.smartactors.https_endpoint.ssl_engine_provider.SslEngineProvider;
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
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Plugin for register http/https endpoint at IOC
 */
public class HttpsEndpointPlugin implements IPlugin {

    private IFieldName typeFieldName;
    private IFieldName portFieldName;
    private IFieldName startChainNameFieldName;
    private IFieldName stackDepthFieldName;
    private IFieldName maxContentLengthFieldName;
    private IFieldName endpointNameFieldName;
    private IFieldName queueFieldName;
    private IFieldName templatesFieldName;

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
//                    .after("IOC")
//                    .after("message_processor")
//                    .after("message_processing_sequence")
                    .after("response")
                    .after("response_content_strategy")
//                    .after("FieldNamePlugin")
//                    .before("starter")
                    .process(
                            () -> {
                                try {
                                    initializeFieldNames();
                                    IOC.register(Keys.getOrAdd(ISslEngineProvider.class.getCanonicalName()),
                                            new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        ISslEngineProvider sslContextProvider = new SslEngineProvider();
                                                        try {
                                                            if (args != null && args.length > 0) {
                                                                sslContextProvider.init((IObject) args[0]);
                                                            } else {
                                                                sslContextProvider.init(null);
                                                            }
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
                                    registerHttpsEndpoint();

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
                        "endpointName"
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


    private void registerHttpsEndpoint() throws InvalidArgumentException, RegistrationException, ResolutionException {
        IKey httpsEndpointKey = Keys.getOrAdd("https_endpoint");
        IOC.register(httpsEndpointKey,
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
                                        "HTTP_POST",
                                        configuration.getValue(endpointNameFieldName));

                                IEnvironmentHandler environmentHandler = IOC.resolve(
                                        Keys.getOrAdd(IEnvironmentHandler.class.getCanonicalName()),
                                        configuration);
                                ISslEngineProvider sslContextProvider =
                                        IOC.resolve(
                                                Keys.getOrAdd(ISslEngineProvider.class.getCanonicalName()),
                                                configuration
                                        );
                                HttpsEndpoint httpsEndpoint = new HttpsEndpoint((Integer) configuration.getValue(portFieldName),
                                        (Integer) configuration.getValue(maxContentLengthFieldName),
                                        ScopeProvider.getCurrentScope(), environmentHandler,
                                        (String) configuration.getValue(endpointNameFieldName),
                                        (IReceiverChain) configuration.getValue(startChainNameFieldName),
                                        sslContextProvider);
                                IOC.resolve(Keys.getOrAdd("endpoints"), configuration.getValue(endpointNameFieldName),
                                        httpsEndpoint);
                                return httpsEndpoint;
                            } catch (ReadValueException | ScopeProviderException | ResolutionException | InvalidArgumentException e) {
                            }
                            return null;
                        }
                )
        );
    }
}
