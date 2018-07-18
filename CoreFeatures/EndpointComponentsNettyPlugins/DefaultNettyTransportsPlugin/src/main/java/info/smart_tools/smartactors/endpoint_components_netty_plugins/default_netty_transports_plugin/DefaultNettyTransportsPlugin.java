package info.smart_tools.smartactors.endpoint_components_netty_plugins.default_netty_transports_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.base.simple_strict_storage_strategy.SimpleStrictStorageStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.strategy.strategy_storage_strategy.StrategyStorageStrategy;
import info.smart_tools.smartactors.endpoint_components_netty.default_transport_providers.EpollTransportProvider;
import info.smart_tools.smartactors.endpoint_components_netty.default_transport_providers.NioTransportProvider;
import info.smart_tools.smartactors.endpoint_components_netty.default_transport_providers.OioTransportProvider;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Map;

public class DefaultNettyTransportsPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public DefaultNettyTransportsPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("netty_transport_providers_storage")
    public void registerTransportStorage()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        SimpleStrictStorageStrategy storage = new SimpleStrictStorageStrategy("netty transport provider");
        IOC.register(Keys.getOrAdd("netty transport provider"), storage);
        IOC.register(Keys.getOrAdd("expandable_strategy#netty transport provider"), new SingletonStrategy(storage));
    }

    @Item("netty_default_transport_providers")
    @After({
            "netty_transport_providers_storage",
    })
    public void registerDefaultTransports()
            throws ResolutionException, RegistrationException, InvalidArgumentException, AdditionDependencyStrategyException {
        IAdditionDependencyStrategy storage = IOC.resolve(Keys.getOrAdd("expandable_strategy#netty transport provider"));

        Object nioTransport = new NioTransportProvider();
        Object oioTransport = new OioTransportProvider();

        storage.register("nio",
                new SingletonStrategy(nioTransport));
        storage.register("prefer-native",
                new SingletonStrategy(nioTransport));
        storage.register("blocking",
                new SingletonStrategy(oioTransport));
        storage.register("force-native",
                new IResolveDependencyStrategy() {
                    @Override
                    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
                        throw new ResolveDependencyStrategyException("No native netty transport provider registered.");
                    }
                });
    }

    @Item("netty_native_linux_transport_provider")
    @After({
            "netty_default_transport_providers",
    })
    public void registerLinuxNativeTransport()
            throws ResolutionException, RegistrationException, InvalidArgumentException, AdditionDependencyStrategyException {
        if (!System.getProperty("os.name").toLowerCase().trim().startsWith("linux")) {
            System.out.println("OS (" + System.getProperty("os.name") + ") doesn't seem to be a Linux. Ignoring epoll transport.");
            return;
        }

        IAdditionDependencyStrategy storage = IOC.resolve(Keys.getOrAdd("expandable_strategy#netty transport provider"));

        Object epollTransport = new EpollTransportProvider();

        storage.register("force-native",
                new SingletonStrategy(epollTransport));
        storage.register("prefer-native",
                new SingletonStrategy(epollTransport));
    }
}
