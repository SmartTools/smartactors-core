package info.smart_tools.smartactors.version_management_plugins.version_management_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.scope.iscope.exception.ScopeException;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.version_management.chain_version_manager.ChainIdFromMapNameStrategy;
import info.smart_tools.smartactors.version_management.versioned_recursive_strategy_container.StrategyContainer;
import info.smart_tools.smartactors.version_management.versioned_router_decorator.VersionedRouterDecorator;

import java.util.concurrent.ConcurrentHashMap;

public class VersionManagementPlugin  extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public VersionManagementPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("versioned_chain_id_from_map_name_strategy")
    @After({"messaging_identifiers"})
    @Before({"starter"})
    public void registerChainIdFromMapNameStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {

        ChainIdFromMapNameStrategy strategy = new ChainIdFromMapNameStrategy();

        IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), strategy.getResolveByMessageStrategy());
        IOC.register(Keys.getKeyByName("chain_id_from_map_name"), strategy.getResolveByModuleDependenciesStrategy());
        IOC.register(Keys.getKeyByName("register_message_version_strategy"), strategy.getRegisterMessageVersionStrategy());
    }

    @Item("versioned_router")
    @After({"router"})
    @Before({"receiver_chains_storage"})
    public void registerVersionedRouterStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {

        // alternative 1: realization using decorator pattern
        IRouter router = IOC.resolve(Keys.getKeyByName(IRouter.class.getCanonicalName()));
        IOC.register(
                Keys.getKeyByName(IRouter.class.getCanonicalName()),
                new SingletonStrategy(new VersionedRouterDecorator(new ConcurrentHashMap<>(), router))
        );
        /*
        // alternative 2: more fast realization
        IOC.register(
                Keys.getKeyByName(IRouter.class.getCanonicalName()),
                new SingletonStrategy(new VersionedMapRouter(new ConcurrentHashMap<>()))
        );
        */

    }

    @Item("versioned_strategy_container_for_scope")
    @After({"subscribe_ioc_for_scope_creation"})
    @Before({"create_system_scope"})
    public void registerVersionedStrategyContainerForScope()
            throws ActionExecutionException {
        try {
            ScopeProvider.clearListOfSubscribers();
            ScopeProvider.subscribeOnCreationNewScope(scope -> {
                try {
                    // alternative 1: strategy for recursive strategy container
                    IStrategyContainer parentContainer = null;
                    try {
                        // container undefined in new scope so get it from parent scope recursively
                        parentContainer = (IStrategyContainer) scope.getValue(IOC.getIocKey());
                    } catch (ScopeException e) {
                        // parent container does not exists, create a new with null parent
                    }
                    scope.setValue(IOC.getIocKey(), new StrategyContainer(parentContainer));
                    /*
                    // alternative 2: strategy for plane strategy container (
                    scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    */
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (ScopeProviderException e) {
            throw new ActionExecutionException("ScopedIOC plugin can't load.", e);
        }
    }

    // ToDo: fix rollback to setup previous strategies
    @ItemRevert("versioned_chain_id_from_map_name_strategy")
    public void unregisterChainIdFromMapNameStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        String[] itemNames = {  "register_message_version_strategy",
                                "chain_id_from_map_name",
                                "chain_id_from_map_name_and_message"  };
        Keys.unregisterByNames(itemNames);
    }

    // ToDo: fix rollback to setup previous strategies
    @ItemRevert("versioned_router")
    public void unregisterVersionedRouterStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        String[] itemNames = { IRouter.class.getCanonicalName() };
        Keys.unregisterByNames(itemNames);
    }

    @ItemRevert("versioned_strategy_container_for_scope")
    public void unregisterVersionedStrategyContainerForScope()
            throws ActionExecutionException {
        // nothing to do - we cannot unregister subscription on new scope creation
    }

}