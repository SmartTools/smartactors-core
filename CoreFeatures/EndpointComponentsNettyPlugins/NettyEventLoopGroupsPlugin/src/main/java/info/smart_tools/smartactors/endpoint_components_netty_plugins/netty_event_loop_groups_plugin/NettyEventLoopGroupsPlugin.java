package info.smart_tools.smartactors.endpoint_components_netty_plugins.netty_event_loop_groups_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.synchronized_lazy_named_items_storage_strategy.SynchronizedLazyNamedItemsStorageStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.endpoint_components_netty.event_loops_configuration_section.EventLoopsConfigurationSectionStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Plugin that registers a storage for event loop groups and a configuration strategy to define new groups.
 */
public class NettyEventLoopGroupsPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public NettyEventLoopGroupsPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("netty_event_loop_group_storage")
    public void registerStorage()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IResolveDependencyStrategy storage = new SynchronizedLazyNamedItemsStorageStrategy();
        IOC.register(Keys.getOrAdd("netty event loop group"), storage);
        IOC.register(Keys.getOrAdd("expandable_strategy#netty event loop group"), new SingletonStrategy(storage));
    }

    @Item("netty_event_loop_configuration_strategy")
    public void registerConfigStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IConfigurationManager configurationManager = IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));
        configurationManager.addSectionStrategy(new EventLoopsConfigurationSectionStrategy());
    }
}
