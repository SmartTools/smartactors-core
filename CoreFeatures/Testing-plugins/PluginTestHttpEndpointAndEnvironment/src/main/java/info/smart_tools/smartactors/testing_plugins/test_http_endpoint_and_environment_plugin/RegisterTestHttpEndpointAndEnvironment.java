package info.smart_tools.smartactors.testing_plugins.test_http_endpoint_and_environment_plugin;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.iasync_service.IAsyncService;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.testing.interfaces.isource.ISource;
import info.smart_tools.smartactors.testing.test_data_source_iobject.IObjectDataSource;
import info.smart_tools.smartactors.testing.test_http_endpoint.TestChannelHandler;
import info.smart_tools.smartactors.testing.test_http_endpoint.TestHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Plugin register creation strategies for {@link TestHttpEndpoint}, {@link TestChannelHandler} into IOC.
 */
public class RegisterTestHttpEndpointAndEnvironment implements IPlugin {

    private static final long TIME_BETWEEN_TESTS = 1000L;

    /** Local storage for instance of {@link IBootstrap}*/
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor with single argument
     * @param bootstrap instance of {@link IBootstrap}
     * @throws InvalidArgumentException if any errors occurred
     */
    public RegisterTestHttpEndpointAndEnvironment(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("RegisterTestHttpEndpoint");
            item
//                    .after("IOC")
//                    .after("IFieldNamePlugin")
//                    .after("iobject")
                    .after("test environment handler")
                    .before("chain tests runner")
                    .process(
                            () -> {
                                try {
                                    // Creates and registers test data source
                                    ISource<IObject, IObject> source = new IObjectDataSource();
                                    IOC.register(
                                            IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "test_data_source"),
                                            new SingletonStrategy(source)
                                    );

                                    // Creates and register object with test responses
                                    List<Object> responses = new ArrayList<Object>();
                                    IOC.register(
                                            IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "test_responses"),
                                            new SingletonStrategy(responses)
                                    );

                                    // Creates and registers test channel handler
                                    IChannelHandler channelHandler = new TestChannelHandler(responses);
                                    IOC.register(
                                            IOC.resolve(IOC.getKeyForKeyByNameStrategy(), TestChannelHandler.class.getCanonicalName()),
                                            new SingletonStrategy(channelHandler)
                                    );

                                    // Creates and registers test http endpoint
                                    IEnvironmentHandler handler = IOC.resolve(
                                            IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "test environment handler")
                                    );
                                    IAsyncService endpoint = new TestHttpEndpoint(
                                            source,
                                            ScopeProvider.getCurrentScope(),
                                            handler,
                                            TIME_BETWEEN_TESTS,
                                            null
                                    );
                                    IOC.register(
                                            IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "test_http_endpoint"),
                                            new SingletonStrategy(endpoint)
                                    );
                                    endpoint.start();
                                } catch (ScopeProviderException e) {
                                    throw new ActionExecutionException("RegisterTestHttpEndpoint plugin can't load: current scope is undefined.", e);
                                } catch (ResolutionException e) {
                                    throw new ActionExecutionException("RegisterTestHttpEndpoint plugin can't load: can't get keys for testing objects", e);
                                } catch (InvalidArgumentException e) {
                                    throw new ActionExecutionException("RegisterTestHttpEndpoint plugin can't load: can't create strategy", e);
                                } catch (RegistrationException e) {
                                    throw new ActionExecutionException("RegisterTestHttpEndpoint plugin can't load: can't register new strategy", e);
                                } catch (InitializationException e) {
                                    throw new ActionExecutionException("RegisterTestHttpEndpoint plugin can't load: can't create instance of TestHttpEndpoint", e);
                                }
                            }
                        );
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Could not load 'RegisterTestHttpEndpoint plugin'", e);
        }
    }
}
