package info.smart_tools.smartactors.https_endpoint_plugins.https_endpoint_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.endpoint.iendpoint_external_configuration_reader.IEndpointExternalConfigurationReader;
import info.smart_tools.smartactors.endpoint.iendpoint_external_configuration_reader.exception.EndpointExternalConfigurationReaderException;
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
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Plugin for register http/https endpoint at IOC
 */
public class HttpsEndpointPlugin implements IPlugin {

    private IFieldName portFieldName;
    private IFieldName startChainNameFieldName;
    private IFieldName stackDepthFieldName;
    private IFieldName maxContentLengthFieldName;
    private IFieldName endpointNameFieldName;
    private IFieldName queueFieldName;
    private IFieldName templatesFieldName;
    private IFieldName scopeSwitchingFieldName;
    private IFieldName externalConfigurationReaderFieldName;

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
            item.process(() -> {
                try {
                    initializeFieldNames();
                    IOC.register(Keys.getKeyByName(ISslEngineProvider.class.getCanonicalName()),
                            new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        ISslEngineProvider sslContextProvider = new SslEngineProvider();
                                        try {
                                            if (args != null && args.length > 0) {
                                                sslContextProvider.init((IObject) args[0]);
                                            } else {
                                                sslContextProvider.init(null);
                                            }
                                        } catch (SSLEngineProviderException ignored) {
                                        }
                                        return sslContextProvider;
                                    }
                            )
                    );

                    IOC.register(
                            Keys.getKeyByName(IEnvironmentHandler.class.getCanonicalName()),
                            new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        IObject configuration = (IObject) args[0];
                                        applyExternalConfiguration(configuration);
                                        IQueue queue;
                                        Integer stackDepth;
                                        Boolean scopeSwitching;

                                        try {
                                            queue = (IQueue) configuration.getValue(queueFieldName);
                                            stackDepth =
                                                    (Integer) configuration.getValue(stackDepthFieldName);
                                            scopeSwitching = (Boolean) configuration.getValue(scopeSwitchingFieldName);
                                            if (scopeSwitching == null) {
                                                scopeSwitching = true;
                                            }
                                            return new EnvironmentHandler(queue, stackDepth, scopeSwitching);
                                        } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            )
                    );
                    registerHttpsEndpoint();

                    IOC.register(Keys.getKeyByName(FileInputStream.class.getCanonicalName()),
                            new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new FileInputStream((String) args[0]);
                                        } catch (FileNotFoundException ignored) {
                                        }
                                        return null;
                                    }
                            ));
                    IKey emptyIObjectKey = Keys.getKeyByName("EmptyIObject");
                    IOC.register(emptyIObjectKey, new ApplyFunctionToArgumentsStrategy(
                                    (args) -> new DSObject()
                            )
                    );

                    IKey channelHandlerNettyKey = Keys.getKeyByName("info.smart_tools.smartactors.http_endpoint.channel_handler_netty.ChannelHandlerNetty");
                    IOC.register(channelHandlerNettyKey,
                            new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        ChannelHandlerNetty channelHandlerNetty = new ChannelHandlerNetty();
                                        channelHandlerNetty.init((ChannelHandlerContext) args[0]);
                                        return channelHandlerNetty;
                                    }
                            ));

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Can't load \"CreateHttpsEndpoint\" plugin", e);
        }
    }

    private void initializeFieldNames() throws ResolutionException {
        portFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        "port"
                );
        startChainNameFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        "startChain"
                );
        stackDepthFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        "stackDepth"
                );
        maxContentLengthFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        "maxContentLength"
                );
        endpointNameFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        "endpointName"
                );

        queueFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        "queue"
                );

        templatesFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        "templates"
                );

        scopeSwitchingFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        "scopeSwitching"
                );

        externalConfigurationReaderFieldName =
                IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        "externalConfigurationReader"
                );
    }


    private void registerHttpsEndpoint() throws InvalidArgumentException, RegistrationException, ResolutionException {
        IKey httpsEndpointKey = Keys.getKeyByName("https_endpoint");
        IOC.register(httpsEndpointKey,
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IObject configuration = (IObject) args[0];
                            applyExternalConfiguration(configuration);

                            try {
                                String endpointName = (String) configuration.getValue(endpointNameFieldName);

                                IOC.resolve(
                                        Keys.getKeyByName("info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy"),
                                        "HTTP_GET",
                                        endpointName,
                                        configuration.getValue(templatesFieldName)
                                );
                                IOC.resolve(
                                        Keys.getKeyByName("info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy"),
                                        "HTTP_POST",
                                        endpointName
                                );
                                IOC.register(
                                        Keys.getKeyByName(endpointName + "_endpoint-config"),
                                        new SingletonStrategy(configuration)
                                );

                                IEnvironmentHandler environmentHandler = IOC.resolve(
                                        Keys.getKeyByName(IEnvironmentHandler.class.getCanonicalName()),
                                        configuration);
                                ISslEngineProvider sslContextProvider =
                                        IOC.resolve(
                                                Keys.getKeyByName(ISslEngineProvider.class.getCanonicalName()),
                                                configuration
                                        );

                                IUpCounter upCounter = IOC.resolve(Keys.getKeyByName("root upcounter"));

                                HttpsEndpoint endpoint = new HttpsEndpoint(
                                        (Integer) configuration.getValue(portFieldName),
                                        (Integer) configuration.getValue(maxContentLengthFieldName),
                                        ScopeProvider.getCurrentScope(),
                                        ModuleManager.getCurrentModule(),
                                        environmentHandler,
                                        (String) configuration.getValue(endpointNameFieldName),
                                        (IReceiverChain) configuration.getValue(startChainNameFieldName),
                                        sslContextProvider, upCounter);

                                upCounter.onShutdownComplete(this.toString(), endpoint::stop);

                                return endpoint;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
    }

    private void applyExternalConfiguration(IObject defaultConfiguration) {
        try {
            String readerName = (String) defaultConfiguration.getValue(externalConfigurationReaderFieldName);
            if (readerName == null) {
                return;
            }

            IEndpointExternalConfigurationReader externalConfigurationReader = IOC.resolve(
                    Keys.getKeyByName(readerName),
                    defaultConfiguration
            );

            defaultConfiguration.setValue(
                    portFieldName,
                    Integer.parseInt(externalConfigurationReader.readOrDefault(
                            portFieldName.toString(),
                            String.valueOf(defaultConfiguration.getValue(portFieldName))
                    ))
            );
            defaultConfiguration.setValue(
                    startChainNameFieldName,
                    externalConfigurationReader.readOrDefault(
                            startChainNameFieldName.toString(),
                            defaultConfiguration.getValue(startChainNameFieldName)
                    )
            );
            defaultConfiguration.setValue(
                    stackDepthFieldName,
                    Integer.parseInt(externalConfigurationReader.readOrDefault(
                            stackDepthFieldName.toString(),
                            String.valueOf(defaultConfiguration.getValue(stackDepthFieldName))
                    ))
            );
            defaultConfiguration.setValue(
                    maxContentLengthFieldName,
                    Integer.parseInt(externalConfigurationReader.readOrDefault(
                            maxContentLengthFieldName.toString(),
                            String.valueOf(defaultConfiguration.getValue(maxContentLengthFieldName)))
                    )
            );
            defaultConfiguration.setValue(
                    endpointNameFieldName,
                    externalConfigurationReader.readOrDefault(
                            endpointNameFieldName.toString(),
                            defaultConfiguration.getValue(endpointNameFieldName)
                    )
            );
            defaultConfiguration.setValue(
                    scopeSwitchingFieldName,
                    Boolean.parseBoolean(externalConfigurationReader.readOrDefault(
                            scopeSwitchingFieldName.toString(),
                            String.valueOf(defaultConfiguration.getValue(scopeSwitchingFieldName))
                    ))
            );
        } catch (EndpointExternalConfigurationReaderException
                | ReadValueException
                | ChangeValueException
                | ResolutionException
                | InvalidArgumentException e) {
            throw new RuntimeException("Failed to apply external endpoint configuration", e);
        }
    }
}
