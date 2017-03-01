package info.smart_tools.smartactors.https_endopint_plugins.https_client_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.https_endpoint.https_client.HttpsClient;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.exception.RequestSenderException;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.IResponseHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.exception.ResponseHandlerException;
import info.smart_tools.smartactors.endpoint.irequest_maker.IRequestMaker;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.http_endpoint.http_client_initializer.HttpClientInitializer;
import info.smart_tools.smartactors.http_endpoint.http_request_maker.HttpRequestMaker;
import info.smart_tools.smartactors.http_endpoint.http_response_deserialization_strategy.HttpResponseDeserializationStrategy;
import info.smart_tools.smartactors.http_endpoint.http_response_handler.HttpResponseHandler;
import info.smart_tools.smartactors.http_endpoint.message_to_bytes_mapper.MessageToBytesMapper;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by sevenbits on 15.10.16.
 */
public class HttpsClientPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    private IFieldName uriFieldName;
    private IFieldName startChainNameFieldName,
            queueFieldName,
            stackDepthFieldName,
            exceptionalMessageMapId;

    public HttpsClientPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("CreateHttpsClient");
            item
//                    .after("IOC")
//                    .after("message_processor")
//                    .after("message_processing_sequence")
//                    .after("response")
//                    .after("response_content_strategy")
//                    .after("FieldNamePlugin")
//                    .before("starter")
                    .process(
                            () -> {
                                try {
                                    registerFieldNames();
                                    IOC.register(Keys.getOrAdd(URI.class.getCanonicalName()), new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        try {
                                                            return new URI((String) args[0]);
                                                        } catch (URISyntaxException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            )
                                    );
                                    IMessageMapper<byte[]> messageMapper = new MessageToBytesMapper();

                                    IDeserializeStrategy deserializeStrategy = new HttpResponseDeserializationStrategy(messageMapper);

                                    IOC.register(Keys.getOrAdd("httpResponseResolver"), new SingletonStrategy(
                                                    deserializeStrategy
                                            )
                                    );

                                    IOC.register(Keys.getOrAdd("EmptyIObject"), new CreateNewInstanceStrategy(
                                                    (args) -> new DSObject()
                                            )
                                    );
                                    IOC.register(Keys.getOrAdd(IResponseHandler.class.getCanonicalName()), new CreateNewInstanceStrategy(
                                                    (args) -> {
                                                        try {
                                                            IObject configuration = IOC.resolve(Keys.getOrAdd("responseHandlerConfiguration"));
                                                            IObject request = (IObject) args[0];
                                                            IResponseHandler responseHandler = new HttpResponseHandler(
                                                                    (IQueue<ITask>) configuration.getValue(queueFieldName),
                                                                    (Integer) configuration.getValue(stackDepthFieldName),
                                                                    request.getValue(startChainNameFieldName),
                                                                    request,
                                                                    ScopeProvider.getCurrentScope()
                                                            );
                                                            return responseHandler;
                                                        } catch (ResponseHandlerException | ResolutionException |
                                                                ReadValueException | InvalidArgumentException |
                                                                ScopeProviderException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            )
                                    );
                                    IRequestMaker<FullHttpRequest> requestMaker = new HttpRequestMaker();
                                    IOC.register(Keys.getOrAdd(IRequestMaker.class.getCanonicalName()), new SingletonStrategy(
                                                    requestMaker
                                            )
                                    );
                                    IOC.register(Keys.getOrAdd(MessageToBytesMapper.class.getCanonicalName()),
                                            new SingletonStrategy(
                                                    messageMapper
                                            )
                                    );

                                    IOC.register(Keys.getOrAdd("sendHttpsRequest"), new ApplyFunctionToArgumentsStrategy(
                                                    (args) -> {
                                                        try {
                                                            HttpsClient client = (HttpsClient) args[0];
                                                            IObject request = (IObject) args[1];
                                                            client.sendRequest(request);
                                                            IOC.resolve(
                                                                    Keys.getOrAdd("createTimerOnRequest"),
                                                                    request,
                                                                    request.getValue(exceptionalMessageMapId)
                                                            );
                                                            return client;
                                                        } catch (ResolutionException | RequestSenderException | ReadValueException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            )
                                    );

                                    IOC.register(Keys.getOrAdd("getHttpsClient"), new ApplyFunctionToArgumentsStrategy(
                                                    (args) -> {
                                                        IObject request = (IObject) args[0];
                                                        try {
                                                            IResponseHandler responseHandler = IOC.resolve(
                                                                    Keys.getOrAdd(IResponseHandler.class.getCanonicalName()),
                                                                    request
                                                            );
                                                            HttpsClient client =
                                                                    new HttpsClient(
                                                                            URI.create((String) request.getValue(uriFieldName)),
                                                                            responseHandler
                                                                    );
                                                            return client;
                                                        } catch (ReadValueException | ResolutionException | RequestSenderException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            )
                                    );
                                    HttpClientInitializer.init();
                                } catch (RegistrationException | ResolutionException | InvalidArgumentException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                            }
                    );
            bootstrap.add(item);

        } catch (Exception e) {
            throw new PluginException(e);
        }

    }

    private void registerFieldNames() throws ResolutionException {
        this.uriFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "uri");
        this.startChainNameFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageMapId");
        this.queueFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "queue");
        this.stackDepthFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "stackDepth");
        this.exceptionalMessageMapId = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "exceptionalMessageMapId");
    }
}
